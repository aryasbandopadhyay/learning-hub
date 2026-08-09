# 13. Buildings With an Ocean View

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
Buildings stand in a row, and the ocean is to the right. A building has an ocean view if every building to its right is shorter. Return the indices of all buildings with an ocean view in increasing order.

Implement `Solution.findBuildings` with the parameters below and return the requested value.

**Input**
- `heights`: a `list[int]`; the height values described above.

**Output**
- A `list[int]` value representing the result described above. **This judge compares exactly**, so preserve the order required by the statement.

## Constraints
- `1 <= len(heights) <= 10^5`, `1 <= heights[i] <= 10^9`

## Examples
```text
Input: heights = [4,2,3,1]
Output: [0,2,3]
Explanation: Buildings 0, 2, and 3 are taller than every building to their right. The result is shown in the required order.
```

## Understanding & Intuition
A building is visible exactly when its height is greater than the suffix maximum to its right. Computing that suffix maximum directly works. Scanning from the right keeps only the current maximum, the monotonic skyline blocking ocean views.

## Approach 1 — Naive / Brute Force
**Idea:** For each building, scan all buildings to its right.
```python
from typing import List

class Solution:
    def findBuildings(self, heights: List[int]) -> List[int]:
        ans = []
        for i in range(len(heights)):
            visible = True
            for j in range(i + 1, len(heights)):
                if heights[j] >= heights[i]:
                    visible = False
                    break
            if visible:
                ans.append(i)
        return ans
```
- **Time:** O(n^2) — **Space:** O(1) excluding output

## Approach 2 — Better
**Idea:** Precompute the maximum height strictly to the right of each index.
```python
from typing import List

class Solution:
    def findBuildings(self, heights: List[int]) -> List[int]:
        n = len(heights)
        suffix_max = [0] * n
        best_right = 0
        for i in range(n - 1, -1, -1):
            suffix_max[i] = best_right
            best_right = max(best_right, heights[i])
        return [i for i, height in enumerate(heights) if height > suffix_max[i]]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan from right to left, appending buildings that exceed the current skyline maximum.
```python
from typing import List

class Solution:
    def findBuildings(self, heights: List[int]) -> List[int]:
        ans = []
        max_right = 0
        for i in range(len(heights) - 1, -1, -1):
            if heights[i] > max_right:
                ans.append(i)
                max_right = heights[i]
        ans.reverse()
        return ans
```
- **Time:** O(n) — **Space:** O(1) excluding output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The rightmost building always has an ocean view.
- Equal-height buildings block the view.
- Return indices in increasing order.

## Related
- Number of Visible People in a Queue
- Suffix Maximum
