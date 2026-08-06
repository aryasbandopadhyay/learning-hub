#!/usr/bin/env python3
"""Build judge manifests for one or more DSA topics.

Usage (topic mode — isolates each problem in its own subprocess so a pathological
reference solution can never hang the whole build):

    python build_manifests.py --content-root <projects-root> --out <manifests-dir> \
        --topic arrays-hashing [--topic two-pointers ...] [--timeout 40]

Single-problem mode (used internally by topic mode; also handy for debugging one file):

    python build_manifests.py --single <path-to.md> --problem-id <topic/slug> \
        --topic <topic> --out <manifests-dir>

For each `dsa/<topic>/NN-slug.md` it builds a manifest via genlib and writes it to
`<manifests-dir>/<topic>/NN-slug.json`. It prints a per-problem report and a summary,
and exits non-zero if any manifest fails its self-check (a reference solution does not
pass), so it can be used as a verification gate.
"""

from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys
import traceback


# --------------------------------------------------------------------------------------
# Single-problem build (runs in a child process)
# --------------------------------------------------------------------------------------
def build_single(md_path: str, problem_id: str, topic: str, out_dir: str) -> int:
    """Build ONE manifest, write it, and print its report as a single JSON line.

    Exit code 0 if all reference solutions pass their self-check, else 1.
    """
    import genlib  # imported lazily so topic-mode startup stays cheap

    topic_out = os.path.join(out_dir, topic)
    os.makedirs(topic_out, exist_ok=True)
    slug = os.path.basename(md_path)[:-3]
    out_path = os.path.join(topic_out, f"{slug}.json")
    try:
        with open(md_path, encoding="utf-8") as f:
            md = f.read()
        # Phase 1: a fast, hang-proof, example-only manifest. Written immediately so that
        # even if phase 2 is killed for timing out, a working judge still exists. Only
        # persist it if all three reference solutions pass the example self-check;
        # otherwise remove any stale file so the UI shows no (broken) judge panel.
        base_manifest, base_report = genlib.build_manifest(md, problem_id, topic, with_random=False)
        report = base_report
        if base_report.get("allSolutionsPass"):
            with open(out_path, "w", encoding="utf-8") as f:
                json.dump(base_manifest, f, indent=2)
            print(json.dumps(base_report), flush=True)

            # Phase 2: enrich with oracle-backed random tests + complexity. May be slow/killed.
            try:
                full_manifest, full_report = genlib.build_manifest(md, problem_id, topic, with_random=True)
                if full_report.get("allSolutionsPass"):
                    with open(out_path, "w", encoding="utf-8") as f:
                        json.dump(full_manifest, f, indent=2)
                    report = full_report
                    print(json.dumps(full_report), flush=True)
            except Exception:
                pass  # keep the phase-1 manifest
        else:
            _remove_stale(out_path)
            print(json.dumps(base_report), flush=True)
    except Exception:
        _remove_stale(out_path)
        report = {"id": problem_id, "error": traceback.format_exc(limit=3),
                  "allSolutionsPass": False}
        print(json.dumps(report), flush=True)
    return 0 if report.get("allSolutionsPass") else 1


def _remove_stale(path: str):
    """Delete a previously-written manifest that no longer passes self-check."""
    try:
        if os.path.exists(path):
            os.remove(path)
    except OSError:
        pass


# --------------------------------------------------------------------------------------
# Topic build (parent process): fan out one child per problem with a hard timeout
# --------------------------------------------------------------------------------------
def build_topic(content_root: str, out_dir: str, topic: str, timeout: float):
    topic_src = os.path.join(content_root, "dsa", topic)
    reports = []
    for fname in sorted(os.listdir(topic_src)):
        if not fname.endswith(".md") or fname.lower() == "readme.md":
            continue
        slug = fname[:-3]
        problem_id = f"{topic}/{slug}"
        md_path = os.path.join(topic_src, fname)
        cmd = [sys.executable, os.path.abspath(__file__),
               "--single", md_path, "--problem-id", problem_id,
               "--topic", topic, "--out", out_dir]
        try:
            proc = subprocess.run(cmd, capture_output=True, text=True, timeout=timeout)
            out = (proc.stdout or "").strip()
            try:
                report = json.loads(out.splitlines()[-1]) if out else {}
            except Exception:
                tail = (out[-300:] or (proc.stderr or "")[-300:])
                report = {"id": problem_id, "allSolutionsPass": False,
                          "error": "unparseable child output: " + tail}
        except subprocess.TimeoutExpired as te:
            # Killed while enriching. If phase 1 already emitted a base report, honour it —
            # the example-only manifest was written to disk before the kill.
            partial = (te.stdout or "")
            if isinstance(partial, bytes):
                partial = partial.decode("utf-8", "replace")
            partial = partial.strip()
            report = None
            if partial:
                try:
                    report = json.loads(partial.splitlines()[-1])
                    report["timedOutDuringEnrich"] = True
                except Exception:
                    report = None
            if report is None:
                report = {"id": problem_id, "allSolutionsPass": False,
                          "error": f"TIMEOUT after {timeout}s (killed)"}
        reports.append(report)
        _print_report(report)
    return reports


def _print_report(r):
    status = "OK" if r.get("allSolutionsPass") else "FAIL"
    extra = ""
    if r.get("error"):
        extra = " ERROR: " + str(r["error"]).splitlines()[-1]
    elif not r.get("allSolutionsPass"):
        extra = " passes=" + json.dumps(r.get("solutionPasses"))
    print(f"[{status}] {r.get('id')} shape={r.get('shape')} "
          f"mode={r.get('compareMode')} tests={r.get('numTests')} "
          f"cx={r.get('complexity')}{extra}", flush=True)


def main(argv=None):
    ap = argparse.ArgumentParser()
    ap.add_argument("--content-root", help="Projects root that contains dsa/")
    ap.add_argument("--out", required=True, help="manifests output directory")
    ap.add_argument("--topic", action="append", help="topic slug (repeatable)")
    ap.add_argument("--timeout", type=float, default=40.0, help="per-problem timeout (s)")
    # single-problem mode
    ap.add_argument("--single", help="path to a single .md to build")
    ap.add_argument("--problem-id", help="problem id (topic/slug) for --single")
    args = ap.parse_args(argv)

    if args.single:
        if not (args.problem_id and args.topic):
            ap.error("--single requires --problem-id and --topic")
        return build_single(args.single, args.problem_id, args.topic[0], args.out)

    if not (args.content_root and args.topic):
        ap.error("topic mode requires --content-root and --topic")

    all_reports = []
    for topic in args.topic:
        all_reports.extend(build_topic(args.content_root, args.out, topic, args.timeout))

    ok = sum(1 for r in all_reports if r.get("allSolutionsPass"))
    fail = len(all_reports) - ok
    print(f"\nSummary: {ok} OK, {fail} FAIL, {ok + fail} total", flush=True)
    return 1 if fail else 0


if __name__ == "__main__":
    raise SystemExit(main())
