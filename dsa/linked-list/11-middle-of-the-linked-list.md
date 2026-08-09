# 11. Middle of the Linked List

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Google, Adobe

## Problem
Given a non-empty linked list, return its middle node. If the list has two middle nodes, return the second one. The judge checks the returned node by its remaining suffix.

**Input**
- `head`: head of a non-empty singly linked list.

**Output**
- The middle node. This judge compares exactly by traversing from the returned node to the end.

## Constraints
- `1 <= number of nodes <= 100`
- `1 <= Node.val <= 100`

## Examples
```text
Input: head = [1,2,3,4,5]
Output: [3,4,5]
Explanation: A five-node list has the third node as its only middle, giving the suffix `[3,4,5]`.
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

