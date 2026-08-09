# 06. Minimum Cost Tree From Leaf Values

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
Given an array `arr` of positive leaf values, build a binary tree whose in-order leaves are exactly `arr`. Each non-leaf node has value equal to the product of the largest leaf in its left and right subtrees. Return the minimum possible sum of non-leaf node values.

Implement `Solution.mctFromLeafValues` with the parameters below and return the requested value.

**Input**
- `arr`: a `list[int]`; leaf values in inorder order.

**Output**
- A `int` value representing the result described above.

## Constraints
- `2 <= len(arr) <= 40`, `1 <= arr[i] <= 15`

## Examples
```text
Input: arr = [6,2,4]
Output: 32
Explanation: Combining 2 and 4 first costs 8, then combining with 6 costs 24.
```

## Understanding & Intuition
Every internal cost is determined by maximum leaves on the two sides of a split. Interval DP tries all splits. The stack solution greedily pairs each leaf with the smaller of its nearest greater neighbors.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try every root split and compute interval maxima directly.
```python
from typing import List

class Solution:
    def mctFromLeafValues(self, arr: List[int]) -> int:
        def solve(left: int, right: int) -> int:
            if left == right:
                return 0
            best = float('inf')
            for mid in range(left, right):
                cost = solve(left, mid) + solve(mid + 1, right)
                cost += max(arr[left:mid + 1]) * max(arr[mid + 1:right + 1])
                best = min(best, cost)
            return best
        return solve(0, len(arr) - 1)
```
- **Time:** O(2^n n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use interval DP with precomputed maximum leaf values.
```python
from typing import List

class Solution:
    def mctFromLeafValues(self, arr: List[int]) -> int:
        n = len(arr)
        max_leaf = [[0] * n for _ in range(n)]
        for i in range(n):
            max_leaf[i][i] = arr[i]
            for j in range(i + 1, n):
                max_leaf[i][j] = max(max_leaf[i][j - 1], arr[j])
        dp = [[0] * n for _ in range(n)]
        for length in range(2, n + 1):
            for left in range(n - length + 1):
                right = left + length - 1
                dp[left][right] = min(
                    dp[left][mid] + dp[mid + 1][right] + max_leaf[left][mid] * max_leaf[mid + 1][right]
                    for mid in range(left, right)
                )
        return dp[0][n - 1]
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Use an increasing stack; when a smaller leaf is trapped, multiply it by its cheaper greater neighbor.
```python
from typing import List

class Solution:
    def mctFromLeafValues(self, arr: List[int]) -> int:
        stack = [float('inf')]
        total = 0
        for value in arr:
            while stack[-1] <= value:
                mid = stack.pop()
                total += mid * min(stack[-1], value)
            stack.append(value)
        while len(stack) > 2:
            total += stack.pop() * stack[-1]
        return total
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n n) | O(n) |
| Better | O(n^3) | O(n^2) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Leaf order must remain unchanged.
- All values are positive.
- The infinity sentinel simplifies boundary handling.

## Related
- Burst Balloons
- Matrix Chain Multiplication
