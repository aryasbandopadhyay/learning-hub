# 22. Lowest Common Ancestor of a Binary Tree

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the root of a binary tree and two nodes `p` and `q` in that tree, return their lowest common ancestor.

Unlike a binary search tree, this tree has no ordering rule. The lowest common ancestor is the deepest node that has both `p` and `q` as descendants; a node is allowed to be a descendant of itself.

**Input**
- `root`: the root of a binary tree.
- `p`: one node in the tree.
- `q`: another node in the tree.

**Output**
- The tree node that is the lowest common ancestor of `p` and `q`.

## Constraints
- `2 <= number of nodes <= 10^5`
- `-10^9 <= Node.val <= 10^9`
- All node values are unique.
- `p` and `q` are distinct nodes that both exist in the tree.

## Examples
```text
Input: root = [3,5,1,6,2,0,8,None,None,7,4], p = 5, q = 1
Output: 3
Explanation: Node `5` is in the left subtree of `3`, and node `1` is in the right subtree of `3`. No lower node contains both targets, so `3` is the lowest common ancestor.
```

```text
Input: root = [3,5,1,6,2,0,8,None,None,7,4], p = 5, q = 4
Output: 5
Explanation: A node can be an ancestor of itself, and `5` contains node `4` in its subtree.
```

## Understanding & Intuition
Without BST ordering, each subtree reports whether it contains either target. The first node receiving hits from both sides is the LCA.

## Approach 1 — Naive / Brute Force
**Idea:** Build root-to-node paths and compare them.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        def find(node,target,path):
            if not node: return None
            if node is target: return path + [node]
            return find(node.left,target,path+[node]) or find(node.right,target,path+[node])
        a,b=find(root,p,[]),find(root,q,[]); ans=None
        for x,y in zip(a,b):
            if x is y: ans=x
            else: break
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Recursive postorder returns the found target or LCA.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        if not root or root is p or root is q: return root
        left = self.lowestCommonAncestor(root.left,p,q)
        right = self.lowestCommonAncestor(root.right,p,q)
        if left and right: return root
        return left or right
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store parent pointers iteratively, then walk ancestors.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        parent={root: None}; stack=[root]
        while p not in parent or q not in parent:
            node=stack.pop()
            if node.left: parent[node.left]=node; stack.append(node.left)
            if node.right: parent[node.right]=node; stack.append(node.right)
        seen=set()
        while p:
            seen.add(p); p=parent[p]
        while q not in seen:
            q=parent[q]
        return q
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- One target can be ancestor of the other.
- This assumes both nodes exist in the tree.

## Related
- Lowest Common Ancestor of a BST
- Same Tree
