# 09. Merge k Sorted Lists

- **Difficulty:** Hard
- **Pattern:** Linked List
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an array of `k` linked-list heads, merge all nodes into one sorted linked list.

Each input linked list is sorted in non-decreasing order. The returned list must contain every input node value exactly once in non-decreasing order.

**Input**
- `lists`: a list of linked-list heads. Each list may be empty and is sorted when non-empty.

**Output**
- The head of a merged linked list sorted in non-decreasing order.

## Constraints
- `0 <= k <= 10^4`
- `0 <= total number of nodes <= 10^4`
- `-10^4 <= Node.val <= 10^4`
- Each input linked list is sorted in non-decreasing order.

## Examples
```text
Input: lists = [[1,4,5],[1,3,4],[2,6]]
Output: [1,1,2,3,4,4,5,6]
Explanation: Taking the smallest available value from the list heads and continuing through all nodes yields `1,1,2,3,4,4,5,6`.
```

```text
Input: lists = []
Output: []
Explanation: No input lists means the merged list is empty.
```

## Understanding & Intuition
The smallest next node among all lists must be one of the current heads. Sorting all values is easy but discards node structure. A heap keeps the current smallest head available in logarithmic time, while divide-and-conquer repeatedly uses the two-list merge pattern.

## Approach 1 — Naive / Brute Force
**Idea:** Collect all values, sort them, and build a new linked list.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeKLists(self, lists):
        values = []
        for head in lists:
            while head:
                values.append(head.val)
                head = head.next

        dummy = ListNode()
        tail = dummy
        for value in sorted(values):
            tail.next = ListNode(value)
            tail = tail.next
        return dummy.next
```
- **Time:** O(N log N) — **Space:** O(N)

## Approach 2 — Better
**Idea:** Push each current list head into a min-heap and repeatedly pop the smallest.
```python
import heapq

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeKLists(self, lists):
        heap = []
        for i, node in enumerate(lists):
            if node:
                heapq.heappush(heap, (node.val, i, node))

        dummy = ListNode()
        tail = dummy
        while heap:
            _, i, node = heapq.heappop(heap)
            tail.next = node
            tail = tail.next
            if node.next:
                heapq.heappush(heap, (node.next.val, i, node.next))
        tail.next = None
        return dummy.next
```
- **Time:** O(N log k) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Divide and conquer: merge lists in pairs until one list remains.
```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeKLists(self, lists):
        def merge(a, b):
            dummy = ListNode()
            tail = dummy
            while a and b:
                if a.val <= b.val:
                    tail.next = a
                    a = a.next
                else:
                    tail.next = b
                    b = b.next
                tail = tail.next
            tail.next = a or b
            return dummy.next

        if not lists:
            return None
        while len(lists) > 1:
            merged = []
            for i in range(0, len(lists), 2):
                a = lists[i]
                b = lists[i + 1] if i + 1 < len(lists) else None
                merged.append(merge(a, b))
            lists = merged
        return lists[0]
```
- **Time:** O(N log k) — **Space:** O(1) auxiliary besides merge rounds

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(N log N) | O(N) |
| Better | O(N log k) | O(k) |
| Optimal | O(N log k) | O(1) auxiliary |

## Edge Cases & Pitfalls
- `lists` can be empty or contain null heads.
- Heap entries need a tie-breaker because `ListNode` is not orderable.
- Detach the final tail if reusing nodes from heap pops.

## Related
- Merge Two Sorted Lists
- Kth Smallest Element in a Sorted Matrix

