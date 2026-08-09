# 16. Binary Tree Inorder Traversal

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree, return its inorder traversal: left subtree, current node, then right subtree.

**Input**
- `root`: the root of a binary tree, or `None`.

**Output**
- A list of values. **This judge compares exactly**, in left-root-right order.

## Constraints
- `0 <= number of nodes <= 100`
- `-100 <= Node.val <= 100`.

## Examples
```text
Input: root = [1,None,2,3]
Output: [1, 3, 2]
Explanation: Visit root 1 first because it has no left child. Then visit the right subtree's left child 3 before node 2.
```

## Understanding & Intuition
Inorder traversal defines a precise visit order. Recursive DFS mirrors the definition, while stack/Morris variants avoid recursion.

## Approach 1 — Naive / Brute Force
**Idea:** Use recursive DFS that follows the traversal definition.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def inorderTraversal(self, root):
        ans=[]
        def dfs(node):
            if not node: return
            dfs(node.left); ans.append(node.val); dfs(node.right)
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
    def inorderTraversal(self, root):
        ans, stack, node = [], [], root
        while stack or node:
            while node:
                stack.append(node); node = node.left
            node = stack.pop(); ans.append(node.val); node = node.right
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
    def inorderTraversal(self, root):
        ans=[]; cur=root
        while cur:
            if not cur.left:
                ans.append(cur.val); cur=cur.right
            else:
                pred=cur.left
                while pred.right and pred.right is not cur: pred=pred.right
                if not pred.right:
                    pred.right=cur; cur=cur.left
                else:
                    pred.right=None; ans.append(cur.val); cur=cur.right
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
