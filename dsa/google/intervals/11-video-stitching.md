# 11. Video Stitching

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, Amazon, Hulu

## Problem
You are given video clips `clips`, where `clips[i] = [start, end]`, and a target duration `time`. Return the minimum number of clips needed to cover the entire interval `[0, time]`. If coverage is impossible, return `-1`.

Implement `Solution.videoStitching` with the parameters below and return the requested value.

**Input**
- `clips`: a `list[list[int]]`; video clips as `[start, end]` intervals.
- `time`: a `int`; the target video duration.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(clips) <= 10^4`, `0 <= start <= end <= 10^5`, `1 <= time <= 10^5`

## Examples
```text
Input: clips = [[0,2],[4,6],[8,10],[1,9],[1,5],[5,9]], time = 10
Output: 3
Explanation: Clips [0,2], [1,9], and [8,10] cover [0,10].
```

## Understanding & Intuition
This is interval covering from the origin to a target. At each current coverage boundary, among all clips that start no later than that boundary, choose the one that extends farthest. That greedy choice minimizes the number of jumps, like Jump Game II over ranges.

## Approach 1 — Naive / Brute Force
**Idea:** Use recursion over current covered prefix and try every clip that can extend it.
```python
class Solution:
    def videoStitching(self, clips: list[list[int]], time: int) -> int:
        from functools import lru_cache
        clips = tuple((s, e) for s, e in clips if e > s)

        @lru_cache(None)
        def solve(covered):
            if covered >= time:
                return 0
            best = 10 ** 9
            for start, end in clips:
                if start <= covered < end:
                    best = min(best, 1 + solve(min(end, time)))
            return best

        answer = solve(0)
        return -1 if answer >= 10 ** 9 else answer
```
- **Time:** O(nT) — **Space:** O(T)

## Approach 2 — Better
**Idea:** Dynamic programming where `dp[t]` is the fewest clips needed to cover `[0,t]`.
```python
class Solution:
    def videoStitching(self, clips: list[list[int]], time: int) -> int:
        inf = 10 ** 9
        dp = [inf] * (time + 1)
        dp[0] = 0
        for t in range(1, time + 1):
            for start, end in clips:
                if start < t <= end and dp[start] != inf:
                    dp[t] = min(dp[t], dp[start] + 1)
        return -1 if dp[time] == inf else dp[time]
```
- **Time:** O(nT) — **Space:** O(T)

## Approach 3 — Optimal
**Idea:** Sort clips and greedily extend the current covered prefix as far as possible before committing one clip.
```python
class Solution:
    def videoStitching(self, clips: list[list[int]], time: int) -> int:
        clips.sort()
        i = 0
        used = 0
        current_end = 0
        farthest = 0
        while current_end < time:
            while i < len(clips) and clips[i][0] <= current_end:
                farthest = max(farthest, clips[i][1])
                i += 1
            if farthest == current_end:
                return -1
            used += 1
            current_end = farthest
        return used
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nT) | O(T) |
| Better | O(nT) | O(T) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- Clips that start after the current covered point cannot help yet.
- Zero-length clips do not extend coverage.
- Return `-1` if there is any uncovered gap before `time`.

## Related
- Jump Game II
- Minimum Number of Taps to Open to Water a Garden
- Merge Intervals
