# 10. Sqrt(x)

- **Difficulty:** Easy
- **Pattern:** Binary Search
- **Asked at:** Apple, Amazon, Microsoft, Bloomberg

## Problem
Given a non-negative integer `x`, return the integer square root of `x`, rounded down. Do not use built-in exponent functions. Constraints: `0 <= x <= 2^31 - 1`.

## Examples
```text
Input: x = 8
Output: 2
Explanation: The square root is 2.828..., so it rounds down to 2.
```

## Understanding & Intuition
We need the largest integer `r` such that `r*r <= x`. The predicate `r*r <= x` is monotonic: true for small `r`, false after the square root. Binary search finds the last true value.

## Approach 1 — Naive / Brute Force
**Idea:** Increment until the square exceeds `x`.
```python
class Solution:
    def mySqrt(self, x: int) -> int:
        r = 0
        while (r + 1) * (r + 1) <= x:
            r += 1
        return r
```
- **Time:** O(sqrt(x)) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use Newton's method with integer division until it converges.
```python
class Solution:
    def mySqrt(self, x: int) -> int:
        if x < 2:
            return x
        guess = x
        while guess * guess > x:
            guess = (guess + x // guess) // 2
        return guess
```
- **Time:** O(log x) approximately — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search the last integer whose square is at most `x`.
```python
class Solution:
    def mySqrt(self, x: int) -> int:
        left, right = 0, x
        answer = 0
        while left <= right:
            mid = (left + right) // 2
            square = mid * mid
            if square <= x:
                answer = mid
                left = mid + 1
            else:
                right = mid - 1
        return answer
```
- **Time:** O(log x) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(sqrt(x)) | O(1) |
| Better | O(log x) approximately | O(1) |
| Optimal | O(log x) | O(1) |

## Edge Cases & Pitfalls
- `0` and `1` return themselves.
- In fixed-width languages, use division checks to avoid square overflow.
- Return the floor, not a floating-point approximation.

## Related
- Valid Perfect Square
- Pow(x, n)

