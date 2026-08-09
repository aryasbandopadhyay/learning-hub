# 05. Missing Number

- **Difficulty:** Easy
- **Pattern:** Bit Manipulation
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
You are given a list `nums` containing `n` distinct numbers chosen from the range `0` through `n`. Exactly one number from that range is missing.

Find and return the missing number.

**Input**
- `nums`: a list of `n` distinct integers from the inclusive range `[0, n]`.

**Output**
- The single integer in `[0, n]` that does not appear in `nums`.

## Constraints
- `n == nums.length`
- `1 <= n <= 10^4`
- `0 <= nums[i] <= n`
- All values in `nums` are distinct.

## Examples
```text
Input: nums = [3,0,1]
Output: 2
Explanation: Since the list has length `3`, the complete range should be `{0,1,2,3}`. The values `3`, `0`, and `1` are present, so `2` is missing.
```

```text
Input: nums = [1]
Output: 0
Explanation: The range is `{0,1}`, and `0` is the absent value.
```

## Understanding & Intuition
The full range is known, so we can compare the array against `0..n`. XOR cancels values that appear in both the expected range and the actual array. The survivor is the missing number.

## Approach 1 — Naive / Brute Force
**Idea:** For every value in `[0, n]`, scan the array to see whether it exists.
```python
from typing import List

class Solution:
    def missingNumber(self, nums: List[int]) -> int:
        n = len(nums)
        for target in range(n + 1):
            found = False
            for x in nums:
                if x == target:
                    found = True
                    break
            if not found:
                return target
        return -1
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use the arithmetic sum formula and subtract actual values.
```python
from typing import List

class Solution:
    def missingNumber(self, nums: List[int]) -> int:
        n = len(nums)
        expected = n * (n + 1) // 2
        actual = sum(nums)
        return expected - actual
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** XOR all indices and values, plus `n`, so paired values cancel.
```python
from typing import List

class Solution:
    def missingNumber(self, nums: List[int]) -> int:
        missing = len(nums)
        for i, x in enumerate(nums):
            # Index i represents an expected value; x is an actual value.
            missing ^= i ^ x
        return missing
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The missing number may be `0` or `n`.
- The array has length `n`, but the valid range has `n + 1` values.
- Sum formula is simple in Python because integers do not overflow.

## Related
- Single Number
- Counting Bits
- Bitwise AND of Numbers Range
