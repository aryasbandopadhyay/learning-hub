# 04. Maximum Population Year

- **Difficulty:** Medium
- **Pattern:** interval merging & sweep line
- **Asked at:** Google, Amazon, Microsoft

## Problem
Given `logs`, where each log is `[birth, death]`, a person is alive during every year `birth <= year < death`. Return the earliest year with the maximum living population.

Constraints: `1 <= len(logs) <= 10^5`, `1900 <= birth < death <= 2100`.

## Examples
```text
Input: logs = [[1993,1999],[2000,2010],[1975,2005],[1990,2001]]
Output: 1993
Explanation: Population first reaches its maximum in 1993.
```

## Understanding & Intuition
Each log is a half-open interval on years. A birth increases population at `birth`, while death decreases it at `death`. Sweeping these changes in year order gives the population for each year.

## Approach 1 — Naive / Brute Force
**Idea:** Test every possible year and count how many logs contain it.
```python
class Solution:
    def maximumPopulation(self, logs: list[list[int]]) -> int:
        best_year = 1900
        best_pop = -1
        for year in range(1900, 2101):
            pop = 0
            for birth, death in logs:
                if birth <= year < death:
                    pop += 1
            if pop > best_pop:
                best_pop = pop
                best_year = year
        return best_year
```
- **Time:** O(201n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Create explicit birth/death events and sort them.
```python
class Solution:
    def maximumPopulation(self, logs: list[list[int]]) -> int:
        events = []
        for birth, death in logs:
            events.append((birth, 1))
            events.append((death, -1))
        events.sort()
        pop = 0
        best_pop = -1
        best_year = 1900
        i = 0
        while i < len(events):
            year = events[i][0]
            while i < len(events) and events[i][0] == year:
                pop += events[i][1]
                i += 1
            if pop > best_pop:
                best_pop = pop
                best_year = year
        return best_year
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a difference array over the bounded year range and scan once.
```python
class Solution:
    def maximumPopulation(self, logs: list[list[int]]) -> int:
        base = 1900
        diff = [0] * 202
        for birth, death in logs:
            diff[birth - base] += 1
            diff[death - base] -= 1
        pop = 0
        best_pop = -1
        best_year = base
        for offset in range(201):
            pop += diff[offset]
            if pop > best_pop:
                best_pop = pop
                best_year = base + offset
        return best_year
```
- **Time:** O(n + 201) — **Space:** O(201)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(201n) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n + 201) | O(201) |

## Edge Cases & Pitfalls
- Death years are not alive years.
- Return the earliest year when ties occur.
- The bounded year range enables a difference-array solution.

## Related
- Car Pooling
- Range Addition
