# 03. Diameter of Binary Tree

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree, return the diameter length. The diameter is the number of edges on the longest path between any two nodes; it need not pass through the root.

**Input**
- `root`: the root of a binary tree.

**Output**
- An integer: the maximum number of edges on any node-to-node path.

## Constraints
- `1 <= number of nodes <= 10^4`
- `-100 <= Node.val <= 100`.

## Examples
```text
Input: root = [1,2,3,4,5]
Output: 3
Explanation: The longest path is `4 -> 2 -> 1 -> 3` or `5 -> 2 -> 1 -> 3`. It has 3 edges.
```

## Understanding & Intuition
The diameter through a node is left height plus right height. Computing heights repeatedly is simple but wasteful; a postorder pass reuses each height once.

## Approach 1 — Naive / Brute Force
**Idea:** For every node, recompute subtree heights and update the best diameter.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def diameterOfBinaryTree(self, root):
        def height(node):
            if not node: return 0
            return 1 + max(height(node.left), height(node.right))
        if not root:
            return 0
        here = height(root.left) + height(root.right)
        return max(here, self.diameterOfBinaryTree(root.left), self.diameterOfBinaryTree(root.right))
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use one DFS returning height while updating the best edge count.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def diameterOfBinaryTree(self, root):
        best = 0
        def dfs(node):
            nonlocal best
            if not node: return 0
            left, right = dfs(node.left), dfs(node.right)
            best = max(best, left + right)
            return 1 + max(left, right)
        dfs(root)
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Perform iterative postorder to avoid recursion while still visiting each node once.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def diameterOfBinaryTree(self, root):
        if not root: return 0
        height, best = {}, 0
        stack = [(root, False)]
        while stack:
            node, seen = stack.pop()
            if not node: continue
            if seen:
                l = height.get(node.left, 0); r = height.get(node.right, 0)
                best = max(best, l + r)
                height[node] = 1 + max(l, r)
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
- Diameter counts edges, not nodes.
- The longest path need not pass through the root.

## Related
- Maximum Depth of Binary Tree
- Binary Tree Maximum Path Sum
