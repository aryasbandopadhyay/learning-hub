# 05. Plus One

- **Difficulty:** Easy
- **Pattern:** Math & Geometry
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
You are given a non-empty list `digits` representing a non-negative integer. Each element is one decimal digit, and the most significant digit appears first.

Add one to the represented integer and return the resulting digits in the same most-significant to least-significant order. The input has no leading zeroes unless the number itself is `0`.

**Input**
- `digits`: a list of decimal digits representing one integer.

**Output**
- A list of digits for the value `digits + 1`, in exact left-to-right number order.

## Constraints
- `1 <= digits.length <= 100`
- `0 <= digits[i] <= 9`
- `digits` does not contain leading zeroes unless it is exactly `[0]`.

## Examples
```text
Input: digits = [9,9]
Output: [1,0,0]
Explanation: The number is `99`. Adding one creates a carry through both digits and adds a new leading `1`, so the result is `[1,0,0]`.
```

```text
Input: digits = [1,2,3]
Output: [1,2,4]
Explanation: The represented number is `123`, and `123 + 1 = 124`.
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
