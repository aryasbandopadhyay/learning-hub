# 20. Path Sum

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree and `targetSum`, return whether any root-to-leaf path has values summing to `targetSum`. A leaf has no children.

**Input**
- `root`: the root of a binary tree, or `None`.
- `targetSum`: the required sum.

**Output**
- `True` if such a root-to-leaf path exists; otherwise `False`.

## Constraints
- `0 <= number of nodes <= 5000`
- `-1000 <= Node.val <= 1000`
- `-1000 <= targetSum <= 1000`.

## Examples
```text
Input: root = [5,4,8,11,None,13,4,7,2,None,None,None,1], targetSum = 22
Output: True
Explanation: The path `5 -> 4 -> 11 -> 2` starts at the root, ends at a leaf, and sums to 22.
```

## Understanding & Intuition
The path must start at the root and end at a leaf. Subtracting the current value leaves the required sum for children.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all root-to-leaf sums then test membership.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def hasPathSum(self, root, targetSum):
        sums=[]
        def dfs(node,total):
            if not node: return
            total += node.val
            if not node.left and not node.right: sums.append(total)
            dfs(node.left,total); dfs(node.right,total)
        dfs(root,0)
        return targetSum in sums
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Recursively subtract values and check leaves.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def hasPathSum(self, root, targetSum):
        if not root: return False
        if not root.left and not root.right: return targetSum == root.val
        return self.hasPathSum(root.left, targetSum-root.val) or self.hasPathSum(root.right, targetSum-root.val)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterate with `(node, running_sum)` pairs.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def hasPathSum(self, root, targetSum):
        if not root: return False
        stack=[(root, root.val)]
        while stack:
            node,total=stack.pop()
            if not node.left and not node.right and total == targetSum: return True
            if node.left: stack.append((node.left,total+node.left.val))
            if node.right: stack.append((node.right,total+node.right.val))
        return False
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Only leaf-ending paths count.
- Negative values mean you cannot prune by exceeding target.

## Related
- Path Sum II
- Count Good Nodes in Binary Tree
