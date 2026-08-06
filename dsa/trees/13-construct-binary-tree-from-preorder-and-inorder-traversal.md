# 13. Construct Binary Tree from Preorder and Inorder Traversal

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Construct Binary Tree from Preorder and Inorder Traversal**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
Output: [3,9,20,None,None,15,7]
Explanation: Preorder chooses roots; inorder splits left and right subtrees.
```

## Understanding & Intuition
Preorder gives the next root, while inorder tells how many nodes belong to each side. A hash map avoids repeated inorder scans.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively slice arrays around the root value.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right
class Solution:
    def buildTree(self, preorder, inorder):
        # Slicing is simple but copies subarrays each call.
        if not preorder:
            return None
        root_val = preorder[0]
        mid = inorder.index(root_val)
        root = TreeNode(root_val)
        root.left = self.buildTree(preorder[1:1 + mid], inorder[:mid])
        root.right = self.buildTree(preorder[1 + mid:], inorder[mid + 1:])
        return root

def to_level(root):
    from collections import deque
    if not root: return []
    out, q = [], deque([root])
    while q:
        node = q.popleft(); out.append(None if node is None else node.val)
        if node:
            q.append(node.left); q.append(node.right)
    while out and out[-1] is None: out.pop()
    return out
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use inorder indexes and a preorder pointer to avoid slicing.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right
class Solution:
    def buildTree(self, preorder, inorder):
        pos = {v: i for i, v in enumerate(inorder)}
        pre_i = 0
        def build(lo, hi):
            nonlocal pre_i
            if lo > hi: return None
            val = preorder[pre_i]; pre_i += 1
            root = TreeNode(val); mid = pos[val]
            root.left = build(lo, mid - 1)
            root.right = build(mid + 1, hi)
            return root
        return build(0, len(inorder) - 1)

def to_level(root):
    from collections import deque
    if not root: return []
    out, q = [], deque([root])
    while q:
        node = q.popleft(); out.append(None if node is None else node.val)
        if node:
            q.append(node.left); q.append(node.right)
    while out and out[-1] is None: out.pop()
    return out
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use preorder index ranges plus the inorder map for explicit subtree sizes.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right
class Solution:
    def buildTree(self, preorder, inorder):
        pos = {v: i for i, v in enumerate(inorder)}
        def build(pre_l, pre_r, in_l, in_r):
            if pre_l > pre_r: return None
            root_val = preorder[pre_l]
            mid = pos[root_val]
            left_size = mid - in_l
            root = TreeNode(root_val)
            root.left = build(pre_l + 1, pre_l + left_size, in_l, mid - 1)
            root.right = build(pre_l + left_size + 1, pre_r, mid + 1, in_r)
            return root
        return build(0, len(preorder) - 1, 0, len(inorder) - 1)

def to_level(root):
    from collections import deque
    if not root: return []
    out, q = [], deque([root])
    while q:
        node = q.popleft(); out.append(None if node is None else node.val)
        if node:
            q.append(node.left); q.append(node.right)
    while out and out[-1] is None: out.pop()
    return out
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Values must be unique for this reconstruction.
- Avoid O(n^2) `index` scans in large trees.

## Related
- Binary Tree Preorder Traversal
- Binary Tree Inorder Traversal
