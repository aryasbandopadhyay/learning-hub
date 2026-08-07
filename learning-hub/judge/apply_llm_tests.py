#!/usr/bin/env python3
"""Apply LLM-authored edge-case tests to judge manifests.

Motivation
----------
The auto-generated ``kind:"random"`` tests in each manifest are synthesised from a
problem's example *shape* using uniform random inputs, with a vetted reference solution
acting as the oracle for the expected output. That guarantees *correctness* but not
*quality*: uniform-random inputs rarely hit the interesting edges (empty input, a single
element, all-duplicates, negatives, min/max boundaries, degenerate structures, already
sorted / reverse sorted, ...). Those are exactly the inputs that catch subtle bugs.

This tool lets a human/LLM curate high-signal edge-case **inputs** per problem, while the
**expected output stays machine-computed** by the same trustworthy oracle so it can never
be wrong. Concretely, for each problem it:

  1. Reads a sidecar spec ``judge/tests/<topic>/<slug>.json`` listing curated ``cases``
     (each an input ``args`` for function/codec problems, or ``ops`` + ``args`` for design
     problems, plus an optional human ``label`` describing the edge it targets).
  2. Runs the manifest's ``optimal`` reference solution against those inputs to compute the
     expected output (the *oracle*).
  3. Keeps only the cases on which **all** reference solutions (naive/better/optimal) agree
     under the manifest's ``compareMode``/validator — dropping any ambiguous or
     oracle-erroring input. This mirrors the robustness contract already used by genlib.
  4. Injects the survivors into the manifest as ``kind:"edge"`` tests, *replacing* the old
     ``kind:"random"`` tests and preserving the canonical ``kind:"example"`` tests.
  5. Verifies every reference solution still passes the enriched manifest before saving.
  6. Writes the computed ``expected`` back into the sidecar so it is a self-contained,
     human-readable record of the correct test cases.

Only the standard library + the sibling ``runner`` module are used, so it runs anywhere the
judge runs.

Usage
-----
    # Apply every sidecar found under judge/tests/ to the manifests under judge/manifests/
    python apply_llm_tests.py

    # Restrict to a single topic (matches sidecars under judge/tests/<topic>/)
    python apply_llm_tests.py --topic arrays-hashing

    # Apply a single problem by id (topic/slug)
    python apply_llm_tests.py --id arrays-hashing/01-two-sum

    # Dry run: report what would change without touching any file
    python apply_llm_tests.py --topic arrays-hashing --dry-run
"""

from __future__ import annotations

import argparse
import copy
import json
import os
import sys

import runner  # sibling module in the judge/ directory


HERE = os.path.dirname(os.path.abspath(__file__))
DEFAULT_MANIFESTS = os.path.join(HERE, "manifests")
DEFAULT_TESTS = os.path.join(HERE, "tests")


# --------------------------------------------------------------------------------------
# Oracle-backed expected computation + cross-solution validation
# --------------------------------------------------------------------------------------
def _probe_tests_from_cases(shape: str, cases: list) -> list:
    """Turn curated sidecar ``cases`` into runnable test dicts (without ``expected``)."""
    probe = []
    for i, c in enumerate(cases, 1):
        t = {"id": f"edge-{i}", "kind": "edge"}
        if c.get("label"):
            t["label"] = c["label"]
        if shape == "design":
            t["ops"] = c["ops"]
            t["args"] = c["args"]
        else:
            t["args"] = c["args"]
        probe.append(t)
    return probe


def compute_edge_tests(manifest: dict, cases: list):
    """Compute expected via the optimal oracle and keep only unanimously-agreed cases.

    Returns ``(edge_tests, report)`` where ``edge_tests`` is the list of accepted tests
    (each carrying an oracle-computed ``expected``) and ``report`` is a small dict with
    counts and the reason any case was dropped.
    """
    shape = manifest.get("shape", "function")
    solutions = manifest.get("solutions", {})
    optimal = solutions.get("optimal")
    report = {"submitted": len(cases), "accepted": 0, "dropped": [], "error": None}

    if not optimal:
        report["error"] = "manifest has no optimal reference solution"
        return [], report

    probe_tests = _probe_tests_from_cases(shape, cases)

    # Step 1: run the oracle to obtain expected outputs.
    oracle_manifest = copy.deepcopy(manifest)
    oracle_manifest["tests"] = probe_tests
    oracle_res = runner.mode_run(oracle_manifest, optimal)
    if not oracle_res.get("ok"):
        report["error"] = "optimal solution failed to compile/run: " + \
            str(oracle_res.get("compileError", ""))[:300]
        return [], report

    candidates = []
    for t, r in zip(probe_tests, oracle_res["results"]):
        if r.get("error"):
            report["dropped"].append({"id": t["id"], "label": t.get("label"),
                                      "reason": "oracle raised on this input"})
            continue
        exp = r.get("got")
        try:
            json.dumps(exp)
        except TypeError:
            report["dropped"].append({"id": t["id"], "label": t.get("label"),
                                      "reason": "oracle output not JSON-serialisable"})
            continue
        et = dict(t)
        et["expected"] = exp
        candidates.append(et)

    if not candidates:
        return [], report

    # Step 2: cross-validate — every reference solution must pass every candidate under the
    # manifest's compareMode/validator. A disagreement means the input is ambiguous (multiple
    # valid answers with no validator) or a reference solution is weaker than the oracle; in
    # either case we drop that specific case rather than ship a flaky test.
    check_manifest = copy.deepcopy(manifest)
    check_manifest["tests"] = candidates
    per_sol = {name: runner.mode_run(check_manifest, src) for name, src in solutions.items()}

    accepted = []
    for idx, et in enumerate(candidates):
        disagreeing = None
        for name, rr in per_sol.items():
            if not rr.get("ok") or not rr["results"][idx].get("passed"):
                disagreeing = name
                break
        if disagreeing is None:
            accepted.append(et)
        else:
            report["dropped"].append({"id": et["id"], "label": et.get("label"),
                                      "reason": f"reference solution '{disagreeing}' disagreed"})

    # Renumber accepted tests edge-1..N for a clean manifest.
    for i, et in enumerate(accepted, 1):
        et["id"] = f"edge-{i}"
    report["accepted"] = len(accepted)
    return accepted, report


# --------------------------------------------------------------------------------------
# Manifest surgery
# --------------------------------------------------------------------------------------
def merge_edge_tests(manifest: dict, edge_tests: list) -> dict:
    """Return a new manifest whose tests = existing example tests + the new edge tests.

    All prior ``kind:"random"`` and ``kind:"edge"`` tests are discarded (idempotent).
    """
    kept = [t for t in manifest.get("tests", []) if t.get("kind") == "example"]
    out = copy.deepcopy(manifest)
    out["tests"] = kept + edge_tests
    return out


def all_solutions_pass(manifest: dict) -> bool:
    """Final gate: every reference solution must pass the full enriched manifest."""
    sols = manifest.get("solutions", {})
    if not sols:
        return False
    for src in sols.values():
        res = runner.mode_run(manifest, src)
        if not res.get("ok") or not res.get("summary", {}).get("allPassed", False):
            return False
    return True


# --------------------------------------------------------------------------------------
# Filesystem plumbing
# --------------------------------------------------------------------------------------
def _manifest_path(manifests_dir: str, problem_id: str) -> str:
    return os.path.join(manifests_dir, *problem_id.split("/")) + ".json"


def _sidecar_path(tests_dir: str, problem_id: str) -> str:
    return os.path.join(tests_dir, *problem_id.split("/")) + ".json"


def _iter_sidecars(tests_dir: str, topic: str | None, only_id: str | None):
    """Yield (problem_id, sidecar_path) for every sidecar spec matching the filters."""
    if only_id:
        p = _sidecar_path(tests_dir, only_id)
        if os.path.isfile(p):
            yield only_id, p
        return
    root = os.path.join(tests_dir, topic) if topic else tests_dir
    if not os.path.isdir(root):
        return
    for dirpath, _dirs, files in os.walk(root):
        for fn in sorted(files):
            if not fn.endswith(".json"):
                continue
            full = os.path.join(dirpath, fn)
            rel = os.path.relpath(full, tests_dir).replace("\\", "/")
            problem_id = rel[:-5]  # strip .json
            yield problem_id, full


def apply_one(problem_id: str, sidecar_path: str, manifests_dir: str, dry_run: bool):
    """Apply one sidecar to its manifest. Returns a result dict for reporting."""
    result = {"id": problem_id, "ok": False, "accepted": 0, "message": ""}
    manifest_path = _manifest_path(manifests_dir, problem_id)
    if not os.path.isfile(manifest_path):
        result["message"] = "no manifest (problem may not be gradable yet)"
        return result

    with open(sidecar_path, encoding="utf-8") as f:
        spec = json.load(f)
    cases = spec.get("cases", [])
    if not cases:
        result["message"] = "sidecar has no cases"
        return result

    with open(manifest_path, encoding="utf-8") as f:
        manifest = json.load(f)

    edge_tests, report = compute_edge_tests(manifest, cases)
    if report["error"]:
        result["message"] = "ERROR: " + report["error"]
        return result
    if not edge_tests:
        result["message"] = f"0/{report['submitted']} cases accepted (all dropped)"
        result["dropped"] = report["dropped"]
        return result

    enriched = merge_edge_tests(manifest, edge_tests)
    if not all_solutions_pass(enriched):
        result["message"] = "reference solutions did not all pass the enriched manifest"
        return result

    result["accepted"] = report["accepted"]
    result["dropped"] = report["dropped"]
    if dry_run:
        result["ok"] = True
        result["message"] = f"[dry-run] would write {report['accepted']}/{report['submitted']} edge tests"
        return result

    # Persist the enriched manifest.
    with open(manifest_path, "w", encoding="utf-8") as f:
        json.dump(enriched, f, indent=2)

    # Write the oracle-computed expected back into the sidecar for a self-contained record.
    spec["cases"] = [{k: v for k, v in {
        "label": et.get("label"),
        **({"ops": et["ops"]} if "ops" in et else {}),
        "args": et["args"],
        "expected": et["expected"],
    }.items() if v is not None or k in ("expected", "args")} for et in edge_tests]
    with open(sidecar_path, "w", encoding="utf-8") as f:
        json.dump(spec, f, indent=2)

    result["ok"] = True
    result["message"] = f"wrote {report['accepted']}/{report['submitted']} edge tests"
    return result


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--manifests-dir", default=DEFAULT_MANIFESTS)
    ap.add_argument("--tests-dir", default=DEFAULT_TESTS)
    ap.add_argument("--topic", help="restrict to sidecars under tests/<topic>/")
    ap.add_argument("--id", dest="only_id", help="apply a single problem id (topic/slug)")
    ap.add_argument("--dry-run", action="store_true", help="report without writing")
    args = ap.parse_args(argv)

    sidecars = list(_iter_sidecars(args.tests_dir, args.topic, args.only_id))
    if not sidecars:
        print("No sidecar test specs found.", file=sys.stderr)
        return 1

    ok = fail = 0
    for problem_id, path in sidecars:
        res = apply_one(problem_id, path, args.manifests_dir, args.dry_run)
        status = "OK  " if res["ok"] else "SKIP"
        if not res["ok"]:
            fail += 1
        else:
            ok += 1
        print(f"[{status}] {problem_id}: {res['message']}")
        for d in res.get("dropped", []):
            print(f"         - dropped {d['id']} ({d.get('label') or ''}): {d['reason']}")

    print(f"\nSummary: {ok} applied, {fail} skipped/failed, {len(sidecars)} total")
    return 0 if fail == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
