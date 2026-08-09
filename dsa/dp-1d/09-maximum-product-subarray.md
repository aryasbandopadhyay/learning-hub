# 09. Maximum Product Subarray

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, LinkedIn

## Problem
Given an integer array `nums`, find the contiguous non-empty subarray whose elements have the largest
product. Return that product.

**Input**
- `nums`: a list of integers.

**Output**
- An integer: the maximum product over all non-empty contiguous subarrays.

## Constraints
- 1 <= nums.length <= 2 * 10^4
- -10 <= nums[i] <= 10
- The product of any prefix or suffix fits in a 32-bit signed integer.

## Examples
```text
Input: nums = [2,3,-2,4]
Output: 6
Explanation: The subarray `[2,3]` has product `6`. Extending it by `-2` makes the product negative, and no other subarray has a larger product.
```

## Understanding & Intuition
Because multiplying by a negative swaps largest and smallest products, track both maximum and minimum product ending at each index. The recurrence considers starting fresh at `nums[i]` or extending the previous max/min. The answer is the largest ending maximum seen.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively enumerate every start and extend the product.
```python
from typing import List

class Solution:
    def maxProduct(self, nums: List[int]) -> int:
        best = nums[0]

        def extend(start: int, end: int, product: int) -> None:
            nonlocal best
            if end == len(nums):
                return
            product *= nums[end]
            best = max(best, product)
            extend(start, end + 1, product)

        for i in range(len(nums)):
            extend(i, i, 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize `(max_product, min_product)` ending at each index.
```python
from typing import List, Tuple

class Solution:
    def maxProduct(self, nums: List[int]) -> int:
        memo = {}

        def ending(i: int) -> Tuple[int, int]:
            if i == 0:
                return nums[0], nums[0]
            if i not in memo:
                prev_max, prev_min = ending(i - 1)
                x = nums[i]
                memo[i] = (max(x, x * prev_max, x * prev_min),
                           min(x, x * prev_max, x * prev_min))
            return memo[i]

        return max(ending(i)[0] for i in range(len(nums)))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep only current max/min ending products.
```python
from typing import List

class Solution:
    def maxProduct(self, nums: List[int]) -> int:
        cur_max = cur_min = ans = nums[0]
        for x in nums[1:]:
            a, b = x * cur_max, x * cur_min
            cur_max = max(x, a, b)
            cur_min = min(x, a, b)
            ans = max(ans, cur_max)
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Zeros reset products naturally by choosing `x`.
- Negative numbers require tracking the minimum product too.

## Related
- Maximum Subarray
- Product of Array Except Self
