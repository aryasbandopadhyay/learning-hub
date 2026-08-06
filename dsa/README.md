# DSA Question Bank (Python) — FAANG Interview Prep 🧠

A curated bank of ~300 most-asked FAANG data-structures & algorithms problems, organized by
**pattern**. Every question is a self-contained Markdown file with:

- a clear **restatement** of the problem + examples and constraints,
- **understanding & intuition** (what the problem is really asking, which pattern to reach for),
- **three solutions** — **Naive → Better → Optimal** — each with commented **Python** code and
  **time/space complexity**,
- a complexity summary table, edge cases/pitfalls, and related problems.

> Solutions are written in **Python 3** (standard library only). This bank is content for the
> [learning-hub](../learning-hub) app (the **DSA** tab) — the app itself stays Spring Boot; only
> the solution code here is Python.

## How to use

- Browse a topic folder below; each has its own `README.md` indexing its questions by difficulty.
- Or open the **DSA** tab in the learning-hub app to read everything rendered nicely.
- Suggested study order roughly follows the list below (fundamentals → graphs → DP).

## Topics

| # | Pattern | Folder | ~Count |
|---|---------|--------|--------|
| 1 | Arrays & Hashing | [arrays-hashing](./arrays-hashing) | 25 |
| 2 | Two Pointers | [two-pointers](./two-pointers) | 15 |
| 3 | Sliding Window | [sliding-window](./sliding-window) | 15 |
| 4 | Stack | [stack](./stack) | 15 |
| 5 | Binary Search | [binary-search](./binary-search) | 15 |
| 6 | Linked List | [linked-list](./linked-list) | 20 |
| 7 | Trees | [trees](./trees) | 25 |
| 8 | Tries | [tries](./tries) | 8 |
| 9 | Heap / Priority Queue | [heap-priority-queue](./heap-priority-queue) | 12 |
| 10 | Backtracking | [backtracking](./backtracking) | 15 |
| 11 | Graphs | [graphs](./graphs) | 25 |
| 12 | Advanced Graphs | [advanced-graphs](./advanced-graphs) | 10 |
| 13 | Dynamic Programming (1D) | [dp-1d](./dp-1d) | 15 |
| 14 | Dynamic Programming (2D) | [dp-2d](./dp-2d) | 15 |
| 15 | Greedy | [greedy](./greedy) | 15 |
| 16 | Intervals | [intervals](./intervals) | 8 |
| 17 | Math & Geometry | [math-geometry](./math-geometry) | 15 |
| 18 | Bit Manipulation | [bit-manipulation](./bit-manipulation) | 12 |
| 19 | Strings | [strings](./strings) | 20 |
| 20 | Matrix | [matrix](./matrix) | 10 |

**Total: ~300 questions.**

## Solution philosophy — Naive · Better · Optimal

For each problem we deliberately show the progression an interviewer wants to see:

1. **Naive** — the first correct idea (often brute force). Establishes correctness and a baseline.
2. **Better** — a meaningful improvement (e.g. sorting, hashing) that cuts time or space.
3. **Optimal** — the best-known approach with the key trick, and why you can't easily do better.

Seeing all three builds the pattern recognition that makes FAANG rounds tractable.
