# 07. Largest Divisible Subset

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Microsoft

## Problem
Given a list of distinct positive integers `nums`, return the largest subset such that for every pair `(a, b)` in the subset, either `a % b == 0` or `b % a == 0`. If several largest subsets exist, return the lexicographically smallest subset in increasing order.
Constraints: `1 <= len(nums) <= 1000`, `1 <= nums[i] <= 2 * 10^9`.

## Examples
```text
Input: nums = [1,2,3]
Output: [1,2]
Explanation: [1,2] and [1,3] are both largest; [1,2] is lexicographically smaller.
```

## Understanding & Intuition
After sorting, divisibility becomes transitive along a chain where each next number is divisible by the previous one. To make judging deterministic, ties are always broken by lexicographic order.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all subsets, keep valid divisible ones, and break equal-length ties lexicographically.
```python
class Solution:
    def largestDivisibleSubset(self, nums: list[int]) -> list[int]:
        arr = sorted(nums)
        n = len(arr)
        best = []
        def valid(seq: list[int]) -> bool:
            for i in range(len(seq)):
                for j in range(i):
                    if seq[i] % seq[j] != 0:
                        return False
            return True
        def dfs(i: int, seq: list[int]) -> None:
            nonlocal best
            if i == n:
                if valid(seq) and (len(seq) > len(best) or (len(seq) == len(best) and seq < best)):
                    best = seq[:]
                return
            dfs(i + 1, seq)
            seq.append(arr[i])
            dfs(i + 1, seq)
            seq.pop()
        dfs(0, [])
        return best
```
- **Time:** O(n^2 2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the best canonical chain starting after a previous chosen value.
```python
class Solution:
    def largestDivisibleSubset(self, nums: list[int]) -> list[int]:
        from functools import lru_cache
        arr = tuple(sorted(nums))
        n = len(arr)
        @lru_cache(None)
        def dfs(i: int, prev: int) -> tuple[int, ...]:
            if i == n:
                return ()
            best = dfs(i + 1, prev)
            if prev == -1 or arr[i] % arr[prev] == 0:
                cand = (arr[i],) + dfs(i + 1, i)
                if len(cand) > len(best) or (len(cand) == len(best) and cand < best):
                    best = cand
            return best
        return list(dfs(0, -1))
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Build the best canonical divisible chain ending at each sorted value.
```python
class Solution:
    def largestDivisibleSubset(self, nums: list[int]) -> list[int]:
        arr = sorted(nums)
        chains = []
        best = []
        for x in arr:
            cur = [x]
            for chain in chains:
                if x % chain[-1] == 0:
                    cand = chain + [x]
                    if len(cand) > len(cur) or (len(cand) == len(cur) and cand < cur):
                        cur = cand
            chains.append(cur)
            if len(cur) > len(best) or (len(cur) == len(best) and cur < best):
                best = cur
        return best
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 2^n) | O(n) |
| Better | O(n^3) | O(n^2) |
| Optimal | O(n^3) | O(n^2) |

## Edge Cases & Pitfalls
- Sort the returned subset increasingly to define one canonical form.
- Tie-breaking must be identical in all approaches.

## Related
- Longest Increasing Subsequence
- Number of Longest Increasing Subsequence
