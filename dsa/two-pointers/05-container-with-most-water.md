# 05. Container With Most Water

- **Difficulty:** Medium
- **Pattern:** Two Pointers
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given vertical line heights at consecutive x-positions, choose two lines that with the x-axis form a container holding the most water.

**Input**
- `height`: a list where `height[i]` is the height of the line at index `i`.

**Output**
- The maximum possible water area between two chosen lines.

## Constraints
- `2 <= height.length <= 10^5`
- `0 <= height[i] <= 10^4`

## Examples
```text
Input: height = [1,8,6,2,5,4,8,3,7]
Output: 49
Explanation: Choosing heights `8` at index `1` and `7` at index `8` gives width `7` and limiting height `7`, for area `49`.
```

## Understanding & Intuition
Area is limited by the shorter of two selected lines and their distance. Starting wide is promising, and after measuring a pair, moving the taller line cannot improve the limiting height. Therefore, move the shorter side inward.

## Approach 1 — Naive / Brute Force
**Idea:** Compute the area for every pair of lines.
```python
from typing import List

class Solution:
    def maxArea(self, height: List[int]) -> int:
        best = 0
        n = len(height)
        for i in range(n):
            for j in range(i + 1, n):
                area = min(height[i], height[j]) * (j - i)
                best = max(best, area)
        return best
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Brute force but skip right endpoints that are not taller than the best right height already tried for an anchor.
```python
from typing import List

class Solution:
    def maxArea(self, height: List[int]) -> int:
        best = 0
        n = len(height)
        for i in range(n):
            max_right_height = 0
            for j in range(n - 1, i, -1):
                if height[j] <= max_right_height:
                    continue
                max_right_height = height[j]
                best = max(best, min(height[i], height[j]) * (j - i))
        return best
```
- **Time:** O(n²) worst case — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Start with the widest container and always move the shorter wall inward.
```python
from typing import List

class Solution:
    def maxArea(self, height: List[int]) -> int:
        left, right = 0, len(height) - 1
        best = 0
        while left < right:
            best = max(best, min(height[left], height[right]) * (right - left))
            # The shorter side is the only side that can increase the limit.
            if height[left] < height[right]:
                left += 1
            else:
                right -= 1
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(1) |
| Better | O(n²) worst | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Width is `right - left`, not count of bars.
- Moving the taller pointer loses width without improving the bottleneck.
- Equal heights can move either side.

## Related
- Trapping Rain Water
- Best Time to Buy and Sell Stock
