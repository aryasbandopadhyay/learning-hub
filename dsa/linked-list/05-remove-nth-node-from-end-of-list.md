# 05. Remove Nth Node From End of List

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Adobe, Meta

## Problem
Given the head of a linked list and an integer `n`, remove the nth node from the end of the list and return the new head. The list has `1 <= length <= 30`, and `1 <= n <= length`.

## Examples
```text
Input: head = [1,2,3,4,5], n = 2
Output: [1,2,3,5]
Explanation: The second node from the end is 4.
```

## Understanding & Intuition
The nth node from the end is the `(length - n + 1)`th node from the front. Counting length first is simple but takes two passes. A fast pointer advanced `n` steps creates a fixed gap so a slow pointer lands before the node to delete.

## Approach 1 — Naive / Brute Force
**Idea:** Copy values except the removed index into a new linked list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def removeNthFromEnd(self, head, n):
        values = []
        cur = head
        while cur:
            values.append(cur.val)
            cur = cur.next

        remove_index = len(values) - n
        dummy = ListNode()
        tail = dummy
        for i, value in enumerate(values):
            if i != remove_index:
                tail.next = ListNode(value)
                tail = tail.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count length, then walk to the node before the target and unlink it.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def removeNthFromEnd(self, head, n):
        length = 0
        cur = head
        while cur:
            length += 1
            cur = cur.next

        dummy = ListNode(0, head)
        prev = dummy
        for _ in range(length - n):
            prev = prev.next
        prev.next = prev.next.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use two pointers separated by `n` nodes, then delete after the slow pointer.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def removeNthFromEnd(self, head, n):
        dummy = ListNode(0, head)
        fast = slow = dummy
        for _ in range(n):
            fast = fast.next

        while fast.next:
            fast = fast.next
            slow = slow.next

        slow.next = slow.next.next
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
- Removing the head is easiest with a dummy node.
- `n` is guaranteed valid, so no extra validation is required.
- In the one-pass method, advance `fast` exactly `n` nodes from dummy.

## Related
- Middle of the Linked List
- Rotate List

