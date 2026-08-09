# 21. Path Sum II

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree and `targetSum`, return all root-to-leaf paths whose values sum to `targetSum`.

**Input**
- `root`: the root of a binary tree, or `None`.
- `targetSum`: the required sum.

**Output**
- A list of paths. **This judge compares exactly**, so return paths in left-to-right DFS order, with values from root to leaf.

## Constraints
- `0 <= number of nodes <= 5000`
- `-1000 <= Node.val <= 1000`
- `-1000 <= targetSum <= 1000`.

## Examples
```text
Input: root = [5,4,8,11,None,13,4,7,2,None,None,5,1], targetSum = 22
Output: [[5,4,11,2],[5,8,4,5]]
Explanation: Two root-to-leaf paths sum to 22: `5 -> 4 -> 11 -> 2` and `5 -> 8 -> 4 -> 5`, returned left-to-right.
```

## Understanding & Intuition
This is Path Sum with path reconstruction. Backtracking avoids copying too much state, while iterative traversal carries path lists explicitly.

## Approach 1 — Naive / Brute Force
**Idea:** Collect every root-to-leaf path and filter by sum.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def pathSum(self, root, targetSum):
        paths=[]
        def dfs(node,path):
            if not node: return
            path = path + [node.val]
            if not node.left and not node.right: paths.append(path)
            dfs(node.left,path); dfs(node.right,path)
        dfs(root,[])
        return [p for p in paths if sum(p) == targetSum]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Backtrack with remaining sum and append only valid paths.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def pathSum(self, root, targetSum):
        ans=[]; path=[]
        def dfs(node, rem):
            if not node: return
            path.append(node.val); rem -= node.val
            if not node.left and not node.right and rem == 0: ans.append(path[:])
            dfs(node.left, rem); dfs(node.right, rem)
            path.pop()
        dfs(root, targetSum)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use an explicit stack carrying current path and sum.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def pathSum(self, root, targetSum):
        if not root: return []
        ans=[]; stack=[(root, root.val, [root.val])]
        while stack:
            node,total,path=stack.pop()
            if not node.left and not node.right and total == targetSum: ans.append(path)
            if node.right: stack.append((node.right,total+node.right.val,path+[node.right.val]))
            if node.left: stack.append((node.left,total+node.left.val,path+[node.left.val]))
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Return all paths, not just one.
- Copy the path before storing it when backtracking.

## Related
- Path Sum
- Binary Tree Maximum Path Sum
