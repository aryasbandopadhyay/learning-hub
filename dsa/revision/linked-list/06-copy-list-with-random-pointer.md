# 06. Copy List with Random Pointer

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Microsoft, Meta, Bloomberg

## Problem
Given a linked list where each node has `next` and `random` pointers, return a deep copy of the list. The list has `0 <= n <= 1000` nodes, and each random pointer is either null or points to a node in the list.

## Examples
```text
Input: head = [[7,null],[13,0],[11,4],[10,2],[1,0]]
Output: [[7,null],[13,0],[11,4],[10,2],[1,0]]
Explanation: The copied nodes have the same values and random relationships, but are distinct objects.
```

## Understanding & Intuition
A deep copy needs new nodes while preserving both next and random relationships. The random pointer can point forward, backward, or to itself, so a direct one-pass copy needs a mapping. The optimal interleaving trick places each copy beside its original to avoid an external hash map.

## Approach 1 — Naive / Brute Force
**Idea:** Store originals in an array, build copies, and find random targets by index lookup.
```python
class Node:
    def __init__(self, x=0, next=None, random=None):
        self.val = int(x)
        self.next = next
        self.random = random

class Solution:
    def copyRandomList(self, head):
        originals = []
        cur = head
        while cur:
            originals.append(cur)
            cur = cur.next

        copies = [Node(node.val) for node in originals]
        for i, node in enumerate(originals):
            if i + 1 < len(copies):
                copies[i].next = copies[i + 1]
            if node.random:
                copies[i].random = copies[originals.index(node.random)]
        return copies[0] if copies else None
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a dictionary from original nodes to copied nodes, then wire both pointers.
```python
class Node:
    def __init__(self, x=0, next=None, random=None):
        self.val = int(x)
        self.next = next
        self.random = random

class Solution:
    def copyRandomList(self, head):
        old_to_new = {None: None}
        cur = head
        while cur:
            old_to_new[cur] = Node(cur.val)
            cur = cur.next

        cur = head
        while cur:
            old_to_new[cur].next = old_to_new[cur.next]
            old_to_new[cur].random = old_to_new[cur.random]
            cur = cur.next
        return old_to_new[head]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Interleave copied nodes with originals, assign randoms through neighbors, then detach the copy list.
```python
class Node:
    def __init__(self, x=0, next=None, random=None):
        self.val = int(x)
        self.next = next
        self.random = random

class Solution:
    def copyRandomList(self, head):
        if not head:
            return None

        cur = head
        while cur:
            copy = Node(cur.val, cur.next)
            cur.next = copy
            cur = copy.next

        cur = head
        while cur:
            if cur.random:
                cur.next.random = cur.random.next
            cur = cur.next.next

        dummy = Node(0)
        copy_tail = dummy
        cur = head
        while cur:
            copy = cur.next
            cur.next = copy.next      # Restore original list.
            copy_tail.next = copy
            copy_tail = copy
            cur = cur.next
        return dummy.next
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The copy must not share any original node.
- Random pointers can be null or self-referential.
- The interleaving method should restore the original list.

## Related
- Clone Graph
- LRU Cache

