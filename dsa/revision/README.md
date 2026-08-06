# Revision (No Online Judge)

These 12 problems are kept here for **reading and manual revision only** — they do
**not** have an interactive Solve panel in the online judge.

## Why they aren't auto-graded

The local judge grades a submission by auto-driving your function against generated
test cases and comparing outputs. That requires the input/output to be expressible as
plain JSON arrays. These problems break that assumption:

| Problem | Reason it can't be generically auto-graded |
|---|---|
| linked-list/linked-list-cycle | Input encodes a cycle via a `pos` back-edge index — not representable as a flat array |
| linked-list/copy-list-with-random-pointer | Node has an extra `random` pointer forming an arbitrary graph |
| linked-list/merge-k-sorted-lists, heap/merge-k-sorted-lists | Argument is a *list of linked lists* |
| linked-list/intersection-of-two-linked-lists | Two lists share a physical suffix node (pointer aliasing) |
| trees/lowest-common-ancestor-of-a-bst, lowest-common-ancestor-of-a-binary-tree | `p` / `q` are node **references** (given as values); return is a node |
| trees/serialize-and-deserialize-binary-tree | Round-trip of an arbitrary codec, not a pure function |
| graphs/clone-graph | Adjacency-graph `Node` with cycles |
| graphs/evaluate-division | Floating-point output needs tolerance comparison |
| intervals/employee-free-time | Nested interval objects |
| backtracking/sudoku-solver | In-place mutation of a 9×9 board |

Each file still contains the full write-up: understanding + naive / better / optimal
solutions with complexity analysis. Use them to revise the patterns; code them up in a
scratch file if you want to run them.
