# 01. Reverse Linked List

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Meta, Google

## Problem
Given the `head` of a singly linked list, reverse the list and return the new head. Examples show lists as arrays of node values, but the function receives a `ListNode`.

**Input**
- `head`: head node of a singly linked list.

**Output**
- The head of the reversed list. This judge compares exactly by traversal order, so values must appear in reverse input order.

## Constraints
- `0 <= number of nodes <= 5000`
- `-5000 <= Node.val <= 5000`
- The list is singly linked and acyclic.

## Examples
```text
Input: head = [1,2,3,4,5]
Output: [5,4,3,2,1]
Explanation: Reversing all links makes `5` the head, followed by `4`, `3`, `2`, and `1`.
```

## Understanding & Intuition
Reversing a linked list means each node should point to its previous node instead of its next node. Since nodes only know their next node, we must preserve the next pointer before rewiring. The optimal pattern keeps three pointers: previous, current, and next.

## Approach 1 — Naive / Brute Force
**Idea:** Copy values into an array, then rewrite the same nodes in reverse value order.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reverseList(self, head):
        values = []
        cur = head
        while cur:
            values.append(cur.val)
            cur = cur.next

        cur = head
        for value in reversed(values):
            cur.val = value  # Reuse nodes, but reverse the values.
            cur = cur.next
        return head
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use recursion to reverse the suffix, then attach the current node after its next node.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reverseList(self, head):
        if head is None or head.next is None:
            return head

        new_head = self.reverseList(head.next)
        head.next.next = head
        head.next = None
        return new_head
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iteratively reverse pointers with O(1) extra space.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reverseList(self, head):
        prev = None
        cur = head
        while cur:
            nxt = cur.next      # Save the rest before breaking the link.
            cur.next = prev
            prev = cur
            cur = nxt
        return prev
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Empty and single-node lists are already reversed.
- Save `cur.next` before changing it.
- Recursion may hit Python recursion depth on very long lists.

## Related
- Swap Nodes in Pairs
- Reverse Nodes in k-Group

