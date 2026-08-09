# 08. Merge k Sorted Lists

- **Difficulty:** Hard
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Microsoft, Apple

## Problem
Given an array of `k` linked-list heads, merge all nodes into one sorted linked list.

Each input linked list is sorted in non-decreasing order. Reuse or create nodes as needed, but the returned list must contain exactly all values from all input lists in sorted order.

**Input**
- `lists`: a list of linked-list heads. Each linked list is sorted in non-decreasing order and may be empty.

**Output**
- The head of one linked list containing all input values in non-decreasing order.

## Constraints
- `0 <= k <= 10^4`
- `0 <= total number of nodes <= 10^4`
- `-10^4 <= Node.val <= 10^4`
- Each input linked list is sorted in non-decreasing order.

## Examples
```text
Input: lists = [[1,4,5],[1,3,4],[2,6]]
Output: [1,1,2,3,4,4,5,6]
Explanation: Repeatedly taking the smallest available head across the three lists produces the sorted sequence `1,1,2,3,4,4,5,6`.
```

```text
Input: lists = []
Output: []
Explanation: With no lists, there are no nodes to merge.
```

## Understanding & Intuition
Merging all values after extraction is simple but ignores that each list is already sorted. Pairwise merging uses that sorted structure and reduces repeated scanning. A min-heap of current list heads performs a k-way merge directly.

## Approach 1 — Naive / Brute Force
**Idea:** Collect all node values, sort them, and build a new linked list.
```python
from typing import List, Optional

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        values = []
        for head in lists:
            while head:
                values.append(head.val)
                head = head.next

        dummy = ListNode()
        current = dummy
        for value in sorted(values):
            current.next = ListNode(value)
            current = current.next
        return dummy.next
```
- **Time:** O(N log N) — **Space:** O(N)

## Approach 2 — Better
**Idea:** Merge lists two at a time using the standard two-list merge.
```python
from typing import List, Optional

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        def merge(a: Optional[ListNode], b: Optional[ListNode]) -> Optional[ListNode]:
            dummy = ListNode()
            tail = dummy
            while a and b:
                if a.val <= b.val:
                    tail.next, a = a, a.next
                else:
                    tail.next, b = b, b.next
                tail = tail.next
            tail.next = a or b
            return dummy.next

        result = None
        for head in lists:
            result = merge(result, head)
        return result
```
- **Time:** O(kN) worst — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Push each non-empty list head into a min-heap and repeatedly append the smallest node.
```python
from typing import List, Optional
import heapq

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        heap = []
        for index, node in enumerate(lists):
            if node:
                # index breaks ties because ListNode objects are not comparable.
                heapq.heappush(heap, (node.val, index, node))

        dummy = ListNode()
        tail = dummy
        while heap:
            _, index, node = heapq.heappop(heap)
            tail.next = node
            tail = tail.next
            if node.next:
                heapq.heappush(heap, (node.next.val, index, node.next))

        tail.next = None
        return dummy.next
```
- **Time:** O(N log k) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(N log N) | O(N) |
| Better | O(kN) worst | O(1) |
| Optimal | O(N log k) | O(k) |

## Edge Cases & Pitfalls
- `lists` can be empty or contain `None` heads.
- Heap entries need a tie-breaker to avoid comparing nodes.
- If reusing nodes, terminate the final list to avoid stale links.

## Related
- Merge Two Sorted Lists
- Design Twitter
- Smallest Range Covering Elements from K Lists
