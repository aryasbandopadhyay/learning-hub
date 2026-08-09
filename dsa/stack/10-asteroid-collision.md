# 10. Asteroid Collision

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Uber

## Problem
Asteroids move along a line. Positive values move right and negative values move left; absolute value is size. When a right-moving asteroid meets a left-moving one, the smaller explodes, and equal sizes both explode.

**Input**
- `asteroids`: a list of signed asteroid sizes and directions.

**Output**
- The remaining asteroids in original left-to-right order after all collisions. **This judge compares exactly** to that order.

## Constraints
- `2 <= asteroids.length <= 10^4`
- `-1000 <= asteroids[i] <= 1000`
- `asteroids[i] != 0`

## Examples
```text
Input: asteroids = [5,10,-5]
Output: [5,10]
Explanation: The `-5` moves left into `10`; since `10` is larger, `-5` explodes and `[5,10]` remain.
```

## Understanding & Intuition
A collision can only occur between the current left-moving asteroid and previous right-moving asteroids. A stack represents surviving asteroids to the left. Repeatedly compare sizes until the current asteroid is destroyed or no collision is possible.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan adjacent asteroids and resolve the first collision found.
```python
class Solution:
    def asteroidCollision(self, asteroids: list[int]) -> list[int]:
        arr = asteroids[:]
        changed = True
        while changed:
            changed = False
            i = 0
            while i + 1 < len(arr):
                if arr[i] > 0 and arr[i + 1] < 0:
                    left, right = abs(arr[i]), abs(arr[i + 1])
                    if left == right:
                        arr[i:i + 2] = []
                    elif left > right:
                        arr.pop(i + 1)
                    else:
                        arr.pop(i)
                    changed = True
                    break
                i += 1
        return arr
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack and a flag showing whether the current asteroid survives.
```python
class Solution:
    def asteroidCollision(self, asteroids: list[int]) -> list[int]:
        stack = []
        for a in asteroids:
            alive = True
            while alive and a < 0 and stack and stack[-1] > 0:
                if stack[-1] < -a:
                    stack.pop()
                    continue
                if stack[-1] == -a:
                    stack.pop()
                alive = False
            if alive:
                stack.append(a)
        return stack
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Same stack idea, written with loop `else` so survivors are appended only after all collisions end.
```python
class Solution:
    def asteroidCollision(self, asteroids: list[int]) -> list[int]:
        stack = []
        for a in asteroids:
            while stack and a < 0 < stack[-1]:
                if stack[-1] < -a:
                    stack.pop()
                    continue
                if stack[-1] == -a:
                    stack.pop()
                break
            else:
                # Runs when the asteroid was not destroyed by a break.
                stack.append(a)
        return stack
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Asteroids moving away from each other never collide.
- Equal sizes destroy both asteroids.
- A large left-moving asteroid may destroy many stack entries.

## Related
- Car Fleet
- Decode String

