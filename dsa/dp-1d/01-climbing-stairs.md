# 01. Climbing Stairs

- **Difficulty:** Easy
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
You are climbing a staircase with exactly `n` steps. On each move, you may climb either 1 step or 2
steps. Count how many distinct sequences of moves reach exactly the top step.

**Input**
- `n`: the number of steps in the staircase.

**Output**
- An integer: the number of distinct valid step sequences.

## Constraints
- 1 <= n <= 45

## Examples
```text
Input: n = 3
Output: 3
Explanation: There are three valid move sequences: `1+1+1`, `1+2`, and `2+1`.
```

## Understanding & Intuition
Let `dp[i]` be the number of ways to reach step `i`. The last move into `i` came from `i-1` or `i-2`, so `dp[i] = dp[i-1] + dp[i-2]`. This is the Fibonacci recurrence with small base cases.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try taking 1 or 2 steps from the current position.
```python
class Solution:
    def climbStairs(self, n: int) -> int:
        def ways(step: int) -> int:
            # Reached the top exactly.
            if step == n:
                return 1
            if step > n:
                return 0
            return ways(step + 1) + ways(step + 2)

        return ways(0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the number of ways from each step.
```python
class Solution:
    def climbStairs(self, n: int) -> int:
        memo = {}

        def ways(step: int) -> int:
            if step == n:
                return 1
            if step > n:
                return 0
            if step not in memo:
                memo[step] = ways(step + 1) + ways(step + 2)
            return memo[step]

        return ways(0)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Build the recurrence bottom-up with two rolling values.
```python
class Solution:
    def climbStairs(self, n: int) -> int:
        one, two = 1, 1  # ways for the next one and next two steps
        for _ in range(n - 1):
            one, two = one + two, one
        return one
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `n = 1` has exactly one way.
- Avoid off-by-one errors in base cases.

## Related
- Fibonacci Number
- Min Cost Climbing Stairs
