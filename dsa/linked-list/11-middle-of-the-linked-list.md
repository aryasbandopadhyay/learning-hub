# 11. Middle of the Linked List

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Google, Adobe

## Problem
Given the head of a singly linked list, return the middle node. If there are two middle nodes, return the second middle. The list has `1 <= n <= 100`.

## Examples
```text
Input: head = [1,2,3,4,5]
Output: [3,4,5]
Explanation: Node 3 is the middle node.
```

## Understanding & Intuition
The middle depends on the list length. Counting first is simple, but it uses two passes. With a slow pointer moving one step and a fast pointer moving two, slow lands on the required second middle.

## Approach 1 — Naive / Brute Force
**Idea:** Store every node in an array and index the middle.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def middleNode(self, head):
        nodes = []
        cur = head
        while cur:
            nodes.append(cur)
            cur = cur.next
        return nodes[len(nodes) // 2]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count nodes first, then walk `length // 2` steps.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def middleNode(self, head):
        length = 0
        cur = head
        while cur:
            length += 1
            cur = cur.next

        cur = head
        for _ in range(length // 2):
            cur = cur.next
        return cur
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Move slow by one and fast by two until fast reaches the end.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def middleNode(self, head):
        slow = fast = head
        while fast and fast.next:
            slow = slow.next
            fast = fast.next.next
        return slow
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- For even length, return the second middle.
- The input is non-empty by constraint.
- Do not compare values to identify the middle.

## Related
- Remove Nth Node From End of List
- Palindrome Linked List

