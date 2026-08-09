# 10. Minimum Number of Increments to Form Target Array

- **Difficulty:** Hard
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Bloomberg

## Problem
You start with an array of zeros with the same length as `target`.

In one operation, choose any contiguous subarray and increase every value in it by `1`. Return the minimum number of operations needed to create `target`.

**Input**
- `target`: a list of positive integers.

**Output**
- The minimum number of range-increment operations.

## Constraints
- `1 <= target.length <= 10^5`
- `1 <= target[i] <= 10^5`

## Examples
```text
Input: target = [1,2,3,2,1]
Output: 3
Explanation: Raise the whole array once, the middle four positions once, and the center position once, forming the target in `3` operations.
```

## Understanding & Intuition
Every time the desired height rises above the previous height, new increment layers must start. Drops do not cost immediately because open layers can simply stop.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly subtract one from every positive segment, counting how many layers are removed.
```python
class Solution:
    def minNumberOperations(self, target: list[int]) -> int:
        arr = target[:]
        ans = 0
        while any(x > 0 for x in arr):
            i = 0
            while i < len(arr):
                if arr[i] == 0:
                    i += 1
                    continue
                ans += 1
                while i < len(arr) and arr[i] > 0:
                    arr[i] -= 1
                    i += 1
        return ans
```
- **Time:** O(n * max(target)) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Divide and conquer: for a segment, raise it from a base to its minimum, then solve pieces above that minimum.
```python
class Solution:
    def minNumberOperations(self, target: list[int]) -> int:
        def solve(l: int, r: int, base: int) -> int:
            if l > r:
                return 0
            m = min(target[l:r + 1])
            cost = m - base
            i = l
            while i <= r:
                if target[i] == m:
                    i += 1
                else:
                    j = i
                    while j <= r and target[j] > m:
                        j += 1
                    cost += solve(i, j - 1, m)
                    i = j
            return cost
        return solve(0, len(target) - 1, 0)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sum only the positive rises between adjacent desired heights.
```python
class Solution:
    def minNumberOperations(self, target: list[int]) -> int:
        ans = target[0]
        for i in range(1, len(target)):
            if target[i] > target[i - 1]:
                ans += target[i] - target[i - 1]
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nM) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Count rises, not absolute differences.
- The first element contributes its full height above zero.

## Related
- Partition Array for Maximum Sum
- Minimum Path Sum
