# 15. Swap Nodes in Pairs

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Google, Adobe

## Problem
Given the head of a linked list, swap every two adjacent nodes and return the new head. You must swap nodes, not just values. The list length can be from `0` to `100`.

## Examples
```text
Input: head = [1,2,3,4]
Output: [2,1,4,3]
Explanation: Adjacent pairs (1,2) and (3,4) are swapped.
```

## Understanding & Intuition
Swapping pairs is the special case of reversing nodes in groups of two. A dummy node helps reconnect the swapped pair to the previous part. The iterative optimal solution tracks the node before each pair.

## Approach 1 — Naive / Brute Force
**Idea:** Copy values to an array, swap adjacent values, and write them back.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def swapPairs(self, head):
        values = []
        cur = head
        while cur:
            values.append(cur.val)
            cur = cur.next
        for i in range(0, len(values) - 1, 2):
            values[i], values[i + 1] = values[i + 1], values[i]

        cur = head
        for value in values:
            cur.val = value
            cur = cur.next
        return head
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Recursively swap the first pair and attach it to the swapped remainder.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def swapPairs(self, head):
        if not head or not head.next:
            return head
        second = head.next
        head.next = self.swapPairs(second.next)
        second.next = head
        return second
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iteratively rewire each adjacent pair using a dummy predecessor.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def swapPairs(self, head):
        dummy = ListNode(0, head)
        prev = dummy
        while prev.next and prev.next.next:
            first = prev.next
            second = first.next
            first.next = second.next
            second.next = first
            prev.next = second
            prev = first
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
- Odd-length lists leave the final node unchanged.
- A dummy node handles swapping the original head.
- Store both nodes in the pair before rewiring.

## Related
- Reverse Nodes in k-Group
- Reverse Linked List

