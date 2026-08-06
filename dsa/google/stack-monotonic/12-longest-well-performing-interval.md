# 12. Longest Well-Performing Interval

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
A day is tiring if `hours[i] > 8`. Return the length of the longest interval where tiring days are strictly more numerous than non-tiring days. Constraints: `1 <= len(hours) <= 10^4`, `0 <= hours[i] <= 16`.

## Examples
```text
Input: hours = [9,9,6,0,6,6,9]
Output: 3
Explanation: The first three days have two tiring days and one non-tiring day.
```

## Understanding & Intuition
Convert each day to `+1` for tiring and `-1` otherwise. An interval is well-performing when its prefix-sum difference is positive. A decreasing stack of prefix indexes preserves the earliest low prefix for each later higher prefix.

## Approach 1 — Naive / Brute Force
**Idea:** Check every interval by summing tiring and non-tiring scores.
```python
from typing import List

class Solution:
    def longestWPI(self, hours: List[int]) -> int:
        best = 0
        for left in range(len(hours)):
            score = 0
            for right in range(left, len(hours)):
                score += 1 if hours[right] > 8 else -1
                if score > 0:
                    best = max(best, right - left + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Track the first occurrence of each prefix score and use `score - 1` for the best prior prefix.
```python
from typing import List

class Solution:
    def longestWPI(self, hours: List[int]) -> int:
        first = {}
        score = 0
        best = 0
        for i, hour in enumerate(hours):
            score += 1 if hour > 8 else -1
            if score > 0:
                best = i + 1
            elif score - 1 in first:
                best = max(best, i - first[score - 1])
            if score not in first:
                first[score] = i
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Build prefix sums and match later higher prefixes with earliest decreasing-stack prefixes.
```python
from typing import List

class Solution:
    def longestWPI(self, hours: List[int]) -> int:
        prefix = [0]
        for hour in hours:
            prefix.append(prefix[-1] + (1 if hour > 8 else -1))
        stack = []
        for i, value in enumerate(prefix):
            if not stack or value < prefix[stack[-1]]:
                stack.append(i)
        best = 0
        for j in range(len(prefix) - 1, -1, -1):
            while stack and prefix[j] > prefix[stack[-1]]:
                best = max(best, j - stack.pop())
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The condition is strictly more tiring days.
- Hours equal to 8 are non-tiring.
- Prefix index `0` represents the empty prefix before day 0.

## Related
- Maximum Width Ramp
- Prefix Sum
