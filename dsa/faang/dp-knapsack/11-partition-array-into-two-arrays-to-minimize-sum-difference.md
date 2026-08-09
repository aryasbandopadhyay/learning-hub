# 11. Partition Array Into Two Arrays to Minimize Sum Difference

- **Difficulty:** Hard
- **Pattern:** cardinality-constrained subset DP
- **Asked at:** Google, Meta, Amazon

## Problem
Implement `minimumDifference` for **Partition Array Into Two Arrays to Minimize Sum Difference**. Given `2n` integers, split them into two arrays of length `n` and return the minimum absolute difference of their sums.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `nums`: list; input integer list.

**Output**
- A single integer.

## Constraints
- `1 <= n <= 15`, values may be negative

## Examples
```text
Input: nums = [3,9,7,3]
Output: 2
Explanation: Split as [3,9] and [7,3], with sums 12 and 10. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
The equal-size requirement adds a cardinality dimension to subset sum. Meet-in-the-middle enumerates sums grouped by how many values were chosen. Pairing `k` choices from the left with `n-k` from the right gives a valid half.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate choices of exactly half the indices.
```python
class Solution:
    def minimumDifference(self, nums: list[int]) -> int:
        total = sum(nums)
        need = len(nums) // 2
        best = 10**18
        def dfs(i: int, chosen: int, s: int) -> None:
            nonlocal best
            if chosen > need:
                return
            if i == len(nums):
                if chosen == need:
                    best = min(best, abs(total - 2 * s))
                return
            dfs(i + 1, chosen, s)
            dfs(i + 1, chosen + 1, s + nums[i])
        dfs(0, 0, 0)
        return best
```
- **Time:** O(2^(2n)) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build possible sums by chosen count using sets.
```python
class Solution:
    def minimumDifference(self, nums: list[int]) -> int:
        total = sum(nums)
        need = len(nums) // 2
        dp = [set() for _ in range(need + 1)]
        dp[0].add(0)
        for x in nums:
            for c in range(need - 1, -1, -1):
                for s in list(dp[c]):
                    dp[c + 1].add(s + x)
        return min(abs(total - 2 * s) for s in dp[need])
```
- **Time:** O(n * C(2n,n)) — **Space:** O(C(2n,n))

## Approach 3 — Optimal
**Idea:** Use meet-in-the-middle grouped by chosen count.
```python
class Solution:
    def minimumDifference(self, nums: list[int]) -> int:
        from bisect import bisect_left
        m = len(nums) // 2
        total = sum(nums)
        def grouped(arr: list[int]) -> list[list[int]]:
            groups = [[] for _ in range(len(arr) + 1)]
            def dfs(i: int, cnt: int, s: int) -> None:
                if i == len(arr):
                    groups[cnt].append(s)
                    return
                dfs(i + 1, cnt, s)
                dfs(i + 1, cnt + 1, s + arr[i])
            dfs(0, 0, 0)
            return groups
        left = grouped(nums[:m])
        right = grouped(nums[m:])
        for g in right:
            g.sort()
        ans = 10**18
        target = total / 2
        for k in range(m + 1):
            rg = right[m - k]
            for s in left[k]:
                i = bisect_left(rg, target - s)
                for j in (i - 1, i):
                    if 0 <= j < len(rg):
                        chosen = s + rg[j]
                        ans = min(ans, abs(total - 2 * chosen))
        return ans
```
- **Time:** O(n * 2^n) — **Space:** O(2^n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(2n)) | O(n) |
| Better | O(n * C(2n,n)) | O(C(2n,n)) |
| Optimal | O(n * 2^n) | O(2^n) |

## Edge Cases & Pitfalls
- Exactly half the elements must be chosen.
- Negative values make boolean sum arrays awkward.
- Group right-half sums by cardinality before binary search.

## Related
- Closest Subsequence Sum
- Last Stone Weight II
