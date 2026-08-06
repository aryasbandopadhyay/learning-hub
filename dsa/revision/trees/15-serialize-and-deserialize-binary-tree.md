# 15. Serialize and Deserialize Binary Tree

- **Difficulty:** Hard
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Serialize and Deserialize Binary Tree**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: root = [1,2,3,None,None,4,5]
Output: [1,2,3,None,None,4,5]
Explanation: Deserializing the serialized string rebuilds the same tree.
```

## Understanding & Intuition
Serialization must preserve both values and null structure. Preorder with null markers is compact and easy to decode recursively.

## Approach 1 — Naive / Brute Force
**Idea:** Use level-order text with explicit null markers.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def build(vals):
    from collections import deque
    if not vals or vals[0] is None: return None
    root=TreeNode(vals[0]); q=deque([root]); i=1
    while q and i < len(vals):
        node=q.popleft()
        if i < len(vals) and vals[i] is not None:
            node.left=TreeNode(vals[i]); q.append(node.left)
        i += 1
        if i < len(vals) and vals[i] is not None:
            node.right=TreeNode(vals[i]); q.append(node.right)
        i += 1
    return root

def to_level(root):
    from collections import deque
    if not root: return []
    out=[]; q=deque([root])
    while q:
        node=q.popleft(); out.append(None if node is None else node.val)
        if node:
            q.append(node.left); q.append(node.right)
    while out and out[-1] is None: out.pop()
    return out
class Codec:
    def serialize(self, root):
        from collections import deque
        if not root: return ''
        out=[]; q=deque([root])
        while q:
            node=q.popleft()
            if node:
                out.append(str(node.val)); q.append(node.left); q.append(node.right)
            else:
                out.append('#')
        return ','.join(out)
    def deserialize(self, data):
        from collections import deque
        if not data: return None
        vals=data.split(','); root=TreeNode(int(vals[0])); q=deque([root]); i=1
        while q and i < len(vals):
            node=q.popleft()
            if vals[i] != '#': node.left=TreeNode(int(vals[i])); q.append(node.left)
            i += 1
            if i < len(vals) and vals[i] != '#': node.right=TreeNode(int(vals[i])); q.append(node.right)
            i += 1
        return root
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use recursive preorder with null markers.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def build(vals):
    from collections import deque
    if not vals or vals[0] is None: return None
    root=TreeNode(vals[0]); q=deque([root]); i=1
    while q and i < len(vals):
        node=q.popleft()
        if i < len(vals) and vals[i] is not None:
            node.left=TreeNode(vals[i]); q.append(node.left)
        i += 1
        if i < len(vals) and vals[i] is not None:
            node.right=TreeNode(vals[i]); q.append(node.right)
        i += 1
    return root

def to_level(root):
    from collections import deque
    if not root: return []
    out=[]; q=deque([root])
    while q:
        node=q.popleft(); out.append(None if node is None else node.val)
        if node:
            q.append(node.left); q.append(node.right)
    while out and out[-1] is None: out.pop()
    return out
class Codec:
    def serialize(self, root):
        vals=[]
        def dfs(node):
            if not node:
                vals.append('#'); return
            vals.append(str(node.val)); dfs(node.left); dfs(node.right)
        dfs(root)
        return ','.join(vals)
    def deserialize(self, data):
        vals=iter(data.split(','))
        def dfs():
            v=next(vals)
            if v == '#': return None
            node=TreeNode(int(v)); node.left=dfs(); node.right=dfs(); return node
        return dfs()
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use iterative preorder serialization and recursive index-based decoding.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def build(vals):
    from collections import deque
    if not vals or vals[0] is None: return None
    root=TreeNode(vals[0]); q=deque([root]); i=1
    while q and i < len(vals):
        node=q.popleft()
        if i < len(vals) and vals[i] is not None:
            node.left=TreeNode(vals[i]); q.append(node.left)
        i += 1
        if i < len(vals) and vals[i] is not None:
            node.right=TreeNode(vals[i]); q.append(node.right)
        i += 1
    return root

def to_level(root):
    from collections import deque
    if not root: return []
    out=[]; q=deque([root])
    while q:
        node=q.popleft(); out.append(None if node is None else node.val)
        if node:
            q.append(node.left); q.append(node.right)
    while out and out[-1] is None: out.pop()
    return out
class Codec:
    def serialize(self, root):
        vals=[]; stack=[root]
        while stack:
            node=stack.pop()
            if not node:
                vals.append('#'); continue
            vals.append(str(node.val)); stack.append(node.right); stack.append(node.left)
        return ','.join(vals)
    def deserialize(self, data):
        vals=data.split(','); i=0
        def dfs():
            nonlocal i
            v=vals[i]; i += 1
            if v == '#': return None
            node=TreeNode(int(v)); node.left=dfs(); node.right=dfs(); return node
        return dfs()
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Null markers are required to preserve shape.
- Handle an empty string or single null tree.

## Related
- Subtree of Another Tree
- Construct Binary Tree from Preorder and Inorder Traversal
