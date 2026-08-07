# 13. Binary Tree Vertical Order Traversal

- **Difficulty:** Medium
- **Pattern:** Tree / BFS
- **Asked at:** Salesforce, Facebook, Amazon

## Problem
Return a binary tree's vertical order traversal from leftmost column to rightmost column.

## Examples
```text
Input: root = [3,9,20,null,null,15,7]
Output: [[9],[3,15],[20],[7]]
Explanation: BFS visits column 0 as 3 then 15.
```

## Understanding & Intuition
Left child decrements column and right child increments it. BFS preserves top-to-bottom and left-to-right order within columns.

## Approach 1 — Naive / Brute Force
**Idea:** DFS all nodes with row and column, then sort.
```python
class TreeNode:
    def __init__(self, val: int = 0, left: 'TreeNode | None' = None, right: 'TreeNode | None' = None):
        self.val = val; self.left = left; self.right = right
class Solution:
    def verticalOrder(self, root: TreeNode | None) -> list[list[int]]:
        nodes = []
        def dfs(node: TreeNode | None, row: int, col: int) -> None:
            if not node: return
            nodes.append((col, row, len(nodes), node.val)); dfs(node.left, row + 1, col - 1); dfs(node.right, row + 1, col + 1)
        dfs(root, 0, 0); ans = []; prev = None
        for col, _, _, val in sorted(nodes):
            if col != prev: ans.append([]); prev = col
            ans[-1].append(val)
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** BFS into a map, then sort column keys.
```python
from collections import defaultdict, deque
class Solution:
    def verticalOrder(self, root: TreeNode | None) -> list[list[int]]:
        if not root: return []
        cols = defaultdict(list); q = deque([(root, 0)])
        while q:
            node, col = q.popleft(); cols[col].append(node.val)
            if node.left: q.append((node.left, col - 1))
            if node.right: q.append((node.right, col + 1))
        return [cols[c] for c in sorted(cols)]
```
- **Time:** O(n + c log c) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track min and max column during BFS to avoid sorting.
```python
from collections import defaultdict, deque
class Solution:
    def verticalOrder(self, root: TreeNode | None) -> list[list[int]]:
        if not root: return []
        cols = defaultdict(list); q = deque([(root, 0)]); lo = hi = 0
        while q:
            node, col = q.popleft(); cols[col].append(node.val); lo = min(lo, col); hi = max(hi, col)
            if node.left: q.append((node.left, col - 1))
            if node.right: q.append((node.right, col + 1))
        return [cols[c] for c in range(lo, hi + 1)]
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n + c log c) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Use BFS for correct tie ordering.
- Empty tree returns `[]`.
- Column indices can be negative.

## Related
- Binary Tree Level Order Traversal
- Vertical Order Traversal of a Binary Tree
