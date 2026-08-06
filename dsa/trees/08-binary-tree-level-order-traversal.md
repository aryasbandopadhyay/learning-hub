# 08. Binary Tree Level Order Traversal

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Binary Tree Level Order Traversal**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [3,9,20,None,None,15,7]
Output: [[3],[9,20],[15,7]]
Explanation: Nodes are grouped by depth.
```

## Understanding & Intuition
Level order is breadth-first traversal. A queue naturally processes nodes in increasing depth and can separate levels by queue length.

## Approach 1 — Naive / Brute Force
**Idea:** Compute tree height and collect each depth with DFS.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def levelOrder(self, root):
        def height(node):
            if not node: return 0
            return 1 + max(height(node.left), height(node.right))
        def collect(node, d, row):
            if not node: return
            if d == 0: row.append(node.val)
            else:
                collect(node.left, d - 1, row); collect(node.right, d - 1, row)
        ans = []
        for d in range(height(root)):
            row = []; collect(root, d, row); ans.append(row)
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** DFS once while appending values into a list for each depth.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def levelOrder(self, root):
        ans = []
        def dfs(node, depth):
            if not node: return
            if depth == len(ans): ans.append([])
            ans[depth].append(node.val)
            dfs(node.left, depth + 1); dfs(node.right, depth + 1)
        dfs(root, 0)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use BFS and process exactly one queue length per level.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def levelOrder(self, root):
        from collections import deque
        if not root: return []
        ans, q = [], deque([root])
        while q:
            row = []
            for _ in range(len(q)):
                node = q.popleft(); row.append(node.val)
                if node.left: q.append(node.left)
                if node.right: q.append(node.right)
            ans.append(row)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Return [] for an empty tree.
- Take `len(q)` before processing a level.

## Related
- Binary Tree Zigzag Level Order Traversal
- Binary Tree Right Side View
