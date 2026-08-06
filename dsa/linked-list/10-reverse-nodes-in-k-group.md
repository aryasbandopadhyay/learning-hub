# 10. Reverse Nodes in k-Group

- **Difficulty:** Hard
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Google, Bloomberg

## Problem
Given the head of a linked list, reverse nodes in groups of size `k` and return the modified list. If the final group has fewer than `k` nodes, leave it unchanged. Node values must not be changed; `1 <= k <= length <= 5000`.

## Examples
```text
Input: head = [1,2,3,4,5], k = 2
Output: [2,1,4,3,5]
Explanation: Each full pair is reversed, and the last single node remains unchanged.
```

## Understanding & Intuition
The key is to reverse only complete groups. An array makes block reversal simple, but pointer-only reversal is the interview target. A dummy node helps reconnect each reversed block to the previous and next blocks.

## Approach 1 — Naive / Brute Force
**Idea:** Copy values to an array, reverse every complete slice, and write values back.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reverseKGroup(self, head, k):
        values = []
        cur = head
        while cur:
            values.append(cur.val)
            cur = cur.next

        for start in range(0, len(values) - len(values) % k, k):
            values[start:start + k] = reversed(values[start:start + k])

        cur = head
        for value in values:
            cur.val = value
            cur = cur.next
        return head
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack for each complete group and relink popped nodes.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reverseKGroup(self, head, k):
        dummy = ListNode(0)
        tail = dummy
        cur = head
        while cur:
            stack = []
            node = cur
            for _ in range(k):
                if not node:
                    tail.next = cur
                    return dummy.next
                stack.append(node)
                node = node.next
            while stack:
                tail.next = stack.pop()
                tail = tail.next
            cur = node
        tail.next = None
        return dummy.next
```
- **Time:** O(n) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Locate each kth node, reverse the group in place, and reconnect boundaries.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def reverseKGroup(self, head, k):
        def get_kth(node, steps):
            while node and steps > 0:
                node = node.next
                steps -= 1
            return node

        dummy = ListNode(0, head)
        group_prev = dummy
        while True:
            kth = get_kth(group_prev, k)
            if not kth:
                break
            group_next = kth.next

            prev, cur = group_next, group_prev.next
            while cur is not group_next:
                nxt = cur.next
                cur.next = prev
                prev = cur
                cur = nxt

            old_start = group_prev.next
            group_prev.next = kth
            group_prev = old_start
        return dummy.next
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(k) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Leave incomplete final groups unchanged.
- `k = 1` should return the same order.
- Store `group_next` before reversing pointers.

## Related
- Reverse Linked List
- Swap Nodes in Pairs

