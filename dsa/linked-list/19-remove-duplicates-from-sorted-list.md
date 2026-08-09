# 19. Remove Duplicates from Sorted List

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Bloomberg, Apple

## Problem
Given the head of a sorted linked list, delete duplicate values so each value appears once and return the head. Keep the first node for each value.

**Input**
- `head`: head of a sorted singly linked list.

**Output**
- The de-duplicated sorted list. This judge compares exactly by traversal order.

## Constraints
- `0 <= number of nodes <= 300`
- `-100 <= Node.val <= 100`
- The list is sorted in non-decreasing order.

## Examples
```text
Input: head = [1,1,2,3,3]
Output: [1,2,3]
Explanation: Adjacent duplicates `1` and `3` are collapsed, leaving `1 -> 2 -> 3`.
```

## Understanding & Intuition
Because the list is sorted, duplicates are adjacent. A set works but ignores the sorted property. The optimal solution only compares each node with its next node and skips equal neighbors.

## Approach 1 — Naive / Brute Force
**Idea:** Use a set to collect unique values, sort them, and build a new list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def deleteDuplicates(self, head):
        values = set()
        while head:
            values.add(head.val)
            head = head.next

        dummy = ListNode()
        tail = dummy
        for value in sorted(values):
            tail.next = ListNode(value)
            tail = tail.next
        return dummy.next
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build a new list while remembering the previous value copied.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def deleteDuplicates(self, head):
        dummy = ListNode()
        tail = dummy
        prev_value = None
        first = True
        while head:
            if first or head.val != prev_value:
                tail.next = ListNode(head.val)
                tail = tail.next
                prev_value = head.val
                first = False
            head = head.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Since duplicates are adjacent, skip `cur.next` whenever it has the same value as `cur`.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def deleteDuplicates(self, head):
        cur = head
        while cur and cur.next:
            if cur.val == cur.next.val:
                cur.next = cur.next.next
            else:
                cur = cur.next
        return head
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Empty lists remain empty.
- Duplicates are adjacent only because the list is sorted.
- After deleting a duplicate, do not advance `cur` immediately.

## Related
- Remove Linked List Elements
- Remove Duplicates from Sorted List II

