# 07. Sequence Reconstruction

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Airbnb, Amazon

## Problem
Implement `sequenceReconstruction` for **Sequence Reconstruction**. Given target permutation `nums` and partial ordering sequences `sequences`, return `True` if `nums` is the only shortest supersequence consistent with every sequence; otherwise return `False`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `nums`: list; input integer list.
- `sequences`: list; subsequences encoding ordering constraints.

**Output**
- `True` or `False`.

## Constraints
- `1 <= len(nums) <= 10000`, total sequence length `<= 10000`

## Examples
```text
Input: nums = [1,2,3], sequences = [[1,2],[1,3],[2,3]]
Output: True
Explanation: The only topological order using all numbers is [1,2,3]. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Adjacent elements inside a sequence create directed precedence edges. A unique topological order has exactly one zero-indegree node at every step. That unique order must also match `nums`.

## Approach 1 — Naive / Brute Force
**Idea:** Verify all edges agree with `nums`, then require a path from every adjacent pair in `nums`.
```python
class Solution:
    def sequenceReconstruction(self, nums: list[int], sequences: list[list[int]]) -> bool:
        allowed = set(nums)
        present = set()
        g = {x: set() for x in nums}
        for seq in sequences:
            for x in seq:
                if x not in allowed:
                    return False
                present.add(x)
            for a, b in zip(seq, seq[1:]):
                g[a].add(b)
        if present != allowed:
            return False
        pos = {x: i for i, x in enumerate(nums)}
        for a in g:
            for b in g[a]:
                if pos[a] >= pos[b]:
                    return False
        for a, b in zip(nums, nums[1:]):
            seen = {a}; stack = [a]; found = False
            while stack and not found:
                u = stack.pop()
                for v in g[u]:
                    if v == b:
                        found = True; break
                    if v not in seen:
                        seen.add(v); stack.append(v)
            if not found:
                return False
        return True
```
- **Time:** O(n(n + e)) — **Space:** O(n + e)

## Approach 2 — Better
**Idea:** It is enough that every adjacent pair of `nums` appears directly and no edge contradicts `nums`.
```python
class Solution:
    def sequenceReconstruction(self, nums: list[int], sequences: list[list[int]]) -> bool:
        pos = {x: i for i, x in enumerate(nums)}
        need = {(nums[i], nums[i + 1]) for i in range(len(nums) - 1)}
        seen = set()
        for seq in sequences:
            for x in seq:
                if x not in pos:
                    return False
                seen.add(x)
            for a, b in zip(seq, seq[1:]):
                if pos[a] >= pos[b]:
                    return False
                need.discard((a, b))
        return not need and seen == set(nums)
```
- **Time:** O(n + e) — **Space:** O(n + e)

## Approach 3 — Optimal
**Idea:** Run Kahn's algorithm and require exactly one available node, equal to the next target value, at every step.
```python
class Solution:
    def sequenceReconstruction(self, nums: list[int], sequences: list[list[int]]) -> bool:
        from collections import deque
        nodes = set(nums)
        g = {x: set() for x in nums}
        indeg = {x: 0 for x in nums}
        present = set()
        for seq in sequences:
            for x in seq:
                if x not in nodes:
                    return False
                present.add(x)
            for a, b in zip(seq, seq[1:]):
                if b not in g[a]:
                    g[a].add(b); indeg[b] += 1
        if present != nodes:
            return False
        q = deque([x for x in nums if indeg[x] == 0])
        order = []
        while q:
            if len(q) != 1:
                return False
            u = q.popleft(); order.append(u)
            for v in g[u]:
                indeg[v] -= 1
                if indeg[v] == 0:
                    q.append(v)
        return order == nums
```
- **Time:** O(n + e) — **Space:** O(n + e)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n(n + e)) | O(n + e) |
| Better | O(n + e) | O(n + e) |
| Optimal | O(n + e) | O(n + e) |

## Edge Cases & Pitfalls
- Reject any sequence value not in `nums`.
- A valid order is insufficient; it must be unique.

## Related
- Course Schedule II
- Alien Dictionary
