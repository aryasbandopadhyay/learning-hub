# 05. Plus One

- **Difficulty:** Easy
- **Pattern:** Math & Geometry
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
Given an array of decimal digits representing a non-negative integer without leading zeros, return the digits after adding one. Constraints: `1 <= digits.length <= 100`.

## Examples
```text
Input: digits = [9,9]
Output: [1,0,0]
Explanation: 99 + 1 = 100.
```

## Understanding & Intuition
Addition starts from the least significant digit. If a digit is below `9`, incrementing it stops the carry. A suffix of `9`s becomes zeros.

## Approach 1 — Naive / Brute Force
**Idea:** Convert digits to an integer, add one, and convert back.
```python
from typing import List

class Solution:
    def plusOne(self, digits: List[int]) -> List[int]:
        value = 0
        for digit in digits:
            value = value * 10 + digit
        value += 1
        return [int(ch) for ch in str(value)]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a result copy and propagate a carry from right to left.
```python
from typing import List

class Solution:
    def plusOne(self, digits: List[int]) -> List[int]:
        res = digits[:]
        carry = 1
        for i in range(len(res) - 1, -1, -1):
            total = res[i] + carry
            res[i] = total % 10
            carry = total // 10
            if carry == 0:
                break
        if carry:
            res.insert(0, carry)
        return res
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Mutate the input digits in-place and allocate only if all digits are `9`.
```python
from typing import List

class Solution:
    def plusOne(self, digits: List[int]) -> List[int]:
        for i in range(len(digits) - 1, -1, -1):
            if digits[i] < 9:
                digits[i] += 1
                return digits
            digits[i] = 0
        return [1] + digits
```
- **Time:** O(n) — **Space:** O(1) extra

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) extra |

## Edge Cases & Pitfalls
- All digits `9` require a new leading `1`.
- Do not drop zeros after the carry propagation.

## Related
- Add Binary
- Add Two Numbers
