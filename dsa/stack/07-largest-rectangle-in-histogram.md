# 07. Largest Rectangle in Histogram

- **Difficulty:** Hard
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given bar heights in a histogram where each bar has width `1`, find the area of the largest rectangle that can be formed using contiguous bars.

**Input**
- `heights`: a list of non-negative bar heights.

**Output**
- The maximum rectangle area.

## Constraints
- `1 <= heights.length <= 10^5`
- `0 <= heights[i] <= 10^4`

## Examples
```text
Input: heights = [2,1,5,6,2,3]
Output: 10
Explanation: The largest rectangle uses the bars of heights `5` and `6` with limiting height `5` over width `2`, giving area `10`.
```

## Understanding & Intuition
For each bar, the largest rectangle using that height extends until a smaller bar appears on both sides. A monotonic increasing stack delays area calculation until the right boundary is known. Adding a sentinel height flushes remaining bars.

## Approach 1 — Naive / Brute Force
**Idea:** Try every subarray and maintain its minimum height.
```python
class Solution:
    def largestRectangleArea(self, heights: list[int]) -> int:
        best = 0
        n = len(heights)
        for i in range(n):
            min_h = heights[i]
            for j in range(i, n):
                min_h = min(min_h, heights[j])
                best = max(best, min_h * (j - i + 1))
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Precompute nearest smaller indices on the left and right, then evaluate each bar.
```python
class Solution:
    def largestRectangleArea(self, heights: list[int]) -> int:
        n = len(heights)
        left = [-1] * n
        right = [n] * n
        stack = []
        for i, h in enumerate(heights):
            while stack and heights[stack[-1]] >= h:
                stack.pop()
            left[i] = stack[-1] if stack else -1
            stack.append(i)
        stack = []
        for i in range(n - 1, -1, -1):
            while stack and heights[stack[-1]] >= heights[i]:
                stack.pop()
            right[i] = stack[-1] if stack else n
            stack.append(i)
        return max(heights[i] * (right[i] - left[i] - 1) for i in range(n))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use one increasing stack and compute areas as soon as a smaller right boundary appears.
```python
class Solution:
    def largestRectangleArea(self, heights: list[int]) -> int:
        best = 0
        stack = []  # Pairs of (start_index, height).
        for i, h in enumerate(heights + [0]):
            start = i
            while stack and stack[-1][1] > h:
                idx, height = stack.pop()
                best = max(best, height * (i - idx))
                start = idx
            stack.append((start, h))
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A sentinel `0` ensures remaining bars are processed.
- Use `>=` in boundary precomputation to handle duplicate heights consistently.
- Width is `right_smaller - left_smaller - 1`.

## Related
- Daily Temperatures
- Basic Calculator

