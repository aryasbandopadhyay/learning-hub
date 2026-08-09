# 17. Binary Tree Preorder Traversal

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree, return its preorder traversal: current node, left subtree, then right subtree.

**Input**
- `root`: the root of a binary tree, or `None`.

**Output**
- A list of values. **This judge compares exactly**, in root-left-right order.

## Constraints
- `0 <= number of nodes <= 100`
- `-100 <= Node.val <= 100`.

## Examples
```text
Input: root = [1,None,2,3]
Output: [1, 2, 3]
Explanation: Visit root 1 first, then the right subtree because there is no left child. Within that subtree, visit 2 before 3.
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
