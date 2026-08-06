#!/usr/bin/env python3
"""Manifest generation library for the local DSA judge (standard library only).

`genlib` turns a problem's Markdown file (which contains three reference solutions and
a worked example) into a *judge manifest* that `runner.py` can execute against.

The design guarantees quality via a **self-check**: a manifest is only accepted if all
three reference solutions (naive / better / optimal) pass every test it contains. The
optimal solution is used as the *oracle* that defines the expected output for
auto-generated random tests, and the comparison mode is escalated (exact -> sorted ->
set -> set2d ...) until all three solutions agree.
"""

from __future__ import annotations

import ast
import json
import re
import random
import threading

# runner is a sibling module; importing it lets us reuse the exact execution/compare
# logic so "self-check passes" means "the real judge will pass".
import runner


SOLUTION_HEADERS = [
    ("naive", re.compile(r"Approach 1[^\n]*\n", re.I)),
    ("better", re.compile(r"Approach 2[^\n]*\n", re.I)),
    ("optimal", re.compile(r"Approach 3[^\n]*\n", re.I)),
]

_PY_BLOCK = re.compile(r"```python\s*\n(.*?)```", re.DOTALL)
_FENCE_TEXT = re.compile(r"```text\s*\n(.*?)```", re.DOTALL)


def _lit(s):
    """Tolerant literal parser: understands JSON (null/true/false) and Python literals."""
    s = s.strip()
    try:
        return json.loads(s)
    except Exception:
        return ast.literal_eval(s)


# Hand-authored overrides for problems whose example/answer shape defeats generic
# parsing (randomised output, non-standard example prose, etc.). Each override is a
# patch merged onto the auto-built manifest.
OVERRIDES = {
    "arrays-hashing/13-insert-delete-getrandom-o1": {
        "shape": "design",
        "compareMode": "exact",
        "tests": [{
            "id": "example-1", "kind": "example",
            "ops": ["RandomizedSet", "insert", "remove", "insert", "getRandom",
                    "remove", "insert", "getRandom"],
            "args": [[], [1], [2], [2], [], [1], [2], []],
            "expected": None,
        }],
        "validatorCode": (
            "def validate(args, output):\n"
            "    # Re-simulate a reference RandomizedSet; insert/remove must match and\n"
            "    # getRandom must return a value currently in the set.\n"
            "    ops = [\"RandomizedSet\",\"insert\",\"remove\",\"insert\",\"getRandom\",\n"
            "           \"remove\",\"insert\",\"getRandom\"]\n"
            "    call = [[],[1],[2],[2],[],[1],[2],[]]\n"
            "    s = set()\n"
            "    for i,(op,a) in enumerate(zip(ops,call)):\n"
            "        o = output[i]\n"
            "        if op == 'RandomizedSet':\n"
            "            if o is not None: return False\n"
            "        elif op == 'insert':\n"
            "            exp = a[0] not in s; s.add(a[0])\n"
            "            if o != exp: return False\n"
            "        elif op == 'remove':\n"
            "            exp = a[0] in s; s.discard(a[0])\n"
            "            if o != exp: return False\n"
            "        elif op == 'getRandom':\n"
            "            if o not in s: return False\n"
            "    return True\n"
        ),
    },
    "arrays-hashing/24-range-sum-query-immutable": {
        "shape": "design",
        "compareMode": "exact",
        "tests": [{
            "id": "example-1", "kind": "example",
            "ops": ["NumArray", "sumRange", "sumRange", "sumRange"],
            "args": [[[-2, 0, 3, -5, 2, -1]], [0, 2], [2, 5], [0, 5]],
            "expected": [None, 1, -1, -3],
        }],
    },
}


# --------------------------------------------------------------------------------------
# Markdown parsing
# --------------------------------------------------------------------------------------
def parse_markdown(text: str) -> dict:
    """Extract title, difficulty, the three python solutions and the raw example block."""
    title = ""
    m = re.search(r"^#\s*[\d.]*\s*(.+)$", text, re.M)
    if m:
        title = m.group(1).strip()
    diff = ""
    m = re.search(r"\*\*Difficulty:\*\*\s*(\w+)", text)
    if m:
        diff = m.group(1)

    # Solutions: split the doc at each approach header and grab the first python block
    # that follows it.
    solutions = {}
    for key, header in SOLUTION_HEADERS:
        hm = header.search(text)
        if not hm:
            continue
        after = text[hm.end():]
        bm = _PY_BLOCK.search(after)
        if bm:
            solutions[key] = bm.group(1).rstrip()

    # Fallback: if headers weren't found, take the python blocks in order.
    if len(solutions) < 3:
        blocks = _PY_BLOCK.findall(text)
        for key, blk in zip(["naive", "better", "optimal"], blocks):
            solutions.setdefault(key, blk.rstrip())

    example_block = ""
    em = _FENCE_TEXT.search(text)
    if em:
        example_block = em.group(1)

    return {"title": title, "difficulty": diff, "solutions": solutions, "example": example_block}


# --------------------------------------------------------------------------------------
# Solution introspection
# --------------------------------------------------------------------------------------
def _returns_value(func: ast.FunctionDef) -> bool:
    """True if the function has a `return <expr>` (not just bare `return`/none)."""
    for node in ast.walk(func):
        if isinstance(node, ast.Return) and node.value is not None:
            # `return None` still counts as not returning a meaningful value.
            if isinstance(node.value, ast.Constant) and node.value.value is None:
                continue
            return True
    return False


_HELPER_CLASS_NAMES = {"TreeNode", "ListNode", "Node", "GraphNode",
                       "DLinkedNode", "DoublyLinkedNode", "DListNode"}
_TREE_PARAMS = {"root", "p", "q", "subRoot", "root1", "root2"}
_LIST_PARAMS = {"head", "l1", "l2", "list1", "list2", "headA", "headB"}


def _class_public_methods(cls: ast.ClassDef):
    return [n for n in cls.body
            if isinstance(n, ast.FunctionDef) and not n.name.startswith("__")]


def _is_helper_class(cls: ast.ClassDef) -> bool:
    """A helper/data-holder node class: a known node name, or a class with no public
    methods (only __init__ assigning fields)."""
    if cls.name in _HELPER_CLASS_NAMES:
        return True
    return len(_class_public_methods(cls)) == 0


def _extract_helper_code(source: str, tree: ast.Module) -> str:
    """Return concatenated source of TreeNode/ListNode helper classes so the runner can
    inject them (adapters look up ns['TreeNode'] / ns['ListNode'])."""
    chunks = []
    for n in tree.body:
        if isinstance(n, ast.ClassDef) and n.name in ("TreeNode", "ListNode"):
            seg = ast.get_source_segment(source, n)
            if seg:
                chunks.append(seg)
    return "\n\n".join(chunks)


def _infer_arg_kinds(method: ast.FunctionDef, has_tree: bool, has_list: bool):
    """Per-parameter kind: 'tree' | 'linkedlist' | 'plain', from annotation or name."""
    kinds = []
    for a in method.args.args:
        if a.arg == "self":
            continue
        ann = ""
        if a.annotation is not None:
            try:
                ann = ast.unparse(a.annotation)
            except Exception:
                ann = ""
        if "TreeNode" in ann or (has_tree and a.arg in _TREE_PARAMS):
            kinds.append("tree")
        elif "ListNode" in ann or (has_list and a.arg in _LIST_PARAMS):
            kinds.append("linkedlist")
        else:
            kinds.append("plain")
    return kinds


def inspect_solution(source: str) -> dict:
    """Return {shape, className, entry, params, helperCode, argKinds} by static parsing."""
    tree = ast.parse(source)
    classes = [n for n in tree.body if isinstance(n, ast.ClassDef)]
    if not classes:
        raise ValueError("no class found in solution")

    helper_code = _extract_helper_code(source, tree)
    has_tree = any(c.name == "TreeNode" for c in classes)
    has_list = any(c.name == "ListNode" for c in classes)

    logic_classes = [c for c in classes if not _is_helper_class(c)]

    # Pick the solution class: prefer 'Solution', then any 'Solution*', then first logic
    # class. If none has public methods (pure data), fall back to the first class.
    sol = None
    for c in logic_classes:
        if c.name == "Solution":
            sol = c
            break
    if sol is None:
        for c in logic_classes:
            if c.name.startswith("Solution"):
                sol = c
                break
    if sol is None and logic_classes:
        sol = logic_classes[0]
    if sol is None:
        sol = classes[0]

    class_name = sol.name
    methods = _class_public_methods(sol)

    # Design problem: the chosen logic class isn't the generic Solution and exposes an
    # operation API (e.g. LRUCache, Trie, RandomizedSet) -> driven by an op sequence.
    if class_name != "Solution":
        return {"shape": "design", "className": class_name, "entry": None,
                "params": [], "helperCode": helper_code, "argKinds": []}

    if not methods:
        raise ValueError("Solution class has no public method")
    method_names = {m.name for m in methods}
    if {"encode", "decode"} <= method_names:
        enc = next(m for m in methods if m.name == "encode")
        params = [a.arg for a in enc.args.args if a.arg != "self"]
        return {"shape": "codec", "className": "Solution", "entry": "encode",
                "params": params, "helperCode": helper_code, "argKinds": []}

    entry_m = methods[0]
    entry = entry_m.name
    params = [a.arg for a in entry_m.args.args if a.arg != "self"]
    returns_value = _returns_value(entry_m)
    arg_kinds = _infer_arg_kinds(entry_m, has_tree, has_list)
    return {"shape": "function", "className": "Solution", "entry": entry,
            "params": params, "returnsValue": returns_value,
            "helperCode": helper_code, "argKinds": arg_kinds}


# --------------------------------------------------------------------------------------
# Example parsing
# --------------------------------------------------------------------------------------
def _split_top_level_assignments(s: str):
    """Split "nums = [2,7], target = 9" into [("nums","[2,7]"), ("target","9")]."""
    assignments = []
    # Find identifiers that are followed by '=' at bracket depth 0.
    positions = []
    depth = 0
    i = 0
    while i < len(s):
        c = s[i]
        if c in "([{":
            depth += 1
        elif c in ")]}":
            depth -= 1
        elif c == "=" and depth == 0 and (i + 1 >= len(s) or s[i + 1] != "="):
            # Walk back over whitespace to capture the identifier.
            j = i - 1
            while j >= 0 and s[j].isspace():
                j -= 1
            k = j
            while k >= 0 and (s[k].isalnum() or s[k] == "_"):
                k -= 1
            name = s[k + 1:j + 1]
            if name.isidentifier():
                positions.append((k + 1, i, name))
        i += 1
    for idx, (start, eq, name) in enumerate(positions):
        val_end = positions[idx + 1][0] if idx + 1 < len(positions) else len(s)
        raw = s[eq + 1:val_end].strip().rstrip(",").strip()
        assignments.append((name, raw))
    return assignments


def _extract_bracket_groups(s: str):
    """Return the source text of each top-level [...] group in order."""
    groups = []
    depth = 0
    start = None
    for i, c in enumerate(s):
        if c == "[":
            if depth == 0:
                start = i
            depth += 1
        elif c == "]":
            depth -= 1
            if depth == 0 and start is not None:
                groups.append(s[start:i + 1])
                start = None
    return groups


def _split_call_args(argtext: str):
    """Split a call's argument text on top-level commas, parsing each with _lit."""
    if not argtext.strip():
        return []
    parts = []
    depth = 0
    cur = ""
    for c in argtext:
        if c in "([{":
            depth += 1
            cur += c
        elif c in ")]}":
            depth -= 1
            cur += c
        elif c == "," and depth == 0:
            parts.append(cur)
            cur = ""
        else:
            cur += c
    if cur.strip():
        parts.append(cur)
    return [_lit(p) for p in parts]


def _split_top_level_calls(s: str):
    """Parse `name(a, b), other(c)` into [(name, [a,b]), (other,[c])]."""
    calls = []
    i = 0
    n = len(s)
    while i < n:
        m = re.match(r"\s*([A-Za-z_]\w*)\s*\(", s[i:])
        if not m:
            i += 1
            continue
        name = m.group(1)
        j = i + m.end() - 1  # index of '('
        depth = 0
        k = j
        while k < n:
            if s[k] == "(":
                depth += 1
            elif s[k] == ")":
                depth -= 1
                if depth == 0:
                    break
            k += 1
        argtext = s[j + 1:k]
        calls.append((name, _split_call_args(argtext)))
        i = k + 1
    return calls


def parse_example(example_block: str, shape: str, params, class_name=None):
    """Parse an Example fenced block into (args, expected).

    For function problems returns (positional_args_in_param_order, expected).
    For design problems returns ((ops, args_list), expected).
    """
    input_line = ""
    output_line = ""
    section = None  # which label we're currently accumulating
    for line in example_block.splitlines():
        ls = line.strip()
        low = ls.lower()
        if low.startswith("input:"):
            section = "input"
            input_line = ls[len("input:"):].strip()
        elif low.startswith("output:"):
            section = "output"
            output_line = ls[len("output:"):].strip()
        elif low.startswith("explanation:") or low.startswith("constraints:"):
            section = None
        elif section == "input" and ls:
            # Continuation of a multi-line Input (e.g. design op-list on one line and
            # the args-list on the next).
            input_line += " " + ls
        elif section == "output" and ls:
            output_line += " " + ls

    try:
        expected = _lit(output_line) if output_line else None
    except Exception:
        # Bare comma-separated outputs (call-syntax design) are parsed per-branch below.
        expected = None

    if shape == "design":
        # Two supported formats:
        #  (a) bracket lists: ["op",...] [[args],...]
        #  (b) call syntax:   op1(a, b), op2(c)
        groups = _extract_bracket_groups(input_line)
        if len(groups) >= 2 and "(" not in input_line.split("]")[0]:
            ops = _lit(groups[0])
            args_list = _lit(groups[1])
            return {"ops": list(ops), "args": [list(a) for a in args_list]}, expected
        calls = _split_top_level_calls(input_line)
        if calls:
            # Prepend the implicit constructor call; it produces a leading None output.
            ops = [class_name] + [c[0] for c in calls]
            args_list = [[]] + [c[1] for c in calls]
            out_vals = _lit("[" + output_line + "]") if output_line else None
            exp = ([None] + list(out_vals)) if out_vals is not None else None
            return {"ops": ops, "args": args_list}, exp
        ops, args_list = ast.literal_eval(f"({input_line})")
        return {"ops": list(ops), "args": [list(a) for a in args_list]}, expected

    named = dict(_split_top_level_assignments(input_line))
    args = []
    for p in params:
        if p in named:
            args.append(_lit(named[p]))
        else:
            # Single unnamed value case: "Input: [1,2,3]".
            if len(params) == 1 and not named:
                args.append(_lit(input_line))
    return args, expected


# --------------------------------------------------------------------------------------
# Comparison-mode selection & self-check
# --------------------------------------------------------------------------------------
COMPARE_MODES = ["exact", "sorted", "set", "multiset2d", "set2d"]


def _make_manifest(meta, shape_info, compare_mode, tests, complexity=None, validator=None):
    m = {
        "shape": shape_info["shape"],
        "className": shape_info["className"],
        "entry": shape_info["entry"],
        "params": shape_info["params"],
        "compareMode": compare_mode,
        "tests": tests,
    }
    if shape_info.get("inPlaceArg") is not None:
        m["inPlaceArg"] = shape_info["inPlaceArg"]
    if shape_info.get("helperCode"):
        m["helperCode"] = shape_info["helperCode"]
    if shape_info.get("argKinds") and any(k != "plain" for k in shape_info["argKinds"]):
        m["argKinds"] = shape_info["argKinds"]
    if validator:
        m["validatorCode"] = validator
    if complexity:
        m["complexity"] = complexity
    return m


def _solution_passes(manifest, source) -> bool:
    res = runner.mode_run(manifest, source)
    return bool(res.get("ok")) and res.get("summary", {}).get("allPassed", False)


def choose_compare_mode(shape_info, solutions, tests):
    """Pick the least permissive compare mode under which all 3 solutions pass `tests`."""
    for mode in COMPARE_MODES:
        manifest = _make_manifest(None, shape_info, mode, tests)
        if all(_solution_passes(manifest, src) for src in solutions.values()):
            return mode
    return None


# --------------------------------------------------------------------------------------
# Random test generation (function problems)
# --------------------------------------------------------------------------------------
def _infer_type(value) -> str:
    if isinstance(value, bool):
        return "bool"
    if isinstance(value, int):
        return "int"
    if isinstance(value, float):
        return "float"
    if isinstance(value, str):
        return "str"
    if isinstance(value, list):
        if value and isinstance(value[0], list):
            return "list[list[int]]"
        if value and isinstance(value[0], str):
            return "list[str]"
        return "list[int]"
    return "int"


def build_gen_spec(params, example_args):
    """Infer a complexity generator spec from the example's argument types.

    The first list-typed parameter becomes the "size" driver; ints become constants
    taken from the example (a positive int that isn't obviously an index becomes a
    plausible target), strings/other stay constant.
    """
    specs = []
    size_assigned = False
    supported = True
    for name, val in zip(params, example_args):
        typ = _infer_type(val)
        if typ in ("list[int]", "list[str]", "list[list[int]]", "str") and not size_assigned:
            lo, hi = -10000, 10000
            spec = {"name": name, "type": typ, "role": "size", "lo": lo, "hi": hi}
            if typ == "list[list[int]]":
                spec["width"] = len(val[0]) if val and val[0] else 3
            specs.append(spec)
            size_assigned = True
        elif typ == "int":
            specs.append({"name": name, "type": "int", "role": "target", "lo": 1, "hi": 10000})
        else:
            specs.append({"name": name, "type": typ, "role": "const", "value": val})
    if not size_assigned:
        supported = False
    return supported, {"params": specs}


def generate_random_tests(shape_info, solutions, example_args, gen_spec, count=8, seed=99, budget=8.0):
    """Generate oracle-backed random tests with a hard wall-clock budget.

    Some reference solutions (graphs, DP, etc.) can loop or be very slow on the generic
    random inputs we synthesise. To keep the build robust we run generation in a daemon
    thread and abandon it after `budget` seconds, falling back to example-only grading.
    A leaked daemon thread (if a solution truly hangs) dies when the process exits.
    """
    result = {"tests": []}

    def worker():
        try:
            result["tests"] = _generate_random_tests_impl(
                shape_info, solutions, example_args, gen_spec, count, seed)
        except Exception:
            result["tests"] = []

    th = threading.Thread(target=worker, daemon=True)
    th.start()
    th.join(budget)
    if th.is_alive():
        return []  # generation hung or was too slow -> grade on the example(s) only
    return result["tests"]


def _generate_random_tests_impl(shape_info, solutions, example_args, gen_spec, count=8, seed=99):
    """Generate random tests whose expected value is produced by the oracle (optimal).

    Handles three function-style shapes:
      * plain function -> expected = return value
      * in-place       -> expected = the mutated argument (shape_info['inPlaceArg'])
      * codec          -> expected = the original input (round-trip)
    """
    shape = shape_info["shape"]
    if shape not in ("function", "codec"):
        return []
    optimal = solutions.get("optimal")
    if not optimal:
        return []
    rng = random.Random(seed)
    ns = {}
    exec(compile(optimal, "<oracle>", "exec"), ns)
    cls = ns["Solution"]
    entry = shape_info["entry"]
    in_place_arg = shape_info.get("inPlaceArg")

    tests = []
    plain = shape == "function" and in_place_arg is None
    attempts = 0
    max_attempts = count * 8
    while len(tests) < count and attempts < max_attempts:
        attempts += 1
        i = attempts
        # Vary sizes to exercise different inputs.
        n = rng.choice([1, 2, 3, 5, 8, 13, 20, 50])
        args = runner._make_input(gen_spec, n, rng)
        # Coverage boost: with some probability, force duplicate values into the first
        # integer list so problems like contains-duplicate / frequency / anagram get
        # inputs that actually trigger their "positive" branch. The oracle recomputes the
        # expected value, so this never produces an incorrect test.
        if plain and rng.random() < 0.5:
            for a in args:
                if isinstance(a, list) and len(a) >= 2 and all(isinstance(x, int) for x in a):
                    src = rng.randrange(len(a))
                    dst = rng.randrange(len(a))
                    a[dst] = a[src]
                    break
        try:
            call_args = json.loads(json.dumps(args))
            if shape == "codec":
                inst = cls()
                got = inst.decode(inst.encode(*call_args))
            elif in_place_arg is not None:
                getattr(cls(), entry)(*call_args)
                got = call_args[in_place_arg]
            else:
                got = getattr(cls(), entry)(*call_args)
        except Exception:
            continue
        # Only keep JSON-serialisable outputs.
        try:
            json.dumps(got)
        except TypeError:
            continue
        # For plain functions, skip DEGENERATE tests whose answer is "nothing found"
        # (None or empty list). They don't exercise the core logic and would let a
        # trivially-wrong always-empty solution pass. Meaningful [] answers are rare
        # among the generated inputs, so grading falls back to the canonical example.
        if plain and (got is None or (isinstance(got, list) and len(got) == 0)):
            continue
        tests.append({"id": f"rand-{len(tests)+1}", "kind": "random", "args": args, "expected": got})
    return tests


def make_starter(source: str, helper_code: str = "", class_name: str = None) -> str:
    """Build an empty starter stub from a reference solution: same class + method
    signatures, bodies replaced with a `pass` and a hint, so the user only fills bodies.

    If helper_code (TreeNode/ListNode defs) is provided it is prepended so the user sees
    the node structure. class_name selects the logic class (skips helper node classes).
    """
    tree = ast.parse(source)
    all_classes = [n for n in tree.body if isinstance(n, ast.ClassDef)]
    cls = None
    if class_name:
        cls = next((c for c in all_classes if c.name == class_name), None)
    if cls is None:
        cls = next((c for c in all_classes if not _is_helper_class(c)), None)
    if cls is None:
        cls = all_classes[0]
    lines = [f"class {cls.name}:"]
    methods = [n for n in cls.body if isinstance(n, ast.FunctionDef)]
    if not methods:
        lines.append("    pass")
    for m in methods:
        params = []
        args = m.args
        pos = args.args
        defaults = args.defaults
        n_no_default = len(pos) - len(defaults)
        for idx, a in enumerate(pos):
            if idx >= n_no_default:
                d = defaults[idx - n_no_default]
                try:
                    params.append(f"{a.arg}={ast.unparse(d)}")
                except Exception:
                    params.append(a.arg)
            else:
                params.append(a.arg)
        sig = ", ".join(params)
        lines.append(f"    def {m.name}(self, {sig}):" if not sig.startswith("self")
                     else f"    def {m.name}({sig}):")
        lines.append("        # TODO: write your solution here")
        lines.append("        pass")
        lines.append("")
    stub = "\n".join(lines).rstrip() + "\n"
    if helper_code:
        stub = ("# Helper classes (TreeNode/ListNode) are provided automatically.\n"
                + helper_code.rstrip() + "\n\n\n" + stub)
    return stub


# --------------------------------------------------------------------------------------
# Top-level: build one manifest from markdown text
# --------------------------------------------------------------------------------------
def build_manifest(md_text: str, problem_id: str, topic: str, with_random: bool = True):
    """Return (manifest_dict, report_dict). Raises on unrecoverable parse errors.

    with_random=False builds a fast, hang-proof, example-only manifest (no oracle-backed
    random tests, no complexity). Used as a safe base that is later enriched.
    """
    parsed = parse_markdown(md_text)
    solutions = parsed["solutions"]
    if "optimal" not in solutions:
        raise ValueError("could not extract an optimal solution")

    shape_info = inspect_solution(solutions["optimal"])
    supported = False
    gen_spec = None
    validator = None

    override = OVERRIDES.get(problem_id)
    if override:
        # Trust the hand-authored override for shape/tests/validator/compareMode.
        shape_info = dict(shape_info)
        shape_info["shape"] = override.get("shape", shape_info["shape"])
        tests = override["tests"]
        compare_mode = override.get("compareMode", "exact")
        validator = override.get("validatorCode")
    else:
        example_args, expected = parse_example(
            parsed["example"], shape_info["shape"], shape_info["params"],
            shape_info.get("className"))

        if shape_info["shape"] == "design":
            tests = [{"id": "example-1", "kind": "example",
                      "ops": example_args["ops"], "args": example_args["args"],
                      "expected": expected}]
        elif shape_info["shape"] == "codec":
            # Round-trip: expected is the original list of strings.
            tests = [{"id": "example-1", "kind": "example", "args": example_args,
                      "expected": example_args[0]}]
            supported, gen_spec = build_gen_spec(shape_info["params"], example_args)
            if with_random and supported:
                tests += generate_random_tests(shape_info, solutions, example_args, gen_spec)
            supported = False  # complexity not meaningful for codec
        else:
            # Detect in-place problems: optimal returns None but an Output is given.
            if not shape_info.get("returnsValue", True) and expected is not None:
                # The mutated argument is the first list-typed parameter.
                ip = next((i for i, v in enumerate(example_args) if isinstance(v, list)), 0)
                shape_info["inPlaceArg"] = ip

            example_test = {"id": "example-1", "kind": "example",
                            "args": example_args, "expected": expected}
            supported, gen_spec = build_gen_spec(shape_info["params"], example_args)
            # In-place problems with multiple parameters (e.g. merge with m/n) break the
            # generic size generator's invariants, so skip complexity for those.
            if shape_info.get("inPlaceArg") is not None and len(shape_info["params"]) != 1:
                supported = False
            # Tree/linked-list problems: arguments are node structures, so oracle-backed
            # random array generation and empirical complexity are unreliable. Keep them
            # example-only.
            node_problem = any(k != "plain" for k in shape_info.get("argKinds", []))
            if node_problem:
                supported = False
                gen_spec = None
            random_tests = (generate_random_tests(shape_info, solutions, example_args, gen_spec)
                            if (with_random and gen_spec) else [])
            tests = [example_test] + random_tests

        compare_mode = choose_compare_mode(shape_info, solutions, tests)
        if compare_mode is None:
            tests = [tests[0]]  # fall back to example only
            compare_mode = choose_compare_mode(shape_info, solutions, tests) or "exact"

    complexity = None
    if with_random and shape_info["shape"] == "function" and supported and gen_spec:
        complexity = {"supported": True, "sizes": [500, 1000, 2000, 4000, 8000],
                      "repeats": 3, "genSpec": gen_spec}

    manifest = _make_manifest(None, shape_info, compare_mode, tests, complexity, validator)
    manifest["id"] = problem_id
    manifest["title"] = parsed["title"]
    manifest["difficulty"] = parsed["difficulty"]
    manifest["topic"] = topic
    manifest["solutions"] = solutions
    manifest["starterCode"] = make_starter(
        solutions["optimal"], shape_info.get("helperCode", ""), shape_info.get("className"))

    passes = {name: _solution_passes(manifest, src) for name, src in solutions.items()}
    report = {
        "id": problem_id,
        "shape": shape_info["shape"],
        "compareMode": compare_mode,
        "numTests": len(tests),
        "complexity": bool(complexity),
        "solutionPasses": passes,
        "allSolutionsPass": all(passes.values()),
    }
    return manifest, report
