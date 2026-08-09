# 04. Minimum Cost to Connect Sticks

- **Difficulty:** Medium
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given a list `sticks`, repeatedly connect any two sticks with cost equal to their sum; the new stick has that summed length. Return the minimum total cost to connect all sticks into one stick.

Implement `Solution.connectSticks` with the parameters below and return the requested value.

**Input**
- `sticks`: a `list[int]`; the stick lengths.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(sticks) <= 10000`, `1 <= sticks[i] <= 100000`

## Examples
```text
Input: sticks = [2,4,3]
Output: 14
Explanation: Connect 2+3 for cost 5, then 5+4 for cost 9, total 14.
```

## Understanding & Intuition
Every time two sticks are connected, their length contributes to future costs. To minimize repeated contribution, combine the two smallest available sticks first. This is the same greedy choice used in Huffman coding.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible first pair recursively and choose the minimum total cost.
```python
class Solution:
    def connectSticks(self, sticks: list[int]) -> int:
        from functools import lru_cache
        @lru_cache(None)
        def solve(state):
            arr = list(state)
            if len(arr) <= 1:
                return 0
            best = 10**30
            for i in range(len(arr)):
                for j in range(i + 1, len(arr)):
                    merged = arr[i] + arr[j]
                    nxt = [arr[t] for t in range(len(arr)) if t != i and t != j]
                    nxt.append(merged)
                    nxt.sort()
                    best = min(best, merged + solve(tuple(nxt)))
            return best
        return solve(tuple(sorted(sticks)))
```
- **Time:** O((2n)!) — **Space:** O((2n)!)

## Approach 2 — Better
**Idea:** Repeatedly sort the current sticks and merge the two smallest.
```python
class Solution:
    def connectSticks(self, sticks: list[int]) -> int:
        arr = sticks[:]
        total = 0
        while len(arr) > 1:
            arr.sort()
            merged = arr[0] + arr[1]
            total += merged
            arr = arr[2:] + [merged]
        return total
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a min-heap to repeatedly extract the two smallest sticks efficiently.
```python
class Solution:
    def connectSticks(self, sticks: list[int]) -> int:
        import heapq
        heap = sticks[:]
        heapq.heapify(heap)
        total = 0
        while len(heap) > 1:
            merged = heapq.heappop(heap) + heapq.heappop(heap)
            total += merged
            heapq.heappush(heap, merged)
        return total
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((2n)!) | O((2n)!) |
| Better | O(n^2 log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- A single stick has total cost `0`.
- Do not greedily connect the largest sticks; that increases repeated costs.
- Copy the input before mutating it.

## Related
- Huffman Coding
- Last Stone Weight
