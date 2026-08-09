# 11. Maximum Swap

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
You are given a non-negative integer `num`.

You may swap two digits at most once. Return the maximum integer obtainable after zero or one swap.

**Input**
- `num`: a non-negative integer.

**Output**
- The largest value obtainable with at most one digit swap.

## Constraints
- `0 <= num <= 10^8`

## Examples
```text
Input: num = 2736
Output: 7236
Explanation: Swapping the leading `2` with `7` produces `7236`, which is the largest possible one-swap result.
```

## Understanding & Intuition
The best swap improves the earliest possible digit, because earlier digits dominate the number's value. At that position, swap with the largest digit available to its right, using the rightmost occurrence to keep the suffix as large as possible. If no better digit exists, the number is already optimal.

## Approach 1 — Naive / Brute Force
**Idea:** Try every pair of digit positions and keep the largest resulting number.
```python
class Solution:
    def maximumSwap(self, num: int) -> int:
        digits = list(str(num))
        best = num
        for i in range(len(digits)):
            for j in range(i, len(digits)):
                arr = digits[:]
                arr[i], arr[j] = arr[j], arr[i]
                best = max(best, int(''.join(arr)))
        return best
```
- **Time:** O(d^3) — **Space:** O(d)

## Approach 2 — Better
**Idea:** Precompute the index of the maximum suffix digit for each position and use the first improving swap.
```python
class Solution:
    def maximumSwap(self, num):
        digits = list(str(num))
        n = len(digits)
        best_idx = [0] * n
        best_idx[-1] = n - 1
        for i in range(n - 2, -1, -1):
            if digits[i] > digits[best_idx[i + 1]]:
                best_idx[i] = i
            else:
                best_idx[i] = best_idx[i + 1]
        for i in range(n):
            j = best_idx[i]
            if digits[j] > digits[i]:
                digits[i], digits[j] = digits[j], digits[i]
                break
        return int(''.join(digits))
```
- **Time:** O(d) — **Space:** O(d)

## Approach 3 — Optimal
**Idea:** Store the last occurrence of every digit and look for the earliest position that can swap with a larger later digit.
```python
class Solution:
    def maximumSwap(self, num):
        digits = list(str(num))
        last = {int(d): i for i, d in enumerate(digits)}
        for i, ch in enumerate(digits):
            cur = int(ch)
            for d in range(9, cur, -1):
                if last.get(d, -1) > i:
                    j = last[d]
                    digits[i], digits[j] = digits[j], digits[i]
                    return int(''.join(digits))
        return num
```
- **Time:** O(d) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(d^3) | O(d) |
| Better | O(d) | O(d) |
| Optimal | O(d) | O(1) |

## Edge Cases & Pitfalls
- Swapping is optional; return `num` if no improvement exists.
- Use the rightmost larger digit to maximize the remaining suffix.
- Repeated digits require tracking the last occurrence, not the first.

## Related
- Monotone Increasing Digits
- Next Permutation
