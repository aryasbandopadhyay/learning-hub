# 14. Binary Tree Maximum Path Sum

- **Difficulty:** Hard
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

A path is any non-empty sequence of connected nodes without repeating a node. It may start and end anywhere in the tree.

Return the maximum sum of values over any path.

**Input**
- `root`: the root of a non-empty binary tree.

**Output**
- An integer: the largest path sum.

## Constraints
- `1 <= number of nodes <= 3 * 10^4`
- `-1000 <= Node.val <= 1000`.

## Examples
```text
Input: root = [-10,9,20,None,None,15,7]
Output: 42
Explanation: The best path is `15 -> 20 -> 7`, summing to 42. Including `-10` would reduce the sum.
```

## Understanding & Intuition
A path may start and end anywhere but cannot branch upward twice. For each node, keep the best one-sided gain to its parent and update the global two-sided path.

## Approach 1 — Naive / Brute Force
**Idea:** For every node, recompute the best downward gain on both sides.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def maxPathSum(self, root):
        def gain(node):
            if not node: return 0
            return max(0, node.val + max(gain(node.left), gain(node.right)))
        if not root: return float('-inf')
        here = root.val + gain(root.left) + gain(root.right)
        return max(here, self.maxPathSum(root.left), self.maxPathSum(root.right))
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Single DFS returns one-sided gain and updates the best full path.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def maxPathSum(self, root):
        best = float('-inf')
        def dfs(node):
            nonlocal best
            if not node: return 0
            left = max(0, dfs(node.left)); right = max(0, dfs(node.right))
            best = max(best, node.val + left + right)
            return node.val + max(left, right)
        dfs(root)
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterative postorder computes gains without recursion depth risk.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def maxPathSum(self, root):
        gain, best = {None: 0}, float('-inf')
        stack = [(root, False)]
        while stack:
            node, seen = stack.pop()
            if not node: continue
            if seen:
                left = max(0, gain[node.left]); right = max(0, gain[node.right])
                best = max(best, node.val + left + right)
                gain[node] = node.val + max(left, right)
            else:
                stack.append((node, True)); stack.append((node.right, False)); stack.append((node.left, False))
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Do not return 0 for an all-negative tree's answer.
- A parent can use only one side of a child path.

## Related
- Diameter of Binary Tree
- Path Sum
