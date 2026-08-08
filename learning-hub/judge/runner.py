#!/usr/bin/env python3
"""Local DSA online-judge runner (standard library only).

Given a *manifest* (JSON describing a problem + its test cases) and a *submission*
(a Python source file), this script:

  * mode="run"        -> executes the submission against every test case and reports
                         per-test pass/fail, timing and the captured output.
  * mode="complexity" -> runs the submission across increasing input sizes and
                         *empirically estimates* time and space complexity by
                         curve-fitting the measurements to common Big-O classes.

It is intentionally dependency-free (only the Python standard library) so it can run
locally on any machine with a Python 3 interpreter, and it emits a single JSON object
on stdout so the Spring Boot backend can parse it.

IMPORTANT: empirical complexity is an *estimate* derived from wall-clock measurements,
not a static proof. Big-O cannot be decided statically in general; we report the best
fit and a confidence score.

Usage:
    python runner.py --manifest <path> --submission <path> --mode run
    python runner.py --manifest <path> --submission <path> --mode complexity
"""

from __future__ import annotations

import argparse
import gc
import io
import json
import random
import sys
import time
import tracemalloc
import traceback
from contextlib import redirect_stdout


# --------------------------------------------------------------------------------------
# Safe execution of user / reference code
# --------------------------------------------------------------------------------------
def _compile_namespace(source: str, helper_code: str = ""):
    """Exec `source` (optionally prefixed with helper class defs such as TreeNode/ListNode)
    in a fresh module namespace and return it.

    Raises on syntax / import-time errors so the caller can report them cleanly.
    """
    ns: dict = {"__name__": "__submission__"}
    full = (helper_code + "\n\n" + source) if helper_code else source
    exec(compile(full, "<submission>", "exec"), ns)
    return ns


_HELPER_CLASS_NAMES = {"TreeNode", "ListNode", "Node", "GraphNode",
                       "DLinkedNode", "DoublyLinkedNode", "DListNode"}


def _find_solution_class(ns: dict, class_name: str, entry: str = None):
    """Locate the logic class in a namespace, tolerant of naming variants.

    Reference solutions sometimes name the class SolutionRecursive / SolutionMemoized
    instead of Solution, so we fall back to (a) any class defining `entry`, then
    (b) any class whose name starts with 'Solution', skipping helper node classes.
    """
    cls = ns.get(class_name)
    if cls is not None and (entry is None or hasattr(cls, entry)):
        return cls
    import inspect as _inspect
    classes = [v for v in ns.values() if _inspect.isclass(v)
               and getattr(v, "__module__", None) == "__submission__"]
    if entry:
        for c in classes:
            if c.__name__ not in _HELPER_CLASS_NAMES and hasattr(c, entry):
                return c
    for c in classes:
        if c.__name__.startswith("Solution"):
            return c
    if cls is not None:
        return cls
    raise NameError(f"class '{class_name}' was not found in the submission")


# --------------------------------------------------------------------------------------
# Tree / linked-list adapters (LeetCode array <-> structure)
# --------------------------------------------------------------------------------------
def _build_tree(arr, TreeNodeCls):
    """Level-order array (with None for missing children) -> TreeNode root."""
    if not arr:
        return None
    from collections import deque
    it = iter(arr)
    root = TreeNodeCls(next(it))
    q = deque([root])
    while q:
        node = q.popleft()
        try:
            lv = next(it)
        except StopIteration:
            break
        if lv is not None:
            node.left = TreeNodeCls(lv)
            q.append(node.left)
        try:
            rv = next(it)
        except StopIteration:
            break
        if rv is not None:
            node.right = TreeNodeCls(rv)
            q.append(node.right)
    return root


def _serialize_tree(root):
    """TreeNode root -> level-order array with trailing None trimmed."""
    if root is None:
        return []
    from collections import deque
    out = []
    q = deque([root])
    while q:
        node = q.popleft()
        if node is None:
            out.append(None)
            continue
        out.append(node.val)
        q.append(node.left)
        q.append(node.right)
    while out and out[-1] is None:
        out.pop()
    return out


def _build_list(arr, ListNodeCls):
    """Array -> singly linked list; returns head."""
    head = None
    for v in reversed(arr or []):
        node = ListNodeCls(v)
        node.next = head
        head = node
    return head


def _serialize_list(head):
    """Singly linked list -> array (guards against cycles)."""
    out = []
    seen = set()
    node = head
    while node is not None:
        if id(node) in seen:
            break
        seen.add(id(node))
        out.append(node.val)
        node = node.next
    return out


def _convert_arg(value, kind, ns):
    if kind == "tree":
        return _build_tree(value, ns["TreeNode"])
    if kind == "linkedlist":
        return _build_list(value, ns["ListNode"])
    return value


def _convert_result(result, ns):
    """Serialize a returned TreeNode/ListNode back to a plain array (auto-detected)."""
    tree_cls = ns.get("TreeNode")
    list_cls = ns.get("ListNode")
    if tree_cls is not None and isinstance(result, tree_cls):
        return _serialize_tree(result)
    if list_cls is not None and isinstance(result, list_cls):
        return _serialize_list(result)
    return result


# --------------------------------------------------------------------------------------
# Output comparison
# --------------------------------------------------------------------------------------
def _canon(value):
    """Canonicalise nested lists/tuples to tuples so they hash and compare cleanly."""
    if isinstance(value, (list, tuple)):
        return tuple(_canon(v) for v in value)
    return value


def _compare(expected, got, mode: str, tol: float = 1e-6) -> bool:
    """Return True if `got` matches `expected` under the given comparison mode."""
    # Common, harmless convention: a function that "finds nothing" may return either
    # None or an empty list. Treat those as equivalent so a valid solution isn't failed
    # on that stylistic choice (never equates None with a non-empty value or 0/False).
    def _empty(v):
        return v is None or (isinstance(v, (list, tuple)) and len(v) == 0)
    if _empty(expected) and _empty(got):
        return True

    if mode == "exact":
        return got == expected
    if mode == "bool":
        return bool(got) == bool(expected)
    if mode == "float":
        try:
            return abs(float(got) - float(expected)) <= tol
        except (TypeError, ValueError):
            return False
    if mode == "sorted":
        # Order-independent comparison of a flat sequence.
        try:
            return sorted(got) == sorted(expected)
        except TypeError:
            return got == expected
    if mode == "set":
        try:
            return set(_canon(got)) == set(_canon(expected))
        except TypeError:
            return got == expected
    if mode == "set2d":
        # List of lists where inner order and outer order are both irrelevant
        # (e.g. 3Sum, Group Anagrams). Each inner list is sorted, outer is a set.
        try:
            e = {tuple(sorted(x)) for x in expected}
            g = {tuple(sorted(x)) for x in got}
            return e == g
        except TypeError:
            return got == expected
    if mode == "multiset2d":
        # Like set2d but preserves inner order (grouping problems where inner order
        # matters but outer order does not).
        try:
            e = sorted(tuple(x) for x in expected)
            g = sorted(tuple(x) for x in got)
            return e == g
        except TypeError:
            return got == expected
    # Fallback.
    return got == expected


def _build_validator(validator_code: str):
    """Compile an optional per-problem validator.

    The manifest may supply Python source defining `def validate(args, output): ...`
    returning a bool. This is used for problems with multiple valid answers
    (e.g. Two Sum returns *any* valid index pair).
    """
    if not validator_code:
        return None
    ns: dict = {}
    exec(compile(validator_code, "<validator>", "exec"), ns)
    fn = ns.get("validate")
    if fn is None:
        raise NameError("validatorCode did not define a 'validate' function")
    return fn


# --------------------------------------------------------------------------------------
# Running a single test case
# --------------------------------------------------------------------------------------
def _run_function_case(cls, entry: str, args, arg_kinds=None, ns=None):
    """Instantiate the class and call the entry method with positional args.

    If arg_kinds/ns are provided, array arguments flagged tree/linkedlist are converted
    to the corresponding structure first, and a returned TreeNode/ListNode is serialized
    back to a plain array.
    """
    instance = cls()
    method = getattr(instance, entry)
    # Deep-copy args so a mutating solution cannot corrupt the stored expected value
    # or the next test case.
    call_args = json.loads(json.dumps(args))
    if arg_kinds and ns is not None:
        call_args = [_convert_arg(v, arg_kinds[i] if i < len(arg_kinds) else "plain", ns)
                     for i, v in enumerate(call_args)]
    result = method(*call_args)
    if ns is not None:
        result = _convert_result(result, ns)
    return result


def _run_design_case(cls, ops, args_list):
    """Drive a design problem in the LeetCode operation-sequence style.

    ops       -> ["LRUCache", "put", "get", ...]
    args_list -> [[2], [1, 1], [1], ...]
    Returns the list of outputs (constructor slot is None/null).
    """
    outputs = []
    instance = None
    for op, a in zip(ops, args_list):
        a = json.loads(json.dumps(a))
        if instance is None:
            # First op is always the constructor: op == class name.
            instance = cls(*a)
            outputs.append(None)
        else:
            method = getattr(instance, op)
            outputs.append(method(*a))
    return outputs


def mode_run(manifest: dict, source: str) -> dict:
    shape = manifest.get("shape", "function")
    class_name = manifest.get("className", "Solution")
    entry = manifest.get("entry")
    compare_mode = manifest.get("compareMode", "exact")
    helper_code = manifest.get("helperCode", "")
    arg_kinds = manifest.get("argKinds")

    try:
        ns = _compile_namespace(source, helper_code)
    except Exception:
        return {
            "ok": False,
            "compileError": traceback.format_exc(limit=3),
            "results": [],
            "summary": {"passed": 0, "total": 0},
        }

    try:
        cls = _find_solution_class(ns, class_name, entry)
    except Exception as exc:
        return {
            "ok": False,
            "compileError": str(exc),
            "results": [],
            "summary": {"passed": 0, "total": 0},
        }

    in_place_arg = manifest.get("inPlaceArg")
    results = []
    passed = 0
    tests = manifest.get("tests", [])
    for tc in tests:
        entry_result = {"id": tc.get("id"), "kind": tc.get("kind", "test")}
        buf = io.StringIO()
        try:
            t0 = time.perf_counter()
            with redirect_stdout(buf):
                if shape == "design":
                    got = _run_design_case(cls, tc["ops"], tc["args"])
                elif shape == "codec":
                    # Round-trip codec: decode(encode(x)) must equal the original input.
                    instance = cls()
                    call_args = json.loads(json.dumps(tc["args"]))
                    encoded = instance.encode(*call_args)
                    got = instance.decode(encoded)
                elif in_place_arg is not None:
                    # In-place problem: the solution mutates an argument and returns None.
                    instance = cls()
                    method = getattr(instance, entry)
                    call_args = json.loads(json.dumps(tc["args"]))
                    if arg_kinds and ns is not None:
                        call_args = [_convert_arg(v, arg_kinds[i] if i < len(arg_kinds) else "plain", ns)
                                     for i, v in enumerate(call_args)]
                    method(*call_args)
                    got = call_args[in_place_arg]
                    if ns is not None:
                        got = _convert_result(got, ns)
                else:
                    got = _run_function_case(cls, entry, tc["args"], arg_kinds, ns)
            elapsed_ms = (time.perf_counter() - t0) * 1000.0

            validator = _build_validator(tc.get("validatorCode") or manifest.get("validatorCode"))
            if validator is not None:
                ok = bool(validator(tc.get("args"), got))
            else:
                ok = _compare(tc.get("expected"), got, compare_mode)

            entry_result.update({
                "passed": ok,
                "timeMs": round(elapsed_ms, 4),
                "got": got,
                "expected": tc.get("expected"),
            })
            if ok:
                passed += 1
        except Exception:
            entry_result.update({
                "passed": False,
                "error": traceback.format_exc(limit=4),
                "expected": tc.get("expected"),
            })
        # Surface anything the user's code printed (e.g. debug statements) so the UI can
        # show a console panel. Captured whether the case passed or raised.
        out_text = buf.getvalue()
        if out_text:
            entry_result["stdout"] = out_text[:4000] + ("\n…(truncated)" if len(out_text) > 4000 else "")
        results.append(entry_result)

    return {
        "ok": True,
        "results": results,
        "summary": {
            "passed": passed,
            "total": len(tests),
            "allPassed": passed == len(tests) and len(tests) > 0,
            "totalTimeMs": round(sum(r.get("timeMs", 0.0) for r in results), 4),
        },
    }


# --------------------------------------------------------------------------------------
# Empirical complexity estimation
# --------------------------------------------------------------------------------------
COMPLEXITY_CLASSES = [
    ("O(1)", lambda n: 1.0),
    ("O(log n)", lambda n: max(1.0, __import__("math").log2(n))),
    ("O(n)", lambda n: float(n)),
    ("O(n log n)", lambda n: n * max(1.0, __import__("math").log2(n))),
    ("O(n^2)", lambda n: float(n) * n),
    ("O(n^3)", lambda n: float(n) * n * n),
]


def _fit_complexity(sizes, measures):
    """Pick the Big-O class whose shape best matches the measurements.

    For each candidate g(n) we compute the ratios measure/g(n); a perfect match
    yields a constant ratio, so we score by the coefficient of variation (std/mean)
    of those ratios. Lowest score wins. Returns (label, confidence in [0,1]).
    """
    import math

    # Filter out non-positive / degenerate measurements.
    pts = [(n, m) for n, m in zip(sizes, measures) if n > 0 and m > 0]
    if len(pts) < 3:
        return "insufficient-data", 0.0

    best_label = "unknown"
    best_score = float("inf")
    scores = {}
    for label, g in COMPLEXITY_CLASSES:
        ratios = [m / g(n) for n, m in pts]
        mean = sum(ratios) / len(ratios)
        if mean == 0:
            continue
        var = sum((r - mean) ** 2 for r in ratios) / len(ratios)
        cv = math.sqrt(var) / mean  # coefficient of variation
        scores[label] = cv
        if cv < best_score:
            best_score = cv
            best_label = label

    # Confidence: how much better the best fit is than the runner-up.
    ordered = sorted(scores.values())
    if len(ordered) >= 2 and ordered[1] > 0:
        confidence = max(0.0, min(1.0, 1.0 - (ordered[0] / ordered[1])))
    else:
        confidence = 0.5
    # A very low absolute CV is itself strong evidence.
    confidence = max(confidence, max(0.0, 1.0 - best_score))
    return best_label, round(confidence, 3)


def _make_input(gen_spec: dict, n: int, rng: random.Random):
    """Build a call-argument list of "size n" from a generator spec.

    Each param spec: {name, type, role, lo, hi, ...}
      type: int | float | str | list[int] | list[str] | list[list[int]]
      role: "size"   -> the collection whose length == n (drives complexity)
            "const"  -> a fixed value taken from `value`
            "n"      -> pass n itself as an int
            "target" -> an int derived to be a plausible target
    """
    args = []
    for p in gen_spec.get("params", []):
        role = p.get("role", "const")
        typ = p.get("type", "int")
        lo = p.get("lo", -1000)
        hi = p.get("hi", 1000)
        if role == "const":
            args.append(p.get("value"))
        elif role == "n":
            args.append(n)
        elif role == "target":
            args.append(rng.randint(lo, hi))
        elif role == "size":
            if typ == "list[int]":
                args.append([rng.randint(lo, hi) for _ in range(n)])
            elif typ == "list[str]":
                args.append(["".join(rng.choice("abcde") for _ in range(3)) for _ in range(n)])
            elif typ == "list[list[int]]":
                w = p.get("width", 3)
                args.append([[rng.randint(lo, hi) for _ in range(w)] for _ in range(n)])
            elif typ == "str":
                args.append("".join(rng.choice("abcde") for _ in range(n)))
            else:
                args.append([rng.randint(lo, hi) for _ in range(n)])
        else:
            args.append(p.get("value"))
    return args


def mode_complexity(manifest: dict, source: str) -> dict:
    comp = manifest.get("complexity", {})
    if not comp.get("supported"):
        return {"supported": False, "reason": comp.get("reason", "not configured for this problem")}

    shape = manifest.get("shape", "function")
    if shape in ("design", "codec"):
        return {"supported": False, "reason": f"complexity estimation is not applicable to {shape} problems"}

    class_name = manifest.get("className", "Solution")
    entry = manifest.get("entry")
    helper_code = manifest.get("helperCode", "")
    try:
        ns = _compile_namespace(source, helper_code)
        cls = _find_solution_class(ns, class_name, entry)
    except Exception:
        return {"supported": True, "error": traceback.format_exc(limit=3)}

    sizes = comp.get("sizes", [1000, 2000, 4000, 8000])
    gen_spec = comp.get("genSpec", {})
    repeats = comp.get("repeats", 3)
    rng = random.Random(1234567)

    times = []
    mems = []
    samples = []
    for n in sizes:
        best_t = float("inf")
        peak_mem = 0
        for _ in range(repeats):
            args = _make_input(gen_spec, n, rng)
            call_args = json.loads(json.dumps(args))
            instance = cls()
            method = getattr(instance, entry)
            gc.collect()
            tracemalloc.start()
            t0 = time.perf_counter()
            try:
                with redirect_stdout(io.StringIO()):
                    method(*call_args)
            except Exception:
                tracemalloc.stop()
                return {"supported": True, "error": traceback.format_exc(limit=4)}
            dt = time.perf_counter() - t0
            _cur, pk = tracemalloc.get_traced_memory()
            tracemalloc.stop()
            best_t = min(best_t, dt)
            peak_mem = max(peak_mem, pk)
        times.append(best_t)
        mems.append(peak_mem)
        samples.append({"n": n, "timeMs": round(best_t * 1000.0, 4), "peakBytes": peak_mem})

    time_label, time_conf = _fit_complexity(sizes, times)
    space_label, space_conf = _fit_complexity(sizes, mems)
    return {
        "supported": True,
        "timeComplexity": time_label,
        "timeConfidence": time_conf,
        "spaceComplexity": space_label,
        "spaceConfidence": space_conf,
        "samples": samples,
        "note": "Estimated empirically from measured runtime/memory at increasing input sizes.",
    }


# --------------------------------------------------------------------------------------
# Entry point
# --------------------------------------------------------------------------------------
def _install_sandbox():
    """Harden execution of untrusted submissions with a Python audit hook (PEP 578).

    This is installed just before any user/reference code runs. It blocks the high-risk
    operations that a graded algorithm never needs — network access, spawning external
    processes, and loading native libraries — so a malicious submission cannot exfiltrate
    data or escape the process. Pure computation (imports of math/collections/heapq/etc.,
    file reads done by the runner itself) is unaffected. Audit hooks cannot be removed
    once added, which is exactly what we want for a one-shot grading process.
    """
    def _hook(event, args):
        if (event.startswith("socket.")
                or event.startswith("subprocess.")
                or event.startswith("ctypes.")
                or event in ("os.system", "os.exec", "os.spawn", "os.posix_spawn",
                             "os.fork", "os.forkpty", "os.startfile", "os.putenv")):
            raise PermissionError(
                "sandbox: operation '%s' is not permitted in the judge" % event)
    sys.addaudithook(_hook)


def main(argv=None):
    parser = argparse.ArgumentParser(description="Local DSA judge runner")
    parser.add_argument("--manifest", required=True)
    parser.add_argument("--submission", required=True)
    parser.add_argument("--mode", choices=["run", "complexity"], default="run")
    args = parser.parse_args(argv)

    with open(args.manifest, encoding="utf-8") as f:
        manifest = json.load(f)
    with open(args.submission, encoding="utf-8") as f:
        source = f.read()

    # Everything below runs untrusted code — turn on the sandbox now that the manifest
    # and submission files have already been read from disk.
    _install_sandbox()

    if args.mode == "complexity":
        out = mode_complexity(manifest, source)
    else:
        out = mode_run(manifest, source)

    sys.stdout.write(json.dumps(out))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
