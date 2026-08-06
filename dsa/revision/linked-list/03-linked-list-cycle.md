# 03. Linked List Cycle

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Bloomberg, Google

## Problem
Given the head of a linked list, determine whether the list contains a cycle. A cycle exists if following `next` pointers can revisit a node; the list may contain up to `10000` nodes.

## Examples
```text
Input: head = [3,2,0,-4], pos = 1
Output: true
Explanation: The tail connects back to the node with value 2.
```

## Understanding & Intuition
A cycle is about revisiting the same node object, not seeing the same value twice. Storing visited nodes is straightforward but costs memory. Floyd's tortoise-and-hare method detects a cycle when a slow and fast pointer meet.

## Approach 1 — Naive / Brute Force
**Idea:** Track node identities in a set and return true on the first repeat.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def hasCycle(self, head):
        seen = set()
        cur = head
        while cur:
            if cur in seen:
                return True
            seen.add(cur)
            cur = cur.next
        return False
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Mark nodes by adding a temporary attribute while traversing.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def hasCycle(self, head):
        cur = head
        while cur:
            if getattr(cur, "visited", False):
                return True
            cur.visited = True  # Mutates nodes, so avoid this in interviews unless allowed.
            cur = cur.next
        return False
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Move one pointer by one step and another by two; a cycle forces them to meet.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def hasCycle(self, head):
        slow = fast = head
        while fast and fast.next:
            slow = slow.next
            fast = fast.next.next
            if slow is fast:
                return True
        return False
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Compare nodes with `is`, not values.
- Empty and one-node acyclic lists return false.
- Attribute marking mutates input and can be unacceptable.

## Related
- Linked List Cycle II
- Intersection of Two Linked Lists

