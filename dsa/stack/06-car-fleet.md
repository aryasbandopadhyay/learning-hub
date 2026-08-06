# 06. Car Fleet

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Uber

## Problem
Given a `target`, arrays `position` and `speed`, return how many car fleets arrive at the target. A faster car that catches a slower car becomes part of that fleet and cannot pass it. Constraints: `1 <= len(position) <= 10^5`, positions are unique, `0 < position[i] < target`.

## Examples
```text
Input: target = 12, position = [10,8,0,5,3], speed = [2,4,1,1,3]
Output: 3
Explanation: Cars form fleets from positions 10, 8, and the group starting behind position 5.
```

## Understanding & Intuition
Cars closer to the target determine whether cars behind can form a new fleet. Sort by position descending and compare arrival times. A car behind joins the fleet ahead if it arrives no later than that fleet.

## Approach 1 — Naive / Brute Force
**Idea:** Sort cars, then repeatedly merge a car into the fleet immediately ahead when it can catch it.
```python
class Solution:
    def carFleet(self, target: int, position: list[int], speed: list[int]) -> int:
        cars = sorted(zip(position, speed), reverse=True)
        times = [(target - p) / s for p, s in cars]
        fleets = []
        for time in times:
            fleets.append(time)
            # If the new rear car catches the fleet ahead, merge it.
            while len(fleets) >= 2 and fleets[-1] <= fleets[-2]:
                fleets.pop()
        return len(fleets)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack of fleet arrival times after sorting by position.
```python
class Solution:
    def carFleet(self, target: int, position: list[int], speed: list[int]) -> int:
        stack = []
        for p, s in sorted(zip(position, speed), reverse=True):
            time = (target - p) / s
            if not stack or time > stack[-1]:
                # Slower than the fleet ahead, so it starts a new fleet.
                stack.append(time)
        return len(stack)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track only the slowest arrival time seen so far; the stack can be compressed to a counter.
```python
class Solution:
    def carFleet(self, target: int, position: list[int], speed: list[int]) -> int:
        fleets = 0
        slowest_time_ahead = 0.0
        for p, s in sorted(zip(position, speed), reverse=True):
            time = (target - p) / s
            if time > slowest_time_ahead:
                fleets += 1
                slowest_time_ahead = time
        return fleets
```
- **Time:** O(n log n) — **Space:** O(n) for sorting

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Sort positions from closest to farthest from the target.
- Equal arrival time means the rear car joins the front fleet.
- The sorting cost dominates the problem.

## Related
- Daily Temperatures
- Asteroid Collision

