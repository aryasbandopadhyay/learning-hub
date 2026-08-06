# 12. Kth Smallest Element in a BST

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Kth Smallest Element in a BST**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [3,1,4,None,2], k = 1
Output: 1
Explanation: Inorder order is [1,2,3,4].
```

## Understanding & Intuition
A BST's inorder traversal yields sorted values. We only need the kth visit, so iterative inorder can stop early.

## Approach 1 — Naive / Brute Force
**Idea:** Collect all values, sort them, and index `k-1`.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def kthSmallest(self, root, k):
        vals = []
        def dfs(node):
            if not node: return
            vals.append(node.val); dfs(node.left); dfs(node.right)
        dfs(root)
        vals.sort()
        return vals[k - 1]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build the inorder list and return the kth value.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def kthSmallest(self, root, k):
        vals = []
        def inorder(node):
            if not node: return
            inorder(node.left); vals.append(node.val); inorder(node.right)
        inorder(root)
        return vals[k - 1]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterative inorder counts nodes and stops at k.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def kthSmallest(self, root, k):
        stack, node = [], root
        while True:
            while node:
                stack.append(node); node = node.left
            node = stack.pop(); k -= 1
            if k == 0: return node.val
            node = node.right
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- `k` is 1-indexed.
- Only works because the tree is a BST.

## Related
- Validate Binary Search Tree
- Binary Tree Inorder Traversal
