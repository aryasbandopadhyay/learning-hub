# 07. Lowest Common Ancestor of a BST

- **Difficulty:** Medium
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the root of a binary search tree and two nodes `p` and `q` in that tree, return their lowest common ancestor.

In a binary search tree, all values in a node's left subtree are smaller than the node's value, and all values in its right subtree are larger. The lowest common ancestor is the deepest node that has both `p` and `q` as descendants; a node can be a descendant of itself.

**Input**
- `root`: the root of a binary search tree.
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
Input: root = [6,2,8,0,4,7,9,None,None,3,5], p = 2, q = 8
Output: 6
Explanation: Node `2` lies in the left subtree of `6`, and node `8` lies in the right subtree of `6`. The split happens at `6`, so `6` is their lowest common ancestor.
```

```text
Input: root = [6,2,8,0,4,7,9,None,None,3,5], p = 2, q = 4
Output: 2
Explanation: A node can be an ancestor of itself, and `2` is above `4`.
```

## Understanding & Intuition
BST ordering tells us whether both targets lie left, right, or split around the current node. The first split point is the LCA.

## Approach 1 — Naive / Brute Force
**Idea:** Record both root-to-node paths, then compare the last common node.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        def path(x):
            node, out = root, []
            while node:
                out.append(node)
                if x.val == node.val: break
                node = node.left if x.val < node.val else node.right
            return out
        a, b, ans = path(p), path(q), None
        for x, y in zip(a, b):
            if x is y: ans = x
            else: break
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use recursive BST branching.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        if p.val < root.val and q.val < root.val:
            return self.lowestCommonAncestor(root.left, p, q)
        if p.val > root.val and q.val > root.val:
            return self.lowestCommonAncestor(root.right, p, q)
        return root
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterate down to the first split point.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def lowestCommonAncestor(self, root, p, q):
        node = root
        while node:
            if p.val < node.val and q.val < node.val:
                node = node.left
            elif p.val > node.val and q.val > node.val:
                node = node.right
            else:
                return node
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- One node may be the ancestor of the other.
- This shortcut only works for BSTs.

## Related
- Lowest Common Ancestor of a Binary Tree
- Validate Binary Search Tree
