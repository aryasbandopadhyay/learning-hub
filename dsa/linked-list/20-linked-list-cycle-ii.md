# 20. Linked List Cycle II

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
Given the head of a linked list, return the node where a cycle begins, or `None` if there is no cycle. A cycle is formed by a tail node pointing to an earlier node, and the list may contain up to `10^4` nodes.

## Examples
```text
Input: head = [3,2,0,-4], pos = 1
Output: tail connects to node index 1
Explanation: The cycle starts at the node with value 2.
```

## Understanding & Intuition
The cycle entry is the first repeated node encountered when following next pointers from the head. A visited set can return that node directly. Floyd's method first detects a meeting point, then moves one pointer from head and one from the meeting point until they meet at the entry.

## Approach 1 — Naive / Brute Force
**Idea:** Store visited nodes and return the first node seen twice.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def detectCycle(self, head):
        seen = set()
        cur = head
        while cur:
            if cur in seen:
                return cur
            seen.add(cur)
            cur = cur.next
        return None
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Mark nodes during traversal and return the first marked node.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def detectCycle(self, head):
        cur = head
        while cur:
            if getattr(cur, "visited", False):
                return cur
            cur.visited = True  # Mutates input; useful only as a conceptual step.
            cur = cur.next
        return None
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use Floyd's cycle detection, then reset one pointer to head to find the entry.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def detectCycle(self, head):
        slow = fast = head
        while fast and fast.next:
            slow = slow.next
            fast = fast.next.next
            if slow is fast:
                finder = head
                while finder is not slow:
                    finder = finder.next
                    slow = slow.next
                return finder
        return None
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Return the node object, not the index or value.
- Compare nodes with identity.
- Attribute marking mutates input and should generally be avoided.

## Related
- Linked List Cycle
- Intersection of Two Linked Lists

