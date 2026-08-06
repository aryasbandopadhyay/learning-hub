# 06. Split Array Into Consecutive Subsequences

- **Difficulty:** Medium
- **Pattern:** greedy scheduling & assignment
- **Asked at:** Google, Amazon, ByteDance

## Problem
Given sorted `nums`, return `True` if it can be split into one or more consecutive subsequences, each of length at least three.

## Examples
```text
Input: nums = [1,2,3,3,4,5]
Output: True
Explanation: Split into [1,2,3] and [3,4,5].
```

## Understanding & Intuition
A number should first extend a subsequence that is waiting for it. Starting a new subsequence is safe only when the next two consecutive numbers are available. This prevents leaving length-one or length-two fragments.

## Approach 1 — Naive / Brute Force
**Idea:** Backtrack on small inputs by assigning the smallest unused value to every possible valid sequence; use the greedy helper for large inputs.
```python
class Solution:
    def isPossible(self, nums: list[int]) -> bool:
        from collections import Counter, defaultdict
        if len(nums) > 18:
            left = Counter(nums)
            need = defaultdict(int)
            for x in nums:
                if left[x] == 0:
                    continue
                left[x] -= 1
                if need[x]:
                    need[x] -= 1
                    need[x + 1] += 1
                elif left[x + 1] and left[x + 2]:
                    left[x + 1] -= 1
                    left[x + 2] -= 1
                    need[x + 3] += 1
                else:
                    return False
            return True
        vals = sorted(set(nums))
        pos = {v: i for i, v in enumerate(vals)}
        start = tuple(Counter(nums)[v] for v in vals)
        memo = {}
        def dfs(counts):
            if counts in memo:
                return memo[counts]
            first = -1
            for i, c in enumerate(counts):
                if c:
                    first = i
                    break
            if first == -1:
                return True
            x = vals[first]
            cur = list(counts)
            y = x
            while y in pos and cur[pos[y]]:
                cur[pos[y]] -= 1
                if y - x + 1 >= 3 and dfs(tuple(cur)):
                    memo[counts] = True
                    return True
                y += 1
            memo[counts] = False
            return False
        return dfs(start)
```
- **Time:** O(2ⁿ) — **Space:** O(2ⁿ)

## Approach 2 — Better
**Idea:** Store lengths of subsequences ending at each value and always extend the shortest one.
```python
class Solution:
    def isPossible(self, nums: list[int]) -> bool:
        import heapq
        from collections import defaultdict
        ends = defaultdict(list)
        for x in nums:
            if ends[x - 1]:
                heapq.heappush(ends[x], heapq.heappop(ends[x - 1]) + 1)
            else:
                heapq.heappush(ends[x], 1)
        return all(not h or h[0] >= 3 for h in ends.values())
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track remaining counts and how many subsequences need each next value.
```python
class Solution:
    def isPossible(self, nums: list[int]) -> bool:
        from collections import Counter, defaultdict
        left = Counter(nums)
        need = defaultdict(int)
        for x in nums:
            if left[x] == 0:
                continue
            left[x] -= 1
            if need[x]:
                need[x] -= 1
                need[x + 1] += 1
            elif left[x + 1] and left[x + 2]:
                left[x + 1] -= 1
                left[x + 2] -= 1
                need[x + 3] += 1
            else:
                return False
        return True
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2ⁿ) | O(2ⁿ) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Extend before starting a new subsequence.
- Any final subsequence shorter than three fails.
- Duplicates are separate usable copies.

## Related
- Hand of Straights
- Partition Labels
