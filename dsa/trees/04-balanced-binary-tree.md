# 04. Balanced Binary Tree

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Balanced Binary Tree**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [3,9,20,None,None,15,7]
Output: True
Explanation: Each node's subtrees differ in height by at most 1.
```

## Understanding & Intuition
A balanced tree requires the height condition at every node. The optimized approach returns height and failure together so subtrees are not rescanned.

## Approach 1 — Naive / Brute Force
**Idea:** At every node, call a height helper on both sides.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isBalanced(self, root):
        def height(node):
            if not node: return 0
            return 1 + max(height(node.left), height(node.right))
        if not root: return True
        return abs(height(root.left) - height(root.right)) <= 1 and self.isBalanced(root.left) and self.isBalanced(root.right)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Return `-1` as a sentinel when a subtree is already unbalanced.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isBalanced(self, root):
        def check(node):
            if not node: return 0
            left = check(node.left)
            if left == -1: return -1
            right = check(node.right)
            if right == -1 or abs(left - right) > 1: return -1
            return 1 + max(left, right)
        return check(root) != -1
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use iterative postorder and stop when any height gap exceeds 1.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isBalanced(self, root):
        heights = {None: 0}
        stack = [(root, False)]
        while stack:
            node, seen = stack.pop()
            if not node: continue
            if seen:
                l, r = heights[node.left], heights[node.right]
                if abs(l - r) > 1: return False
                heights[node] = 1 + max(l, r)
            else:
                stack.append((node, True)); stack.append((node.right, False)); stack.append((node.left, False))
        return True
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- An empty tree is balanced.
- Do not only check the root; every subtree matters.

## Related
- Maximum Depth of Binary Tree
- Diameter of Binary Tree
