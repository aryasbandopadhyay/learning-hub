# 07. Add Two Numbers

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Apple, Meta

## Problem
Given two non-empty linked lists `l1` and `l2` representing non-negative integers in reverse digit order, return the sum as a linked list in the same reverse order. Each node stores one digit.

**Input**
- `l1`: first number, least significant digit first.
- `l2`: second number, least significant digit first.

**Output**
- The head of the sum list. This judge compares exactly by traversal order.

## Constraints
- `1 <= length of l1, length of l2 <= 100`
- `0 <= Node.val <= 9`
- Numbers have no leading zeroes except `0` itself.

## Examples
```text
Input: l1 = [2,4,3], l2 = [5,6,4]
Output: [7,0,8]
Explanation: The lists represent `342` and `465`; their sum is `807`, stored as `7 -> 0 -> 8`.
```

## Understanding & Intuition
Because digits are already reversed, addition can proceed from head to tail just like elementary addition from least significant digit. A carry may remain after both lists end. A dummy head makes appending result digits simple.

## Approach 1 — Naive / Brute Force
**Idea:** Convert each list to an integer, add them, and convert the sum back to a list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def addTwoNumbers(self, l1, l2):
        def to_int(node):
            value, place = 0, 1
            while node:
                value += node.val * place
                place *= 10
                node = node.next
            return value

        total = to_int(l1) + to_int(l2)
        dummy = ListNode()
        tail = dummy
        if total == 0:
            return ListNode(0)
        while total:
            total, digit = divmod(total, 10)
            tail.next = ListNode(digit)
            tail = tail.next
        return dummy.next
```
- **Time:** O(m+n) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** Push digits into arrays and simulate addition by index.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def addTwoNumbers(self, l1, l2):
        a, b = [], []
        while l1:
            a.append(l1.val)
            l1 = l1.next
        while l2:
            b.append(l2.val)
            l2 = l2.next

        dummy = ListNode()
        tail = dummy
        carry = i = 0
        while i < len(a) or i < len(b) or carry:
            total = carry
            if i < len(a):
                total += a[i]
            if i < len(b):
                total += b[i]
            carry, digit = divmod(total, 10)
            tail.next = ListNode(digit)
            tail = tail.next
            i += 1
        return dummy.next
```
- **Time:** O(m+n) — **Space:** O(m+n)

## Approach 3 — Optimal
**Idea:** Traverse both lists directly and create one output digit per step.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def addTwoNumbers(self, l1, l2):
        dummy = ListNode()
        tail = dummy
        carry = 0
        while l1 or l2 or carry:
            total = carry
            if l1:
                total += l1.val
                l1 = l1.next
            if l2:
                total += l2.val
                l2 = l2.next
            carry, digit = divmod(total, 10)
            tail.next = ListNode(digit)
            tail = tail.next
        return dummy.next
```
- **Time:** O(m+n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m+n) | O(m+n) |
| Better | O(m+n) | O(m+n) |
| Optimal | O(m+n) | O(1) |

## Edge Cases & Pitfalls
- Remember the final carry.
- Input lists may have different lengths.
- Python integers avoid overflow, but other languages may not.

## Related
- Merge Two Sorted Lists
- Add Two Numbers II

