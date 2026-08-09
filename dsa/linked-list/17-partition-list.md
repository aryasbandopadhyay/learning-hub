# 17. Partition List

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Apple, Meta

## Problem
Given a linked-list head and value `x`, partition nodes so all values less than `x` come before all values greater than or equal to `x`. Preserve original relative order within both partitions.

**Input**
- `head`: head of a singly linked list.
- `x`: pivot value.

**Output**
- The partitioned list head. This judge compares exactly: all `< x` nodes in original order, then all `>= x` nodes in original order.

## Constraints
- `0 <= number of nodes <= 200`
- `-100 <= Node.val <= 100`
- `-200 <= x <= 200`

## Examples
```text
Input: head = [1,4,3,2,5,2], x = 3
Output: [1,2,2,4,3,5]
Explanation: Values less than `3` are `1, 2, 2`; the remaining values `4, 3, 5` follow in original order.
```

## Understanding & Intuition
This is a stable partition, so sorting is not allowed. A clean method keeps two chains: values less than `x` and values at least `x`. The optimal version reuses original nodes and connects the two chains at the end.

## Approach 1 — Naive / Brute Force
**Idea:** Collect values in two arrays and build a new list from `less + greater`.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def partition(self, head, x):
        less, greater = [], []
        while head:
            if head.val < x:
                less.append(head.val)
            else:
                greater.append(head.val)
            head = head.next

        dummy = ListNode()
        tail = dummy
        for value in less + greater:
            tail.next = ListNode(value)
            tail = tail.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build two new linked lists while scanning once, then concatenate them.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def partition(self, head, x):
        before = before_tail = ListNode()
        after = after_tail = ListNode()
        while head:
            if head.val < x:
                before_tail.next = ListNode(head.val)
                before_tail = before_tail.next
            else:
                after_tail.next = ListNode(head.val)
                after_tail = after_tail.next
            head = head.next
        before_tail.next = after.next
        return before.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Reuse nodes by appending each node to a before or after chain.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def partition(self, head, x):
        before = before_tail = ListNode()
        after = after_tail = ListNode()
        while head:
            nxt = head.next
            head.next = None
            if head.val < x:
                before_tail.next = head
                before_tail = head
            else:
                after_tail.next = head
                after_tail = head
            head = nxt
        before_tail.next = after.next
        return before.next
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Preserve relative order; do not sort.
- Detach reused nodes to avoid stale links.
- Either partition may be empty.

## Related
- Odd Even Linked List
- Remove Linked List Elements

