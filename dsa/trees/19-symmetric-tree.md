# 19. Symmetric Tree

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Symmetric Tree**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [1,2,2,3,4,4,3]
Output: True
Explanation: The left and right subtrees are mirrors.
```

## Understanding & Intuition
Symmetry compares opposite children rather than matching same-side children. Pairwise traversal is the core idea.

## Approach 1 — Naive / Brute Force
**Idea:** Invert a clone of one side and compare it with the other side.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSymmetric(self, root):
        def clone_inv(node):
            if not node: return None
            return TreeNode(node.val, clone_inv(node.right), clone_inv(node.left))
        def same(a,b):
            if not a or not b: return a is b
            return a.val == b.val and same(a.left,b.left) and same(a.right,b.right)
        return True if not root else same(clone_inv(root.left), root.right)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Recursively compare mirror pairs.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSymmetric(self, root):
        def mirror(a,b):
            if not a or not b: return a is b
            return a.val == b.val and mirror(a.left,b.right) and mirror(a.right,b.left)
        return mirror(root.left, root.right) if root else True
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a queue of node pairs to compare mirrors iteratively.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSymmetric(self, root):
        from collections import deque
        q=deque([(root.left, root.right)]) if root else deque()
        while q:
            a,b=q.popleft()
            if not a or not b:
                if a is not b: return False
                continue
            if a.val != b.val: return False
            q.append((a.left,b.right)); q.append((a.right,b.left))
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
- A single root is symmetric.
- Compare outer with outer and inner with inner.

## Related
- Same Tree
- Invert Binary Tree
