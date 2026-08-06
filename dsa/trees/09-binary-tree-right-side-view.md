# 09. Binary Tree Right Side View

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Binary Tree Right Side View**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [1,2,3,None,5,None,4]
Output: [1,3,4]
Explanation: These are the rightmost nodes at each depth.
```

## Understanding & Intuition
The visible node at a depth is the last node visited left-to-right at that depth. BFS can take the final item in each level; DFS can visit right first.

## Approach 1 — Naive / Brute Force
**Idea:** Build all levels, then take each level's last value.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def rightSideView(self, root):
        from collections import deque
        if not root: return []
        levels, q = [], deque([root])
        while q:
            row = []
            for _ in range(len(q)):
                node = q.popleft(); row.append(node.val)
                if node.left: q.append(node.left)
                if node.right: q.append(node.right)
            levels.append(row)
        return [row[-1] for row in levels]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use right-first DFS and record the first node seen at each depth.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def rightSideView(self, root):
        ans = []
        def dfs(node, depth):
            if not node: return
            if depth == len(ans): ans.append(node.val)
            dfs(node.right, depth + 1); dfs(node.left, depth + 1)
        dfs(root, 0)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** BFS level by level and append only the last node value.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def rightSideView(self, root):
        from collections import deque
        if not root: return []
        ans, q = [], deque([root])
        while q:
            size = len(q)
            for i in range(size):
                node = q.popleft()
                if node.left: q.append(node.left)
                if node.right: q.append(node.right)
                if i == size - 1:
                    ans.append(node.val)
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
- Left children can be visible if no right node exists at that depth.
- Do not append every right child blindly.

## Related
- Binary Tree Level Order Traversal
- Binary Tree Zigzag Level Order Traversal
