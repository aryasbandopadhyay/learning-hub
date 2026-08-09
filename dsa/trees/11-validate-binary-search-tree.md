# 11. Validate Binary Search Tree

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree, return whether it is a valid BST. All values in a node's left subtree must be strictly smaller, and all values in its right subtree strictly larger.

**Input**
- `root`: the root of a binary tree.

**Output**
- `True` if strict BST ordering holds everywhere; otherwise `False`.

## Constraints
- `1 <= number of nodes <= 10^4`
- `-2^31 <= Node.val <= 2^31 - 1`.

## Examples
```text
Input: root = [2,1,3]
Output: True
Explanation: The left child 1 is less than 2 and the right child 3 is greater than 2, so all BST ranges are valid.
```

## Understanding & Intuition
A valid BST is not just locally ordered; every node must fall within ancestor-derived bounds. Inorder traversal is another way because valid BST values appear strictly increasing.

## Approach 1 — Naive / Brute Force
**Idea:** Collect inorder values and verify they are strictly increasing.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isValidBST(self, root):
        vals = []
        def inorder(node):
            if not node: return
            inorder(node.left); vals.append(node.val); inorder(node.right)
        inorder(root)
        return all(vals[i] < vals[i + 1] for i in range(len(vals) - 1))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Recursively pass lower and upper bounds to each subtree.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isValidBST(self, root):
        def check(node, lo, hi):
            if not node: return True
            if not (lo < node.val < hi): return False
            return check(node.left, lo, node.val) and check(node.right, node.val, hi)
        return check(root, float('-inf'), float('inf'))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterative inorder stops as soon as order is violated.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isValidBST(self, root):
        stack, prev = [], None
        node = root
        while stack or node:
            while node:
                stack.append(node); node = node.left
            node = stack.pop()
            if prev is not None and node.val <= prev: return False
            prev = node.val
            node = node.right
        return True
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Duplicates are invalid for the standard LeetCode definition.
- Ancestor bounds catch cases local checks miss.

## Related
- Kth Smallest Element in a BST
- Lowest Common Ancestor of a BST
