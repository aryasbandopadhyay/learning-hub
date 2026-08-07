# 23. Minimum Absolute Difference

- **Difficulty:** Easy
- **Pattern:** Sorting
- **Asked at:** Salesforce, Amazon, Google

## Problem
Return all pairs with the minimum absolute difference, sorted by ascending pair values.

## Examples
```text
Input: arr = [4,2,1,3]
Output: [[1,2],[2,3],[3,4]]
Explanation: The minimum difference is 1.
```

## Understanding & Intuition
After sorting, the closest pair for any value must be adjacent, so only adjacent differences need checking.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every pair and keep the best differences.
```python
class Solution:
    def minimumAbsDifference(self, arr: list[int]) -> list[list[int]]:
        best = float("inf"); ans = []
        for i in range(len(arr)):
            for j in range(i + 1, len(arr)):
                diff = abs(arr[i] - arr[j]); pair = sorted([arr[i], arr[j]])
                if diff < best: best = diff; ans = [pair]
                elif diff == best: ans.append(pair)
        return sorted(ans)
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort and do one pass for the gap, another for pairs.
```python
class Solution:
    def minimumAbsDifference(self, arr: list[int]) -> list[list[int]]:
        arr.sort(); best = min(arr[i] - arr[i - 1] for i in range(1, len(arr)))
        return [[arr[i - 1], arr[i]] for i in range(1, len(arr)) if arr[i] - arr[i - 1] == best]
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort and collect best adjacent pairs in one scan.
```python
class Solution:
    def minimumAbsDifference(self, arr: list[int]) -> list[list[int]]:
        arr.sort(); best = float("inf"); ans = []
        for i in range(1, len(arr)):
            diff = arr[i] - arr[i - 1]
            if diff < best: best = diff; ans = [[arr[i - 1], arr[i]]]
            elif diff == best: ans.append([arr[i - 1], arr[i]])
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Only adjacent sorted values can be minimum pairs.
- Return pairs in ascending order.
- Input has at least two numbers.

## Related
- Maximum Product of Three Numbers
- Merge Intervals
