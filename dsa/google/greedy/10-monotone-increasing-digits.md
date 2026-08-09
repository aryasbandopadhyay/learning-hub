# 10. Monotone Increasing Digits

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
You are given a non-negative integer `n`.

Return the largest integer less than or equal to `n` whose decimal digits are monotone increasing, meaning each digit is less than or equal to the digit after it.

**Input**
- `n`: a non-negative integer.

**Output**
- The largest monotone-increasing integer `<= n`.

## Constraints
- `0 <= n <= 10^9`

## Examples
```text
Input: n = 332
Output: 299
Explanation: `332` is not monotone because `3 > 2`. The largest valid number not exceeding it is `299`.
```

## Understanding & Intuition
A violation occurs when a digit is larger than the digit to its right. Decreasing the left digit by one and making all following digits `9` gives the largest possible suffix after fixing that violation. Cascading left handles cases like `332`.

## Approach 1 — Naive / Brute Force
**Idea:** Generate all monotone numbers by DFS and keep the largest one not exceeding `n`.
```python
class Solution:
    def monotoneIncreasingDigits(self, n: int) -> int:
        best = 0
        def dfs(last, value):
            nonlocal best
            if value > n:
                return
            best = max(best, value)
            for d in range(last, 10):
                if value == 0 and d == 0:
                    continue
                dfs(d, value * 10 + d)
        dfs(0, 0)
        return best
```
- **Time:** O(C(d+9,9)) — **Space:** O(d)

## Approach 2 — Better
**Idea:** Scan left to right until the first descent, step back through equal digits, then fill the suffix with `9`.
```python
class Solution:
    def monotoneIncreasingDigits(self, n):
        digits = list(str(n))
        i = 1
        while i < len(digits) and digits[i - 1] <= digits[i]:
            i += 1
        if i == len(digits):
            return n
        while i > 0 and digits[i - 1] > digits[i]:
            digits[i - 1] = str(int(digits[i - 1]) - 1)
            i -= 1
        for j in range(i + 1, len(digits)):
            digits[j] = '9'
        return int(''.join(digits))
```
- **Time:** O(d) — **Space:** O(d)

## Approach 3 — Optimal
**Idea:** Scan right to left, marking the first suffix position that must become all `9`s.
```python
class Solution:
    def monotoneIncreasingDigits(self, n):
        digits = list(str(n))
        marker = len(digits)
        for i in range(len(digits) - 1, 0, -1):
            if digits[i - 1] > digits[i]:
                digits[i - 1] = str(int(digits[i - 1]) - 1)
                marker = i
        for i in range(marker, len(digits)):
            digits[i] = '9'
        return int(''.join(digits))
```
- **Time:** O(d) — **Space:** O(d)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(C(d+9,9)) | O(d) |
| Better | O(d) | O(d) |
| Optimal | O(d) | O(d) |

## Edge Cases & Pitfalls
- Decrementing can create a new violation to the left.
- Leading zeroes after decrement are harmless when converting back to `int`.
- Already monotone numbers should be returned unchanged.

## Related
- Remove K Digits
- Greedy Digit Manipulation
