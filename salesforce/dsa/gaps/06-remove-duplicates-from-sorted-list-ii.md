# 06. Remove Duplicates from Sorted List II

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Salesforce, Amazon, Microsoft

## Problem
Delete every value that appears more than once in a sorted linked list, leaving only distinct values.

## Examples
```text
Input: head = [1,2,3,3,4,4,5]
Output: [1,2,5]
Explanation: All nodes with values 3 and 4 are removed.
```

## Understanding & Intuition
Duplicates form contiguous runs because the list is sorted. A dummy node simplifies deleting a run that starts at the head.

## Approach 1 — Naive / Brute Force
**Idea:** Count values, then create a new list of values with count one.
```python
class ListNode:
    def __init__(self, val: int = 0, next: 'ListNode | None' = None):
        self.val = val; self.next = next
class Solution:
    def deleteDuplicates(self, head: ListNode | None) -> ListNode | None:
        counts = {}; node = head
        while node:
            counts[node.val] = counts.get(node.val, 0) + 1; node = node.next
        dummy = tail = ListNode(); node = head
        while node:
            if counts[node.val] == 1:
                tail.next = ListNode(node.val); tail = tail.next
            node = node.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count first, then relink existing nodes whose values are unique.
```python
class Solution:
    def deleteDuplicates(self, head: ListNode | None) -> ListNode | None:
        counts = {}; node = head
        while node:
            counts[node.val] = counts.get(node.val, 0) + 1; node = node.next
        dummy = ListNode(0, head); prev = dummy; node = head
        while node:
            if counts[node.val] > 1: prev.next = node.next
            else: prev = node
            node = node.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Skip duplicate runs in one pass.
```python
class Solution:
    def deleteDuplicates(self, head: ListNode | None) -> ListNode | None:
        dummy = ListNode(0, head); prev = dummy; curr = head
        while curr:
            if curr.next and curr.val == curr.next.val:
                val = curr.val
                while curr and curr.val == val: curr = curr.next
                prev.next = curr
            else:
                prev = curr; curr = curr.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Duplicate runs may begin at the head.
- Remove all nodes in a duplicate run.
- Empty list returns empty.

## Related
- Remove Duplicates from Sorted List
- Merge Two Sorted Lists
