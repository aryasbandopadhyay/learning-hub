# 18. Intersection of Two Linked Lists

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Bloomberg, Adobe

## Problem
Given the heads of two singly linked lists, return the node where they intersect, or `None` if they do not intersect. Intersection is by node reference, not by value, and the lists must remain unchanged.

## Examples
```text
Input: listA = [4,1,8,4,5], listB = [5,6,1,8,4,5], intersectVal = 8
Output: Intersected at '8'
Explanation: Both lists share the same node object with value 8.
```

## Understanding & Intuition
If two lists intersect, their tails from the intersection onward are identical node objects. A set of nodes from one list makes detection easy. The two-pointer switching trick equalizes path lengths without extra memory.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every node in list A with every node in list B by identity.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def getIntersectionNode(self, headA, headB):
        a = headA
        while a:
            b = headB
            while b:
                if a is b:
                    return a
                b = b.next
            a = a.next
        return None
```
- **Time:** O(mn) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Store all nodes from one list in a set, then scan the other list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def getIntersectionNode(self, headA, headB):
        seen = set()
        while headA:
            seen.add(headA)
            headA = headA.next
        while headB:
            if headB in seen:
                return headB
            headB = headB.next
        return None
```
- **Time:** O(m+n) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Traverse both lists; when a pointer ends, redirect it to the other head.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def getIntersectionNode(self, headA, headB):
        a, b = headA, headB
        while a is not b:
            a = headB if a is None else a.next
            b = headA if b is None else b.next
        return a
```
- **Time:** O(m+n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(1) |
| Better | O(m+n) | O(m) |
| Optimal | O(m+n) | O(1) |

## Edge Cases & Pitfalls
- Compare node identity, not values.
- No intersection returns `None` after both pointers become `None`.
- Do not modify list links to detect intersection.

## Related
- Linked List Cycle
- Linked List Cycle II

