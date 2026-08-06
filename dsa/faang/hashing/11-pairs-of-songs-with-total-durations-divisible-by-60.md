# 11. Pairs of Songs With Total Durations Divisible by 60

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Amazon, Apple, Google

## Problem
Given a list `time` where `time[i]` is the duration of the `i`-th song in seconds, return the number of pairs `(i, j)` with `i < j` such that `(time[i] + time[j])` is divisible by 60.

Constraints: `1 <= len(time) <= 60_000`, and `1 <= time[i] <= 500`.

## Examples
```text
Input: time = [30, 20, 150, 100, 40]
Output: 3
Explanation: The valid pairs have durations (30,150), (20,100), and (20,40).
```

## Understanding & Intuition
Only each duration's remainder modulo 60 matters. A song with remainder `r` pairs with previous songs of remainder `(60 - r) % 60`. Counting remainders avoids checking all pairs.

## Approach 1 — Naive / Brute Force
**Idea:** Check every pair of songs.
```python
class Solution:
    def numPairsDivisibleBy60(self, time: list[int]) -> int:
        ans = 0
        for i in range(len(time)):
            for j in range(i + 1, len(time)):
                if (time[i] + time[j]) % 60 == 0:
                    ans += 1
        return ans
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Count all remainders first, then use combinations for complementary remainder classes.
```python
class Solution:
    def numPairsDivisibleBy60(self, time):
        counts = [0] * 60
        for t in time:
            counts[t % 60] += 1
        ans = counts[0] * (counts[0] - 1) // 2
        ans += counts[30] * (counts[30] - 1) // 2
        for r in range(1, 30):
            ans += counts[r] * counts[60 - r]
        return ans
```
- **Time:** O(n + 60) — **Space:** O(60)

## Approach 3 — Optimal
**Idea:** Stream through songs and add the number of previously seen complementary remainders.
```python
class Solution:
    def numPairsDivisibleBy60(self, time):
        counts = [0] * 60
        ans = 0
        for t in time:
            r = t % 60
            ans += counts[(60 - r) % 60]
            counts[r] += 1
        return ans
```
- **Time:** O(n) — **Space:** O(60)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(1) |
| Better | O(n + 60) | O(60) |
| Optimal | O(n) | O(60) |

## Edge Cases & Pitfalls
- Remainder 0 pairs with remainder 0.
- Remainder 30 pairs with remainder 30.
- Count pairs by indices, so duplicate durations matter.

## Related
- Two Sum
- Subarray Sums Divisible by K
