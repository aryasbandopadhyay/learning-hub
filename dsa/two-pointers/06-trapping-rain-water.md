# 06. Trapping Rain Water

- **Difficulty:** Hard
- **Pattern:** Two Pointers
- **Asked at:** Amazon, Meta, Google, Microsoft, Bloomberg

## Problem
Given non-negative integers `height` representing an elevation map, compute how much water can be trapped after raining. Constraints: `1 <= len(height) <= 2 * 10^4`.

## Examples
```text
Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
Output: 6
Explanation: The bars trap 6 total units of water.
```

## Understanding & Intuition
Water above an index depends on the smaller of the highest wall to its left and right. Precomputing those maxima is straightforward. The optimal two-pointer method keeps enough boundary information without arrays.

## Approach 1 — Naive / Brute Force
**Idea:** For every index, scan left and right to find its water boundary.
```python
from typing import List

class Solution:
    def trap(self, height: List[int]) -> int:
        water = 0
        n = len(height)
        for i in range(n):
            left_max = max(height[:i + 1])
            right_max = max(height[i:])
            water += min(left_max, right_max) - height[i]
        return water
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Precompute left and right maximum heights for each index.
```python
from typing import List

class Solution:
    def trap(self, height: List[int]) -> int:
        n = len(height)
        if n == 0:
            return 0
        left_max = [0] * n
        right_max = [0] * n
        left_max[0] = height[0]
        for i in range(1, n):
            left_max[i] = max(left_max[i - 1], height[i])
        right_max[-1] = height[-1]
        for i in range(n - 2, -1, -1):
            right_max[i] = max(right_max[i + 1], height[i])
        return sum(min(left_max[i], right_max[i]) - height[i] for i in range(n))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use two pointers with running left and right maxima, processing the side with the lower boundary.
```python
from typing import List

class Solution:
    def trap(self, height: List[int]) -> int:
        left, right = 0, len(height) - 1
        left_max = right_max = 0
        water = 0
        while left < right:
            if height[left] < height[right]:
                left_max = max(left_max, height[left])
                water += left_max - height[left]
                left += 1
            else:
                right_max = max(right_max, height[right])
                water += right_max - height[right]
                right -= 1
        return water
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Empty or tiny arrays trap no water.
- Water cannot be negative at an index.
- The lower side determines the safe side to process.

## Related
- Container With Most Water
- Largest Rectangle in Histogram
