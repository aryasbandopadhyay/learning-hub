# 01. Invert Binary Tree

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Invert Binary Tree**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [4,2,7,1,3,6,9]
Output: [4,7,2,9,6,3,1]
Explanation: Every left and right child is swapped.
```

## Understanding & Intuition
Inverting is a local operation: every node swaps its children. The only question is traversal order; recursion is concise while an explicit queue avoids recursion depth.

## Approach 1 — Naive / Brute Force
**Idea:** Build a new mirrored tree instead of modifying the input.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def invertTree(self, root):
        # Create mirrored copies bottom-up.
        if not root:
            return None
        return TreeNode(root.val, self.invertTree(root.right), self.invertTree(root.left))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Swap children recursively in place.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def invertTree(self, root):
        if not root:
            return None
        root.left, root.right = self.invertTree(root.right), self.invertTree(root.left)
        return root
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use BFS to swap nodes iteratively and avoid call-stack growth.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def invertTree(self, root):
        from collections import deque
        if not root:
            return None
        q = deque([root])
        while q:
            node = q.popleft()
            node.left, node.right = node.right, node.left
            if node.left: q.append(node.left)
            if node.right: q.append(node.right)
        return root
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Empty tree returns None.
- Remember to return the root after in-place mutation.

## Related
- Maximum Depth of Binary Tree
- Same Tree
