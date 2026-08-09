# 15. Serialize and Deserialize Binary Tree

- **Difficulty:** Hard
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Design methods to serialize and deserialize a binary tree.

`serialize(root)` should convert the tree into a string representation. `deserialize(data)` should rebuild and return a binary tree with the same structure and node values. The exact internal string format is up to you as long as your two methods are compatible and can represent null children unambiguously.

**Input**
- `root`: the root of a binary tree for `serialize`.
- `data`: a string previously produced by `serialize`, for `deserialize`.

**Output**
- `serialize` returns a string. `deserialize` returns the root of a tree that serializes to the same structure and values as the original tree.

## Constraints
- `0 <= number of nodes <= 10^4`
- `-1000 <= Node.val <= 1000`
- The tree may be empty, balanced, skewed, or contain duplicate values.

## Examples
```text
Input: root = [1,2,3,None,None,4,5]
Output: [1,2,3,None,None,4,5]
Explanation: The serialized data must include enough null-child information to distinguish this shape from other trees. Deserializing that data reconstructs the same level-order structure and values.
```

```text
Input: root = []
Output: []
Explanation: An empty tree can be serialized with an empty/null marker and deserialized back to no root node.
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
