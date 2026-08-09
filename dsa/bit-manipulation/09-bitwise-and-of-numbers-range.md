# 09. Bitwise AND of Numbers Range

- **Difficulty:** Medium
- **Pattern:** Bit Manipulation
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given two integers `left` and `right`, compute the bitwise AND of every integer in the inclusive range `[left, right]`.

**Input**
- `left`: the start of the range.
- `right`: the end of the range, with `left <= right`.

**Output**
- The exact value of `left & (left + 1) & ... & right`.

## Constraints
- `0 <= left <= right <= 2^31 - 1`

## Examples
```text
Input: left = 5, right = 7
Output: 4
Explanation: The inclusive range is `5, 6, 7`. In binary, their common prefix leaves only `100`, so `5 & 6 & 7 = 4`.
```

```text
Input: left = 4, right = 4
Output: 4
Explanation: A range with one number has AND equal to that number.
```

## Understanding & Intuition
Across a range, lower bits flip often and become zero in the final AND. Only the common binary prefix of `left` and `right` can survive. We can either shift both numbers until equal or repeatedly clear changing low bits from `right`.

## Approach 1 — Naive / Brute Force
**Idea:** AND every number from `left` to `right`.
```python
class Solution:
    def rangeBitwiseAnd(self, left: int, right: int) -> int:
        ans = left
        for x in range(left + 1, right + 1):
            ans &= x
            if ans == 0:
                break
        return ans
```
- **Time:** O(right - left + 1) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Shift both bounds right until their common prefix is equal, then shift back.
```python
class Solution:
    def rangeBitwiseAnd(self, left: int, right: int) -> int:
        shifts = 0
        while left < right:
            left >>= 1
            right >>= 1
            shifts += 1
        return left << shifts
```
- **Time:** O(31) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Clear the lowest set bit of `right` until `right <= left`.
```python
class Solution:
    def rangeBitwiseAnd(self, left: int, right: int) -> int:
        while right > left:
            # Removes a low bit that cannot be part of the common prefix.
            right &= right - 1
        return right
```
- **Time:** O(31) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(right - left + 1) | O(1) |
| Better | O(31) | O(1) |
| Optimal | O(31) | O(1) |

## Edge Cases & Pitfalls
- If `left == right`, return `left`.
- Any range crossing a power-of-two boundary often clears many bits.
- Do not iterate over a huge range in production solutions.

## Related
- Number of 1 Bits
- Power of Two
- Missing Number
