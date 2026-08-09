# 10. Daily Temperatures

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
Given an integer array `temperatures` of daily temperatures, return an array `answer` such that `answer[i]` is the number of days you have to wait after day `i` to get a warmer temperature. If there is no future day for which this is possible, set `answer[i] = 0`. `1 <= len(temperatures) <= 10^5`, `30 <= temperatures[i] <= 100`.

Implement `Solution.dailyTemperatures` with the parameters below and return the requested value.

**Input**
- `temperatures`: a `list[int]`; daily temperatures in chronological order.

**Output**
- A `list[int]` value representing the result described above. **This judge compares exactly**, so preserve the order required by the statement.

## Constraints
- n == temperatures.length
- 1 <= n <= 10^5
- 30 <= temperatures[i] <= 100

## Examples
```text
Input: temperatures = [73,74,75,71,69,72,76,73]
Output: [1,1,4,2,1,1,0,0]
Explanation: Day 0 (73) warms up the next day (74), so wait 1. Day 2 (75) waits until day 6 (76), so wait 4. The result is shown in the required order.
```

## Understanding & Intuition
For each day we want the distance to the next strictly greater value. A monotonic decreasing stack of indices lets each day resolve the waiting days below it the moment a warmer temperature arrives, so every index is pushed and popped once.

## Approach 1 — Naive / Brute Force
**Idea:** For every day, scan forward until a warmer day is found.
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
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Keep a monotonic decreasing stack of indices; when a warmer day arrives, pop and record distances.
```python
class Solution:
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        n = len(temperatures)
        ans = [0] * n
        stack = []
        for i, t in enumerate(temperatures):
            while stack and temperatures[stack[-1]] < t:
                j = stack.pop()
                ans[j] = i - j
            stack.append(i)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan from right to left and jump forward using already-computed answers, avoiding an explicit stack.
```python
class Solution:
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        n = len(temperatures)
        ans = [0] * n
        for i in range(n - 2, -1, -1):
            j = i + 1
            while j < n:
                if temperatures[j] > temperatures[i]:
                    ans[i] = j - i
                    break
                if ans[j] == 0:
                    break
                j += ans[j]
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The last day always has answer `0`.
- Use strict greater-than; equal temperatures do not count as warmer.
- The jump trick relies on `answer[j] == 0` meaning nothing warmer follows `j`.

## Related
- Next Greater Element I
- Online Stock Span
