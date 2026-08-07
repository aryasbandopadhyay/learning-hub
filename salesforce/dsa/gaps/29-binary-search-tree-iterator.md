# 29. Binary Search Tree Iterator

- **Difficulty:** Medium
- **Pattern:** Tree / Stack
- **Asked at:** Salesforce, Google, Facebook

## Problem
Implement `BSTIterator` with `next()` returning the next smallest value in a BST and `hasNext()` indicating if more values exist.

## Examples
```text
Input: BSTIterator([7,3,15,null,null,9,20]), next(), next(), hasNext(), next()
Output: [null,3,7,true,9]
Explanation: Inorder traversal of a BST is sorted.
```

## Understanding & Intuition
A full inorder list is simple, but a stack of left ancestors lets the iterator pause and resume in O(h) memory.

## Approach 1 — Naive / Brute Force
**Idea:** Precompute the entire inorder traversal.
```python
class TreeNode:
    def __init__(self, val: int = 0, left: 'TreeNode | None' = None, right: 'TreeNode | None' = None):
        self.val = val; self.left = left; self.right = right
class BSTIterator:
    def __init__(self, root: TreeNode | None):
        self.values = []
        def dfs(node: TreeNode | None) -> None:
            if node: dfs(node.left); self.values.append(node.val); dfs(node.right)
        dfs(root); self.i = 0
    def next(self) -> int:
        val = self.values[self.i]; self.i += 1; return val
    def hasNext(self) -> bool:
        return self.i < len(self.values)
```
- **Time:** O(n) init, O(1) operations — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a generator and one-value buffer.
```python
class BSTIterator:
    def __init__(self, root: TreeNode | None):
        self.it = self._inorder(root); self.peek = None; self.ready = False
    def _inorder(self, node: TreeNode | None):
        if node:
            yield from self._inorder(node.left); yield node.val; yield from self._inorder(node.right)
    def hasNext(self) -> bool:
        if self.ready: return True
        try:
            self.peek = next(self.it); self.ready = True; return True
        except StopIteration: return False
    def next(self) -> int:
        if not self.hasNext(): raise StopIteration
        self.ready = False; return self.peek
```
- **Time:** O(1) amortized — **Space:** O(h)

## Approach 3 — Optimal
**Idea:** Maintain a stack of the next node's left spine.
```python
class BSTIterator:
    def __init__(self, root: TreeNode | None):
        self.stack = []; self._push_left(root)
    def _push_left(self, node: TreeNode | None) -> None:
        while node:
            self.stack.append(node); node = node.left
    def next(self) -> int:
        node = self.stack.pop(); self._push_left(node.right); return node.val
    def hasNext(self) -> bool:
        return bool(self.stack)
```
- **Time:** O(1) amortized — **Space:** O(h)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) init, O(1) operations | O(n) |
| Better | O(1) amortized | O(h) |
| Optimal | O(1) amortized | O(h) |

## Edge Cases & Pitfalls
- Initialize by pushing the root's left spine.
- After popping, push the right child's left spine.
- `hasNext()` should not advance traversal.

## Related
- Binary Tree Inorder Traversal
- Flatten Nested List Iterator
