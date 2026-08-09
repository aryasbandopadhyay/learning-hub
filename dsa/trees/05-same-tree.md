# 05. Same Tree

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given roots `p` and `q`, return whether the two binary trees are identical in both structure and node values.

**Input**
- `p`: root of the first tree, or `None`.
- `q`: root of the second tree, or `None`.

**Output**
- `True` if both trees match exactly; otherwise `False`.

## Constraints
- `0 <= number of nodes in each tree <= 100`
- `-10^4 <= Node.val <= 10^4`.

## Examples
```text
Input: p = [1,2,3], q = [1,2,3]
Output: True
Explanation: Both trees have root 1, left child 2, and right child 3. Every corresponding position and value matches.
```

## Understanding & Intuition
Two trees are identical only if their roots match and their corresponding children match. Serialization is convenient, but direct pairwise traversal is cleaner.

## Approach 1 — Naive / Brute Force
**Idea:** Serialize both trees including null markers, then compare strings.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSameTree(self, p, q):
        def ser(node):
            if not node: return ['#']
            return [str(node.val)] + ser(node.left) + ser(node.right)
        return ser(p) == ser(q)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use recursive structural comparison.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSameTree(self, p, q):
        if not p or not q:
            return p is q
        return p.val == q.val and self.isSameTree(p.left, q.left) and self.isSameTree(p.right, q.right)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Compare node pairs with an explicit stack.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSameTree(self, p, q):
        stack = [(p, q)]
        while stack:
            a, b = stack.pop()
            if not a or not b:
                if a is not b: return False
                continue
            if a.val != b.val: return False
            stack.append((a.left, b.left)); stack.append((a.right, b.right))
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
- Both `None` roots are the same.
- Values can match while structure differs.

## Related
- Subtree of Another Tree
- Symmetric Tree
