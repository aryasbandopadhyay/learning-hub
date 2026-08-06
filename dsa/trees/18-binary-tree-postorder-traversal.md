# 18. Binary Tree Postorder Traversal

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Binary Tree Postorder Traversal**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [1,None,2,3]
Output: [3, 2, 1]
Explanation: left-right-root order visits nodes this way.
```

## Understanding & Intuition
Postorder traversal defines a precise visit order. Recursive DFS mirrors the definition, while stack/Morris variants avoid recursion.

## Approach 1 — Naive / Brute Force
**Idea:** Use recursive DFS that follows the traversal definition.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def postorderTraversal(self, root):
        ans=[]
        def dfs(node):
            if not node: return
            dfs(node.left); dfs(node.right); ans.append(node.val)
        dfs(root)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Simulate recursion with an explicit stack.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def postorderTraversal(self, root):
        if not root: return []
        ans=[]; stack=[(root, False)]
        while stack:
            node, seen=stack.pop()
            if not node: continue
            if seen: ans.append(node.val)
            else:
                stack.append((node, True)); stack.append((node.right, False)); stack.append((node.left, False))
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a refined iterative/Morris-style traversal where applicable.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def postorderTraversal(self, root):
        ans=[]; stack=[]; last=None; cur=root
        while stack or cur:
            if cur:
                stack.append(cur); cur=cur.left
            else:
                peek=stack[-1]
                if peek.right and last is not peek.right:
                    cur=peek.right
                else:
                    ans.append(peek.val); last=stack.pop()
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
- Return [] for an empty tree.
- Be careful with push order in stack-based traversal.

## Related
- Binary Tree Level Order Traversal
- Validate Binary Search Tree
