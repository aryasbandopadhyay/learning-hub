#!/usr/bin/env python3
"""Build all manifests for a nested section (google / faang) and relocate the
non-gradable problems into <section>/revision/<topic>/ with a README.

Usage (run from the judge dir):
    python build_section.py --content-root "<Projects>" --section google --timeout 30

For each leaf topic folder under dsa/<section> (excluding 'revision'):
  1. build manifests via build_manifests.py --topic <section>/<topic>
  2. any .md whose manifest is missing afterwards is MOVED to dsa/<section>/revision/<topic>/
Then (re)write dsa/<section>/revision/README.md summarising what's there and why.
"""
import argparse
import os
import shutil
import subprocess
import sys

JUDGE_DIR = os.path.dirname(os.path.abspath(__file__))


def leaf_topics(section_dir):
    out = []
    for name in sorted(os.listdir(section_dir)):
        p = os.path.join(section_dir, name)
        if os.path.isdir(p) and name != "revision":
            out.append(name)
    return out


def build_topic(content_root, out_dir, section, topic, timeout):
    cmd = [sys.executable, os.path.join(JUDGE_DIR, "build_manifests.py"),
           "--content-root", content_root, "--out", out_dir,
           "--topic", f"{section}/{topic}", "--timeout", str(timeout)]
    subprocess.run(cmd, check=False)


def relocate_failures(content_root, out_dir, section, topic):
    """Move .md files that ended up with no manifest into revision/<topic>/. Returns moved slugs."""
    topic_src = os.path.join(content_root, "dsa", section, topic)
    man_dir = os.path.join(out_dir, section, topic)
    moved = []
    for fname in sorted(os.listdir(topic_src)):
        if not fname.endswith(".md") or fname.lower() == "readme.md":
            continue
        slug = fname[:-3]
        mf = os.path.join(man_dir, slug + ".json")
        if not os.path.isfile(mf):
            dest_dir = os.path.join(content_root, "dsa", section, "revision", topic)
            os.makedirs(dest_dir, exist_ok=True)
            shutil.move(os.path.join(topic_src, fname), os.path.join(dest_dir, fname))
            moved.append(slug)
    return moved


def write_revision_readme(content_root, section):
    rev = os.path.join(content_root, "dsa", section, "revision")
    if not os.path.isdir(rev):
        return
    lines = [f"# {section.upper()} — Revision (No Online Judge)", "",
             "These problems are kept for **reading and manual revision only** — they do not",
             "have an interactive Solve panel because their input/output shape (graphs, trees,",
             "linked lists, op-sequence designs, or non-deterministic answers) can't be",
             "auto-graded by the local judge. Each still has the full write-up: understanding +",
             "naive / better / optimal solutions with complexity.", ""]
    any_topic = False
    for topic in sorted(os.listdir(rev)):
        tdir = os.path.join(rev, topic)
        if not os.path.isdir(tdir):
            continue
        files = [f for f in sorted(os.listdir(tdir)) if f.endswith(".md")]
        if not files:
            continue
        any_topic = True
        lines.append(f"## {topic}")
        for f in files:
            title = f[:-3].replace("-", " ")
            lines.append(f"- {title}")
        lines.append("")
    if not any_topic:
        lines.append("_(Nothing here — every problem in this section is judge-gradable.)_")
    with open(os.path.join(rev, "README.md"), "w", encoding="utf-8") as fh:
        fh.write("\n".join(lines).rstrip() + "\n")


def main(argv=None):
    ap = argparse.ArgumentParser()
    ap.add_argument("--content-root", required=True)
    ap.add_argument("--section", required=True)
    ap.add_argument("--out", default=os.path.join(JUDGE_DIR, "manifests"))
    ap.add_argument("--timeout", type=float, default=30.0)
    args = ap.parse_args(argv)

    section_dir = os.path.join(args.content_root, "dsa", args.section)
    topics = leaf_topics(section_dir)
    total_moved = {}
    for topic in topics:
        print(f"\n########## {args.section}/{topic} ##########", flush=True)
        build_topic(args.content_root, args.out, args.section, topic, args.timeout)
        moved = relocate_failures(args.content_root, args.out, args.section, topic)
        if moved:
            total_moved[topic] = moved
            print(f"  -> moved to revision: {moved}", flush=True)
    write_revision_readme(args.content_root, args.section)

    print("\n==================== SECTION SUMMARY ====================", flush=True)
    moved_count = sum(len(v) for v in total_moved.values())
    print(f"section={args.section} topics={len(topics)} moved_to_revision={moved_count}", flush=True)
    for t, s in total_moved.items():
        print(f"  {t}: {s}", flush=True)


if __name__ == "__main__":
    main()
