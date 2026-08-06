# 05. Daily Temperatures

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given daily temperatures, return an array where each element is the number of days until a warmer temperature. If no warmer day exists, return `0` for that day. Constraints: `1 <= len(temperatures) <= 10^5`, `30 <= temperatures[i] <= 100`.

## Examples
```text
Input: temperatures = [73,74,75,71,69,72,76,73]
Output: [1,1,4,2,1,1,0,0]
Explanation: Day 2 waits four days until temperature 76.
```

## Understanding & Intuition
Each day needs the next greater temperature to its right. A monotonic stack keeps days whose answer has not been found yet. When a warmer day arrives, it resolves all colder days on top of the stack.

## Approach 1 — Naive / Brute Force
**Idea:** For each day, scan forward until a warmer temperature is found.
```python
class Solution:
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        n = len(temperatures)
        ans = [0] * n
        for i in range(n):
            for j in range(i + 1, n):
                if temperatures[j] > temperatures[i]:
                    ans[i] = j - i
                    break
        return ans
```
- **Time:** O(n^2) — **Space:** O(1) extra

## Approach 2 — Better
**Idea:** Since temperatures are bounded, track the next index for each possible temperature.
```python
class Solution:
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        n = len(temperatures)
        next_pos = [10**9] * 101
        ans = [0] * n
        for i in range(n - 1, -1, -1):
            warmer = min(next_pos[t] for t in range(temperatures[i] + 1, 101))
            if warmer < 10**9:
                ans[i] = warmer - i
            # Record the closest occurrence of this exact temperature.
            next_pos[temperatures[i]] = i
        return ans
```
- **Time:** O(71n) = O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Maintain a decreasing stack of unresolved indices.
```python
class Solution:
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        ans = [0] * len(temperatures)
        stack = []
        for i, temp in enumerate(temperatures):
            # Current day answers all previous colder days.
            while stack and temp > temperatures[stack[-1]]:
                j = stack.pop()
                ans[j] = i - j
            stack.append(i)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Equal temperatures are not warmer.
- Unresolved indices correctly remain `0`.
- Store indices, not temperatures, because the answer is a distance.

## Related
- Next Greater Element I
- Next Greater Element II

