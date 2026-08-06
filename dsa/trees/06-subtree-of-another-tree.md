# 06. Subtree of Another Tree

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Subtree of Another Tree**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [3,4,5,1,2], subRoot = [4,1,2]
Output: True
Explanation: The subtree rooted at value 4 matches subRoot.
```

## Understanding & Intuition
A subtree match starts at some node and then requires exact equality. Serialization can reduce matching to substring search when null markers are included.

## Approach 1 — Naive / Brute Force
**Idea:** At every root node, recursively test whether the two trees are identical.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSubtree(self, root, subRoot):
        def same(a, b):
            if not a or not b: return a is b
            return a.val == b.val and same(a.left, b.left) and same(a.right, b.right)
        if not root: return subRoot is None
        return same(root, subRoot) or self.isSubtree(root.left, subRoot) or self.isSubtree(root.right, subRoot)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Serialize with null markers and separators, then search the serialized text.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSubtree(self, root, subRoot):
        def ser(node):
            if not node: return ',#'
            return ',' + str(node.val) + ser(node.left) + ser(node.right)
        return ser(subRoot) in ser(root)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use iterative traversal for candidate roots plus recursive equality.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isSubtree(self, root, subRoot):
        def same(a, b):
            if not a or not b: return a is b
            return a.val == b.val and same(a.left, b.left) and same(a.right, b.right)
        stack = [root]
        while stack:
            node = stack.pop()
            if not node: continue
            if node.val == subRoot.val and same(node, subRoot): return True
            stack.append(node.left); stack.append(node.right)
        return subRoot is None
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Include nulls in serialization to avoid false positives.
- Handle an empty `subRoot` explicitly.

## Related
- Same Tree
- Serialize and Deserialize Binary Tree
