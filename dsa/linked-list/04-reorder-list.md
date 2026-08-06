# 04. Reorder List

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Meta, Amazon, Microsoft, Google

## Problem
Given the head of a singly linked list `L0 -> L1 -> ... -> Ln`, reorder it in-place to `L0 -> Ln -> L1 -> Ln-1 -> ...`. Do not change node values; the list length is up to `50000`.

## Examples
```text
Input: head = [1,2,3,4]
Output: [1,4,2,3]
Explanation: The last node is interleaved after the first node.
```

## Understanding & Intuition
The target order alternates nodes from the front and back. An array makes random access easy, but extra space is not ideal. The pointer trick is to split the list, reverse the second half, and merge the two halves.

## Approach 1 — Naive / Brute Force
**Idea:** Store nodes in an array, then relink from both ends.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reorderList(self, head):
        nodes = []
        cur = head
        while cur:
            nodes.append(cur)
            cur = cur.next

        i, j = 0, len(nodes) - 1
        dummy = ListNode()
        tail = dummy
        while i <= j:
            tail.next = nodes[i]
            tail = tail.next
            i += 1
            if i <= j:
                tail.next = nodes[j]
                tail = tail.next
                j -= 1
        tail.next = None
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack for the second pass so tail nodes can be popped in reverse order.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reorderList(self, head):
        stack = []
        cur = head
        while cur:
            stack.append(cur)
            cur = cur.next

        cur = head
        for _ in range(len(stack) // 2):
            tail = stack.pop()
            nxt = cur.next
            cur.next = tail
            tail.next = nxt
            cur = nxt
        if cur:
            cur.next = None
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Find the middle, reverse the second half, then weave the two lists.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reorderList(self, head):
        if not head or not head.next:
            return

        slow, fast = head, head.next
        while fast and fast.next:
            slow = slow.next
            fast = fast.next.next

        second = slow.next
        slow.next = None
        prev = None
        while second:
            nxt = second.next
            second.next = prev
            prev = second
            second = nxt

        first, second = head, prev
        while second:
            fnext, snext = first.next, second.next
            first.next = second
            second.next = fnext
            first, second = fnext, snext
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- End the final list with `None` to avoid accidental cycles.
- Odd lengths leave one middle node at the end.
- The problem asks to mutate in place and return nothing.

## Related
- Reverse Linked List
- Palindrome Linked List

