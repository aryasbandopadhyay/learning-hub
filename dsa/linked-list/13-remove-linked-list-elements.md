# 13. Remove Linked List Elements

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Apple, Adobe

## Problem
Given the head of a linked list and an integer `val`, remove all nodes whose value equals `val` and return the new head. The list has up to `10^4` nodes.

## Examples
```text
Input: head = [1,2,6,3,4,5,6], val = 6
Output: [1,2,3,4,5]
Explanation: Both nodes with value 6 are removed.
```

## Understanding & Intuition
The difficulty is handling deletions at the head as well as in the middle. A dummy node makes every deletion look like deleting after a previous node. We can either build a new list or relink existing nodes.

## Approach 1 — Naive / Brute Force
**Idea:** Copy kept values into a new linked list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def removeElements(self, head, val):
        dummy = ListNode()
        tail = dummy
        cur = head
        while cur:
            if cur.val != val:
                tail.next = ListNode(cur.val)
                tail = tail.next
            cur = cur.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** First advance the head past removed values, then delete matching nodes after it.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def removeElements(self, head, val):
        while head and head.val == val:
            head = head.next
        cur = head
        while cur and cur.next:
            if cur.next.val == val:
                cur.next = cur.next.next
            else:
                cur = cur.next
        return head
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use a dummy node so head and non-head deletions share the same logic.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def removeElements(self, head, val):
        dummy = ListNode(0, head)
        cur = dummy
        while cur.next:
            if cur.next.val == val:
                cur.next = cur.next.next
            else:
                cur = cur.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- All nodes may be removed.
- Consecutive matching nodes require not advancing after deletion.
- A dummy node avoids special casing the original head.

## Related
- Remove Duplicates from Sorted List
- Remove Nth Node From End of List

