# 02. Maximum Depth of Binary Tree

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree, return its maximum depth: the number of nodes on the longest path from the root down to any leaf.

**Input**
- `root`: the root of a binary tree, or `None`.

**Output**
- An integer; an empty tree has depth `0`.

## Constraints
- `0 <= number of nodes <= 10^4`
- `-100 <= Node.val <= 100`.

## Examples
```text
Input: root = [3,9,20,None,None,15,7]
Output: 3
Explanation: The longest root-to-leaf path goes through node 20 to either 15 or 7. It contains 3 nodes.
```

## Understanding & Intuition
Depth is the number of nodes on the longest downward path. DFS naturally asks each child for its height, while BFS counts levels.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate root-to-leaf path lengths and take the maximum.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def maxDepth(self, root):
        depths = []
        def dfs(node, d):
            if not node:
                return
            if not node.left and not node.right:
                depths.append(d)
            dfs(node.left, d + 1)
            dfs(node.right, d + 1)
        dfs(root, 1)
        return max(depths) if depths else 0
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Recursively compute one plus the larger child depth.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def maxDepth(self, root):
        if not root:
            return 0
        return 1 + max(self.maxDepth(root.left), self.maxDepth(root.right))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterate with a stack of `(node, depth)` pairs.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def maxDepth(self, root):
        if not root:
            return 0
        ans = 0
        stack = [(root, 1)]
        while stack:
            node, depth = stack.pop()
            ans = max(ans, depth)
            if node.left: stack.append((node.left, depth + 1))
            if node.right: stack.append((node.right, depth + 1))
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Empty tree has depth 0.
- A skewed tree may hit recursion depth in recursive solutions.

## Related
- Balanced Binary Tree
- Diameter of Binary Tree
