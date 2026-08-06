# 17. Binary Tree Preorder Traversal

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Binary Tree Preorder Traversal**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [1,None,2,3]
Output: [1, 2, 3]
Explanation: root-left-right order visits nodes this way.
```

## Understanding & Intuition
Preorder traversal defines a precise visit order. Recursive DFS mirrors the definition, while stack/Morris variants avoid recursion.

## Approach 1 — Naive / Brute Force
**Idea:** Use recursive DFS that follows the traversal definition.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def preorderTraversal(self, root):
        ans=[]
        def dfs(node):
            if not node: return
            ans.append(node.val); dfs(node.left); dfs(node.right)
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
    def preorderTraversal(self, root):
        if not root: return []
        ans=[]; stack=[root]
        while stack:
            node=stack.pop(); ans.append(node.val)
            if node.right: stack.append(node.right)
            if node.left: stack.append(node.left)
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
    def preorderTraversal(self, root):
        ans=[]; cur=root
        while cur:
            if not cur.left:
                ans.append(cur.val); cur=cur.right
            else:
                pred=cur.left
                while pred.right and pred.right is not cur: pred=pred.right
                if not pred.right:
                    ans.append(cur.val); pred.right=cur; cur=cur.left
                else:
                    pred.right=None; cur=cur.right
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
