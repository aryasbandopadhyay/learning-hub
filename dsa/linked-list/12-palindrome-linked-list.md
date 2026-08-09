# 12. Palindrome Linked List

- **Difficulty:** Easy
- **Pattern:** Linked List
- **Asked at:** Amazon, Meta, Microsoft, Google

## Problem
Given a linked-list head, return whether its node values form a palindrome. The values must read the same from head to tail and tail to head.

**Input**
- `head`: head of a singly linked list.

**Output**
- A boolean: `True` if the value sequence is a palindrome, otherwise `False`.

## Constraints
- `1 <= number of nodes <= 10^5`
- `0 <= Node.val <= 9`

## Examples
```text
Input: head = [1,2,2,1]
Output: true
Explanation: The sequence `1, 2, 2, 1` is symmetric, so it is a palindrome.
```

## Understanding & Intuition
A palindrome is symmetric around the middle. Arrays make backward comparison trivial, but linked lists do not support reverse traversal. The optimal linked-list method reverses the second half and compares it with the first half.

## Approach 1 — Naive / Brute Force
**Idea:** Copy values into an array and compare the array with its reverse.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def isPalindrome(self, head):
        values = []
        while head:
            values.append(head.val)
            head = head.next
        return values == values[::-1]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use recursion to compare front and back nodes as the call stack unwinds.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def isPalindrome(self, head):
        self.front = head

        def check(node):
            if not node:
                return True
            if not check(node.next):
                return False
            same = self.front.val == node.val
            self.front = self.front.next
            return same

        return check(head)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Reverse the second half, compare halves, and return the result.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def isPalindrome(self, head):
        slow = fast = head
        while fast and fast.next:
            slow = slow.next
            fast = fast.next.next

        prev = None
        cur = slow
        while cur:
            nxt = cur.next
            cur.next = prev
            prev = cur
            cur = nxt

        left, right = head, prev
        while right:
            if left.val != right.val:
                return False
            left = left.next
            right = right.next
        return True
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Single-node lists are palindromes.
- Recursion can exceed Python recursion depth on long lists.
- If the interviewer requires preserving the list, reverse the second half back.

## Related
- Reorder List
- Middle of the Linked List

