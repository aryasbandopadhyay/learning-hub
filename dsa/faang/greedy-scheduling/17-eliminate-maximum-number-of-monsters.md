# 17. Eliminate Maximum Number of Monsters

- **Difficulty:** Medium
- **Pattern:** greedy scheduling
- **Asked at:** Amazon, Google, Microsoft

## Problem
Implement `eliminateMaximum` for **Eliminate Maximum Number of Monsters**. There are monsters moving toward your city. Monster `i` starts at distance `dist[i]` and moves at speed `speed[i]`. At the start of each minute, including minute `0`, you may eliminate one monster. If any monster reaches the city before you can eliminate a monster at that minute, you lose. Return the maximum number of monsters you can eliminate.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `dist`: list; distances in order.
- `speed`: list; monster speeds.

**Output**
- A single integer.

## Constraints
- `1 <= len(dist) == len(speed) <= 10^5`, `1 <= dist[i], speed[i] <= 10^5`

## Examples
```text
Input: dist = [1,3,4], speed = [1,1,1]
Output: 3
Explanation: The monsters arrive at minutes 1, 3, and 4, so all can be eliminated first. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Each monster has a latest integer minute before which it must be eliminated. The safest choice is always to eliminate the monster with the earliest arrival deadline. If the `i`th earliest deadline is at or before minute `i`, that monster reaches the city before the next shot.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate each minute and repeatedly choose the alive monster with the earliest arrival time.
```python
class Solution:
    def eliminateMaximum(self, dist: list[int], speed: list[int]) -> int:
        n = len(dist)
        alive = [True] * n
        for minute in range(n):
            for i in range(n):
                if alive[i] and dist[i] <= speed[i] * minute:
                    return minute
            best = -1
            for i in range(n):
                if alive[i]:
                    if best == -1 or dist[i] * speed[best] < dist[best] * speed[i]:
                        best = i
            alive[best] = False
        return n
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Convert each monster to an integer arrival deadline, sort deadlines, and shoot in that order.
```python
class Solution:
    def eliminateMaximum(self, dist: list[int], speed: list[int]) -> int:
        deadlines = []
        for d, s in zip(dist, speed):
            deadlines.append((d + s - 1) // s)
        deadlines.sort()
        for minute, deadline in enumerate(deadlines):
            if deadline <= minute:
                return minute
        return len(dist)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Bucket deadlines up to `n`, because any monster arriving after minute `n` cannot stop us from eliminating all monsters.
```python
class Solution:
    def eliminateMaximum(self, dist: list[int], speed: list[int]) -> int:
        n = len(dist)
        buckets = [0] * (n + 1)
        for d, s in zip(dist, speed):
            deadline = (d + s - 1) // s
            buckets[min(deadline, n)] += 1
        arrived = 0
        for minute in range(n):
            arrived += buckets[minute]
            if arrived > minute:
                return minute
        return n
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A monster with deadline equal to the current minute has already arrived before that shot.
- Use ceiling division for arrival minute.
- Deadlines greater than `n` can share one final bucket.

## Related
- Minimum Number of Arrows to Burst Balloons
- Course Schedule III
