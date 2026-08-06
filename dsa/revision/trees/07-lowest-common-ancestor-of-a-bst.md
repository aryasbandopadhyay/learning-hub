# 07. Lowest Common Ancestor of a BST

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Lowest Common Ancestor of a BST**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [6,2,8,0,4,7,9,None,None,3,5], p = 2, q = 8
Output: 6
Explanation: The split point between p and q is 6.
```

## Understanding & Intuition
BST ordering tells us whether both targets lie left, right, or split around the current node. The first split point is the LCA.

## Approach 1 — Naive / Brute Force
**Idea:** Record both root-to-node paths, then compare the last common node.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        def path(x):
            node, out = root, []
            while node:
                out.append(node)
                if x.val == node.val: break
                node = node.left if x.val < node.val else node.right
            return out
        a, b, ans = path(p), path(q), None
        for x, y in zip(a, b):
            if x is y: ans = x
            else: break
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use recursive BST branching.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        if p.val < root.val and q.val < root.val:
            return self.lowestCommonAncestor(root.left, p, q)
        if p.val > root.val and q.val > root.val:
            return self.lowestCommonAncestor(root.right, p, q)
        return root
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterate down to the first split point.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        node = root
        while node:
            if p.val < node.val and q.val < node.val:
                node = node.left
            elif p.val > node.val and q.val > node.val:
                node = node.right
            else:
                return node
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- One node may be the ancestor of the other.
- This shortcut only works for BSTs.

## Related
- Lowest Common Ancestor of a Binary Tree
- Validate Binary Search Tree
