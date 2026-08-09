# 16. Rotate List

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Bloomberg, Meta

## Problem
Given a linked-list head and non-negative integer `k`, rotate the list right by `k` positions. One right rotation moves the last node to the front; rotations wrap around the list length.

**Input**
- `head`: head of a singly linked list.
- `k`: number of right rotations.

**Output**
- The rotated list head. This judge compares exactly by traversal order.

## Constraints
- `0 <= number of nodes <= 500`
- `-100 <= Node.val <= 100`
- `0 <= k <= 2 * 10^9`

## Examples
```text
Input: head = [1,2,3,4,5], k = 2
Output: [4,5,1,2,3]
Explanation: Two right rotations move `5` and then `4` to the front, producing `4 -> 5 -> 1 -> 2 -> 3`.
```

## Understanding & Intuition
Rotating by the list length returns the same list, so `k` should be reduced modulo length. The new head is `length - k` steps from the original head. Making the list temporarily circular makes reconnecting simple.

## Approach 1 — Naive / Brute Force
**Idea:** Convert to an array, rotate values, and build a new linked list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def rotateRight(self, head, k):
        values = []
        cur = head
        while cur:
            values.append(cur.val)
            cur = cur.next
        if not values:
            return None

        k %= len(values)
        values = values[-k:] + values[:-k] if k else values
        dummy = ListNode()
        tail = dummy
        for value in values:
            tail.next = ListNode(value)
            tail = tail.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Move the tail to the front one rotation at a time after finding the previous tail.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def rotateRight(self, head, k):
        if not head or not head.next:
            return head
        length = 0
        cur = head
        while cur:
            length += 1
            cur = cur.next
        k %= length

        for _ in range(k):
            prev, tail = None, head
            while tail.next:
                prev, tail = tail, tail.next
            prev.next = None
            tail.next = head
            head = tail
        return head
```
- **Time:** O(nk) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Link tail to head to form a circle, then break at the new tail.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def rotateRight(self, head, k):
        if not head or not head.next:
            return head

        length = 1
        tail = head
        while tail.next:
            length += 1
            tail = tail.next
        k %= length
        if k == 0:
            return head

        tail.next = head
        steps_to_new_tail = length - k - 1
        new_tail = head
        for _ in range(steps_to_new_tail):
            new_tail = new_tail.next
        new_head = new_tail.next
        new_tail.next = None
        return new_head
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(nk) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Reduce `k` modulo length.
- Empty and single-node lists should return unchanged.
- Break the temporary cycle before returning.

## Related
- Remove Nth Node From End of List
- Reorder List

