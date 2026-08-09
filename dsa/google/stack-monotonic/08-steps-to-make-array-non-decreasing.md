# 08. Steps to Make Array Non-Decreasing

- **Difficulty:** Hard
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
In one step, remove every `nums[i]` with `i > 0` and `nums[i - 1] > nums[i]` simultaneously. Return the number of steps needed until the array becomes non-decreasing.

Implement `Solution.totalSteps` with the parameters below and return the requested value.

**Input**
- `nums`: a `list[int]`; the input integer list described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(nums) <= 10^5`, `1 <= nums[i] <= 10^9`

## Examples
```text
Input: nums = [5,3,4,4,7,3,6,11,8,5,11]
Output: 3
Explanation: After three simultaneous removal rounds, the remaining array is non-decreasing.
```

## Understanding & Intuition
Elements are removed based on the nearest larger survivor to their left. Simulating rounds is intuitive but can repeatedly rescan survivors. A monotonic stack computes the exact round in which each element disappears.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly simulate the simultaneous deletion rule.
```python
from typing import List

class Solution:
    def totalSteps(self, nums: List[int]) -> int:
        arr = nums[:]
        steps = 0
        while True:
            removed = False
            next_arr = [arr[0]] if arr else []
            for i in range(1, len(arr)):
                if arr[i - 1] > arr[i]:
                    removed = True
                else:
                    next_arr.append(arr[i])
            if not removed:
                return steps
            arr = next_arr
            steps += 1
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack of indexes and a DP array storing each element's deletion round.
```python
from typing import List

class Solution:
    def totalSteps(self, nums: List[int]) -> int:
        dp = [0] * len(nums)
        stack = []
        answer = 0
        for i, value in enumerate(nums):
            days = 0
            while stack and nums[stack[-1]] <= value:
                days = max(days, dp[stack.pop()])
            if stack:
                dp[i] = days + 1
            answer = max(answer, dp[i])
            stack.append(i)
        return answer
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store `(value, deletion_round)` pairs directly in a decreasing stack.
```python
from typing import List

class Solution:
    def totalSteps(self, nums: List[int]) -> int:
        stack = []
        answer = 0
        for value in nums:
            days = 0
            while stack and stack[-1][0] <= value:
                days = max(days, stack.pop()[1])
            days = 0 if not stack else days + 1
            answer = max(answer, days)
            stack.append((value, days))
        return answer
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Already non-decreasing arrays return `0`.
- Deletions in a round are simultaneous.
- Equal adjacent values are not removed.

## Related
- 132 Pattern
- Monotonic Stack
