# 25. Binary Tree Zigzag Level Order Traversal

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Binary Tree Zigzag Level Order Traversal**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [3,9,20,None,None,15,7]
Output: [[3],[20,9],[15,7]]
Explanation: Direction alternates after each level.
```

## Understanding & Intuition
Zigzag is level order with alternating output direction. A deque can append values on either end without reversing each row.

## Approach 1 — Naive / Brute Force
**Idea:** Run normal BFS levels, then reverse every odd-indexed level.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def zigzagLevelOrder(self, root):
        from collections import deque
        if not root: return []
        levels=[]; q=deque([root])
        while q:
            row=[]
            for _ in range(len(q)):
                node=q.popleft(); row.append(node.val)
                if node.left: q.append(node.left)
                if node.right: q.append(node.right)
            levels.append(row)
        for i in range(1,len(levels),2): levels[i].reverse()
        return levels
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** DFS by depth and append left/right depending on parity.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def zigzagLevelOrder(self, root):
        from collections import deque
        ans=[]
        def dfs(node, depth):
            if not node: return
            if depth == len(ans): ans.append(deque())
            if depth % 2 == 0: ans[depth].append(node.val)
            else: ans[depth].appendleft(node.val)
            dfs(node.left, depth+1); dfs(node.right, depth+1)
        dfs(root,0)
        return [list(row) for row in ans]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** BFS levels while pushing values into a deque in the correct direction.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def zigzagLevelOrder(self, root):
        from collections import deque
        if not root: return []
        ans=[]; q=deque([root]); left=True
        while q:
            row=deque()
            for _ in range(len(q)):
                node=q.popleft()
                if left: row.append(node.val)
                else: row.appendleft(node.val)
                if node.left: q.append(node.left)
                if node.right: q.append(node.right)
            ans.append(list(row)); left = not left
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
- Only output order changes, not child visitation order.
- Convert deques to lists for LeetCode output.

## Related
- Binary Tree Level Order Traversal
- Binary Tree Right Side View
