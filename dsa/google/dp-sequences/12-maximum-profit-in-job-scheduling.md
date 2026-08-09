# 12. Maximum Profit in Job Scheduling

- **Difficulty:** Hard
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Facebook

## Problem
You are given equal-length arrays `startTime`, `endTime`, and `profit` describing jobs.

Job `i` runs from `startTime[i]` to `endTime[i]` and earns `profit[i]`. Choose non-overlapping jobs to maximize profit. A job ending at time `t` may be followed by a job starting at time `t`.

**Input**
- `startTime`: a list of job start times.
- `endTime`: a list of job end times.
- `profit`: a list of job profits.

**Output**
- The maximum profit from a compatible job schedule.

## Constraints
- `1 <= startTime.length == endTime.length == profit.length <= 5 * 10^4`
- `1 <= startTime[i] < endTime[i] <= 10^9`
- `1 <= profit[i] <= 10^4`

## Examples
```text
Input: startTime = [1,2,3,3], endTime = [3,4,5,6], profit = [50,10,40,70]
Output: 120
Explanation: Take the jobs `[1,3]` for `50` and `[3,6]` for `70`; they do not overlap and total `120`.
```

## Understanding & Intuition
Sort jobs by start time. For each job, either skip it or take it and jump to the next job whose start is at least this job's end.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively skip or take each sorted job, linearly finding the next compatible job.
```python
class Solution:
    def jobScheduling(self, startTime: list[int], endTime: list[int], profit: list[int]) -> int:
        jobs = sorted(zip(startTime, endTime, profit))
        n = len(jobs)
        def dfs(i: int) -> int:
            if i == n:
                return 0
            j = i + 1
            while j < n and jobs[j][0] < jobs[i][1]:
                j += 1
            return max(dfs(i + 1), jobs[i][2] + dfs(j))
        return dfs(0)
```
- **Time:** O(2^n * n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the recursion and use binary search for the next compatible start time.
```python
class Solution:
    def jobScheduling(self, startTime: list[int], endTime: list[int], profit: list[int]) -> int:
        from bisect import bisect_left
        from functools import lru_cache
        jobs = sorted(zip(startTime, endTime, profit))
        starts = [s for s, _, _ in jobs]
        n = len(jobs)
        @lru_cache(None)
        def dfs(i: int) -> int:
            if i == n:
                return 0
            j = bisect_left(starts, jobs[i][1])
            return max(dfs(i + 1), jobs[i][2] + dfs(j))
        return dfs(0)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Bottom-up DP over sorted jobs with binary-searched transitions.
```python
class Solution:
    def jobScheduling(self, startTime: list[int], endTime: list[int], profit: list[int]) -> int:
        from bisect import bisect_left
        jobs = sorted(zip(startTime, endTime, profit))
        starts = [s for s, _, _ in jobs]
        n = len(jobs)
        dp = [0] * (n + 1)
        for i in range(n - 1, -1, -1):
            j = bisect_left(starts, jobs[i][1])
            dp[i] = max(dp[i + 1], jobs[i][2] + dp[j])
        return dp[0]
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n * n) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- End time equal to next start time is allowed.
- Sort all three arrays together as jobs.

## Related
- Best Team With No Conflicts
- Maximum Length of Pair Chain
