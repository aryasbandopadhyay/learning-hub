# 25. Binary Tree Zigzag Level Order Traversal

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree, return its zigzag level-order traversal: top to bottom, alternating left-to-right and right-to-left at each level.

**Input**
- `root`: the root of a binary tree, or `None`.

**Output**
- A list of levels. **This judge compares exactly**, so preserve top-to-bottom order and alternate each level's direction.

## Constraints
- `0 <= number of nodes <= 2000`
- `-100 <= Node.val <= 100`.

## Examples
```text
Input: root = [3,9,20,None,None,15,7]
Output: [[3],[20,9],[15,7]]
Explanation: Level 0 is `[3]`, level 1 is reversed to `[20,9]`, and level 2 returns to left-to-right as `[15,7]`.
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
