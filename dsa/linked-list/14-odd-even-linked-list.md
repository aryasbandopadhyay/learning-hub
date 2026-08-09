# 14. Odd Even Linked List

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Bloomberg, Meta

## Problem
Given a linked-list head, group nodes at odd one-based positions first, followed by nodes at even positions. Preserve relative order inside each group.

**Input**
- `head`: head of a singly linked list.

**Output**
- The reordered head. This judge compares exactly: odd-indexed nodes in order, then even-indexed nodes in order.

## Constraints
- `0 <= number of nodes <= 10^4`
- `-10^6 <= Node.val <= 10^6`

## Examples
```text
Input: head = [1,2,3,4,5]
Output: [1,3,5,2,4]
Explanation: Odd positions are `1, 3, 5`; even positions are `2, 4`; concatenating them gives the output.
```

## Understanding & Intuition
Odd-even here means node position, not value. The stable grouping can be done by collecting nodes, building two lists, or rewiring pointers in one pass. The optimal method keeps separate odd and even tails, then appends the even list after the odd list.

## Approach 1 — Naive / Brute Force
**Idea:** Store nodes in an array and relink odd indices followed by even indices.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def oddEvenList(self, head):
        nodes = []
        cur = head
        while cur:
            nodes.append(cur)
            cur = cur.next
        ordered = nodes[0::2] + nodes[1::2]
        dummy = ListNode()
        tail = dummy
        for node in ordered:
            tail.next = node
            tail = tail.next
        tail.next = None
        return dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build two new lists for odd-position and even-position values, then concatenate them.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def oddEvenList(self, head):
        odd_dummy = ListNode()
        even_dummy = ListNode()
        odd_tail, even_tail = odd_dummy, even_dummy
        index = 1
        while head:
            if index % 2:
                odd_tail.next = ListNode(head.val)
                odd_tail = odd_tail.next
            else:
                even_tail.next = ListNode(head.val)
                even_tail = even_tail.next
            head = head.next
            index += 1
        odd_tail.next = even_dummy.next
        return odd_dummy.next
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Rewire existing odd and even chains in one pass and then join them.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def oddEvenList(self, head):
        if not head:
            return None
        odd = head
        even = head.next
        even_head = even
        while even and even.next:
            odd.next = even.next
            odd = odd.next
            even.next = odd.next
            even = even.next
        odd.next = even_head
        return head
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Do not group by odd and even values.
- Preserve relative order inside both groups.
- Lists with fewer than three nodes are already valid.

## Related
- Partition List
- Reorder List

