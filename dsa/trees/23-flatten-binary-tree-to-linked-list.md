# 23. Flatten Binary Tree to Linked List

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Binary trees are represented in level-order arrays, using `None` for missing children.

Given the root of a binary tree, flatten it in place into a linked list using the same nodes. The list follows preorder traversal using only `right` pointers, and every `left` pointer must become `None`.

**Input**
- `root`: the root of a binary tree, or `None`.

**Output**
- The flattened tree. **This judge compares exactly** using level-order serialization of the right-only chain.

## Constraints
- `0 <= number of nodes <= 2000`
- `-100 <= Node.val <= 100`.

## Examples
```text
Input: root = [1,2,5,3,4,None,6]
Output: [1,None,2,None,3,None,4,None,5,None,6]
Explanation: Preorder visits `1,2,3,4,5,6`. After flattening, those values appear along successive right pointers, with `None` left children.
```

## Understanding & Intuition
Flattening follows preorder order and rewires nodes in place. Reverse preorder or Morris-style rewiring can do this without collecting all nodes.

## Approach 1 — Naive / Brute Force
**Idea:** Collect preorder nodes, then relink them as a right-only chain.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def flatten(self, root):
        nodes=[]
        def pre(node):
            if not node: return
            nodes.append(node); pre(node.left); pre(node.right)
        pre(root)
        for i,node in enumerate(nodes):
            node.left=None
            node.right = nodes[i+1] if i+1 < len(nodes) else None
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use reverse preorder recursion with a previous pointer.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def flatten(self, root):
        prev=None
        def dfs(node):
            nonlocal prev
            if not node: return
            dfs(node.right); dfs(node.left)
            node.right=prev; node.left=None; prev=node
        dfs(root)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iteratively splice each left subtree between node and right subtree.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def flatten(self, root):
        cur=root
        while cur:
            if cur.left:
                pred=cur.left
                while pred.right: pred=pred.right
                pred.right=cur.right
                cur.right=cur.left
                cur.left=None
            cur=cur.right
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The result uses `right` pointers only.
- Do not allocate new tree nodes.

## Related
- Binary Tree Preorder Traversal
- Convert Sorted Array to Binary Search Tree
