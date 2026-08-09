# 02. Merge Two Sorted Lists

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Apple, Bloomberg

## Problem
Given heads `list1` and `list2` of two sorted singly linked lists, merge them into one sorted linked list and return its head. Both inputs are sorted in non-decreasing order.

**Input**
- `list1`: head of the first sorted list.
- `list2`: head of the second sorted list.

**Output**
- The head of a sorted merged list. This judge compares exactly by traversal order.

## Constraints
- `0 <= length of list1, length of list2 <= 50`
- `-100 <= Node.val <= 100`
- Both input lists are sorted in non-decreasing order.

## Examples
```text
Input: list1 = [1,2,4], list2 = [1,3,4]
Output: [1,1,2,3,4,4]
Explanation: Taking the smaller current node from either list produces `1, 1, 2, 3, 4, 4`.
```

## Understanding & Intuition
Because both input lists are sorted, the smallest remaining node is always one of the two current heads. A dummy node simplifies attaching nodes to the answer. The optimal version reuses existing nodes instead of allocating replacements.

## Approach 1 — Naive / Brute Force
**Idea:** Collect all values, sort them, and build a new linked list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeTwoLists(self, list1, list2):
        values = []
        while list1:
            values.append(list1.val)
            list1 = list1.next
        while list2:
            values.append(list2.val)
            list2 = list2.next

        dummy = ListNode()
        tail = dummy
        for value in sorted(values):
            tail.next = ListNode(value)
            tail = tail.next
        return dummy.next
```
- **Time:** O((m+n) log(m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** Build a new list by comparing the current values without sorting again.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeTwoLists(self, list1, list2):
        dummy = ListNode()
        tail = dummy
        while list1 and list2:
            if list1.val <= list2.val:
                tail.next = ListNode(list1.val)
                list1 = list1.next
            else:
                tail.next = ListNode(list2.val)
                list2 = list2.next
            tail = tail.next

        rest = list1 or list2
        while rest:
            tail.next = ListNode(rest.val)
            tail = tail.next
            rest = rest.next
        return dummy.next
```
- **Time:** O(m+n) — **Space:** O(m+n)

## Approach 3 — Optimal
**Idea:** Compare heads and splice existing nodes into the merged list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeTwoLists(self, list1, list2):
        dummy = ListNode()
        tail = dummy
        while list1 and list2:
            if list1.val <= list2.val:
                tail.next = list1
                list1 = list1.next
            else:
                tail.next = list2
                list2 = list2.next
            tail = tail.next

        tail.next = list1 or list2
        return dummy.next
```
- **Time:** O(m+n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((m+n) log(m+n)) | O(m+n) |
| Better | O(m+n) | O(m+n) |
| Optimal | O(m+n) | O(1) |

## Edge Cases & Pitfalls
- Either list may be empty.
- Preserve duplicates; do not skip equal values.
- When reusing nodes, attach the remaining tail directly.

## Related
- Merge k Sorted Lists
- Add Two Numbers

