# 24. Convert Sorted Array to Binary Search Tree

- **Difficulty:** Easy
- **Pattern:** Trees
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given the described binary tree/BST input, solve **Convert Sorted Array to Binary Search Tree**. Constraints: number of nodes is 0 to 10^5 unless stated otherwise; node values fit in signed 32-bit integers; recursion depth may be O(n) for skewed trees.

## Examples
```text
Input: nums = [-10,-3,0,5,9]
Output: [0,-10,5,None,-3,None,9]
Explanation: Choosing middle elements keeps the BST height-balanced.
```

## Understanding & Intuition
A sorted array is the inorder order of a BST. Choosing the middle as root recursively balances both sides.

## Approach 1 — Naive / Brute Force
**Idea:** Insert values one by one into a BST, then note it may become skewed.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right
class Solution:
    def sortedArrayToBST(self, nums):
        def insert(root,val):
            if not root: return TreeNode(val)
            if val < root.val: root.left=insert(root.left,val)
            else: root.right=insert(root.right,val)
            return root
        root=None
        for v in nums: root=insert(root,v)
        return root
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Recursively choose the middle element using slices.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right
class Solution:
    def sortedArrayToBST(self, nums):
        if not nums: return None
        mid=len(nums)//2
        root=TreeNode(nums[mid])
        root.left=self.sortedArrayToBST(nums[:mid])
        root.right=self.sortedArrayToBST(nums[mid+1:])
        return root
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use index bounds to avoid slicing copies.
```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right
class Solution:
    def sortedArrayToBST(self, nums):
        def build(lo,hi):
            if lo > hi: return None
            mid=(lo+hi)//2
            root=TreeNode(nums[mid])
            root.left=build(lo,mid-1); root.right=build(mid+1,hi)
            return root
        return build(0,len(nums)-1)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The exact level order can vary with left/right middle choice.
- Naive insertion is not height-balanced for sorted input.

## Related
- Validate Binary Search Tree
- Binary Tree Inorder Traversal
