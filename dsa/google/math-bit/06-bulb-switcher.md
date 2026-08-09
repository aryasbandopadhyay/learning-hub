# 06. Bulb Switcher

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
There are `n` bulbs initially off. In round `i`, you toggle every `i`th bulb. After `n` rounds, return how many bulbs are on.

Implement `Solution.bulbSwitch` with the parameters below and return the requested value.

**Input**
- `n`: a `int`; the size/count parameter described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- `0 <= n <= 10^9`

## Examples
```text
Input: n = 25
Output: 5
Explanation: Only bulbs numbered 1, 4, 9, 16, and 25 have an odd number of divisors.
```

## Understanding & Intuition
Bulb `k` is toggled once for each divisor of `k`. Divisors usually pair up, producing an even number of toggles. Perfect squares have one unpaired square-root divisor, so exactly those bulbs remain on.

## Approach 1 — Naive / Brute Force
**Idea:** Count square numbers directly by checking successive square roots.
```python
class Solution:
    def bulbSwitch(self, n: int) -> int:
        root = 0
        while (root + 1) * (root + 1) <= n:
            root += 1
        return root
```
- **Time:** O(sqrt n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use Newton's method to compute the integer square root without floating point.
```python
class Solution:
    def bulbSwitch(self, n: int) -> int:
        if n == 0:
            return 0
        x = n
        while True:
            y = (x + n // x) // 2
            if y >= x:
                return x
            x = y
```
- **Time:** O(log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Count perfect squares not exceeding `n`, which is `floor(sqrt(n))`.
```python
class Solution:
    def bulbSwitch(self, n: int) -> int:
        lo, hi, ans = 0, n, 0
        while lo <= hi:
            mid = (lo + hi) // 2
            if mid * mid <= n:
                ans = mid
                lo = mid + 1
            else:
                hi = mid - 1
        return ans
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(sqrt n) | O(1) |
| Better | O(log n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- `n = 0` returns `0`.
- Do not count non-square numbers; paired divisors cancel out.
- Floating square roots can be avoided with integer binary search.

## Related
- Sqrt(x)
- Happy Number
