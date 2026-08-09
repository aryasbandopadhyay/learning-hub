# 10. Count Good Nodes in Binary Tree

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

A node is good if no ancestor from the root to that node has a value greater than it. Given the root, return the number of good nodes.

**Input**
- `root`: the root of a binary tree.

**Output**
- An integer: the count of nodes whose value is at least the maximum value on their root path.

## Constraints
- `1 <= number of nodes <= 10^5`
- `-10^4 <= Node.val <= 10^4`.

## Examples
```text
Input: root = [3,1,4,3,None,1,5]
Output: 4
Explanation: The good nodes are the root 3, the left child 3, node 4, and node 5. The nodes with value 1 are smaller than an ancestor.
```

## Understanding & Intuition
A node is good if its value is at least the maximum value on the root-to-node path. Carrying that maximum during traversal gives an immediate decision.

## Approach 1 — Naive / Brute Force
**Idea:** Store each root-to-node path and test the maximum on that path.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def goodNodes(self, root):
        ans = 0
        def dfs(node, path):
            nonlocal ans
            if not node: return
            new_path = path + [node.val]
            if node.val >= max(new_path): ans += 1
            dfs(node.left, new_path); dfs(node.right, new_path)
        dfs(root, [])
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** DFS recursively while carrying the best ancestor value.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def goodNodes(self, root):
        def dfs(node, best):
            if not node: return 0
            good = 1 if node.val >= best else 0
            best = max(best, node.val)
            return good + dfs(node.left, best) + dfs(node.right, best)
        return dfs(root, float('-inf'))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use an explicit stack of `(node, max_so_far)` pairs.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def goodNodes(self, root):
        if not root: return 0
        ans, stack = 0, [(root, float('-inf'))]
        while stack:
            node, best = stack.pop()
            if node.val >= best: ans += 1
            best = max(best, node.val)
            if node.left: stack.append((node.left, best))
            if node.right: stack.append((node.right, best))
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
- Negative values require `-inf`, not 0, as initial maximum.
- Equality still counts as good.

## Related
- Path Sum
- Binary Tree Maximum Path Sum
