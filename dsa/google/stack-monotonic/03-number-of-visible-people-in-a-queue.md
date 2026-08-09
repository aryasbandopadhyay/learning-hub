# 03. Number of Visible People in a Queue

- **Difficulty:** Hard
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
There are people in a queue with distinct `heights`. Person `i` can see person `j > i` if every person between them is shorter than both `heights[i]` and `heights[j]`. Return how many people each person can see to their right.

Implement `Solution.canSeePersonsCount` with the parameters below and return the requested value.

**Input**
- `heights`: a `list[int]`; the height values described above.

**Output**
- A `list[int]` value representing the result described above. **This judge compares exactly**, so preserve the order required by the statement.

## Constraints
- `1 <= len(heights) <= 10^5`, `1 <= heights[i] <= 10^5`

## Examples
```text
Input: heights = [10,6,8,5,11,9]
Output: [3,1,2,1,1,0]
Explanation: The first person sees heights 6, 8, and 11; height 11 blocks everyone after it. The result is shown in the required order.
```

## Understanding & Intuition
A person sees shorter visible people until a taller blocker appears. From the right, a decreasing stack represents the visible skyline. Popping shorter people counts them, and one remaining taller person is also visible as the blocker.

## Approach 1 — Naive / Brute Force
**Idea:** Check every pair and verify all people between them are short enough.
```python
from typing import List

class Solution:
    def canSeePersonsCount(self, heights: List[int]) -> List[int]:
        n = len(heights)
        ans = [0] * n
        for i in range(n):
            for j in range(i + 1, n):
                limit = min(heights[i], heights[j])
                visible = True
                for k in range(i + 1, j):
                    if heights[k] >= limit:
                        visible = False
                        break
                if visible:
                    ans[i] += 1
        return ans
```
- **Time:** O(n^3) — **Space:** O(1) excluding output

## Approach 2 — Better
**Idea:** Scan right while tracking the tallest intermediate person, stopping after the first blocker.
```python
from typing import List

class Solution:
    def canSeePersonsCount(self, heights: List[int]) -> List[int]:
        n = len(heights)
        ans = [0] * n
        for i in range(n):
            tallest_between = 0
            for j in range(i + 1, n):
                if heights[j] > tallest_between:
                    ans[i] += 1
                tallest_between = max(tallest_between, heights[j])
                if heights[j] >= heights[i]:
                    break
        return ans
```
- **Time:** O(n^2) — **Space:** O(1) excluding output

## Approach 3 — Optimal
**Idea:** Process from right to left with a decreasing stack of visible blockers.
```python
from typing import List

class Solution:
    def canSeePersonsCount(self, heights: List[int]) -> List[int]:
        ans = [0] * len(heights)
        stack = []
        for i in range(len(heights) - 1, -1, -1):
            while stack and stack[-1] < heights[i]:
                stack.pop()
                ans[i] += 1
            if stack:
                ans[i] += 1
            stack.append(heights[i])
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The last person sees nobody.
- Count the first taller blocker if it exists.
- Do not count people hidden behind a blocker.

## Related
- Buildings With an Ocean View
- Largest Rectangle in Histogram
