# 09. Degree of an Array

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Google, Amazon, Microsoft

## Problem
The degree of an array is the maximum frequency of any element in it. Given a non-empty integer array `nums`, return the length of the shortest contiguous subarray that has the same degree as `nums`.

**Input**
- `nums`: a `list[int]`; the input integer list.

**Output**
- A `int`. Return the length of the shortest contiguous subarray that has the same degree as `nums`.

## Constraints
- `1 <= len(nums) <= 50_000`, and values fit in signed 32-bit integers.

## Examples
```text
Input: nums = [1, 2, 2, 3, 1, 4, 2]
Output: 6
Explanation: The array degree is 3 for value 2, and the shortest subarray with three 2s is [2,2,3,1,4,2].
```

## Understanding & Intuition
For a value to preserve the global degree, a subarray must include all occurrences from its first to last global position. Therefore, each candidate length is `last[value] - first[value] + 1` among values whose count equals the degree. Hashmaps track these statistics in one pass.

## Approach 1 — Naive / Brute Force
**Idea:** Compute the array degree, then test every subarray until finding the shortest one with that degree.
```python
class Solution:
    def findShortestSubArray(self, nums: list[int]) -> int:
        freq = {}
        for x in nums:
            freq[x] = freq.get(x, 0) + 1
        degree = max(freq.values())
        best = len(nums)
        for i in range(len(nums)):
            counts = {}
            cur_degree = 0
            for j in range(i, len(nums)):
                counts[nums[j]] = counts.get(nums[j], 0) + 1
                cur_degree = max(cur_degree, counts[nums[j]])
                if cur_degree == degree:
                    best = min(best, j - i + 1)
                    break
        return best
```
- **Time:** O(n²) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Store every index for each value, then inspect values whose positions list has maximum length.
```python
class Solution:
    def findShortestSubArray(self, nums):
        positions = {}
        for i, x in enumerate(nums):
            positions.setdefault(x, []).append(i)
        degree = max(len(v) for v in positions.values())
        best = len(nums)
        for pos in positions.values():
            if len(pos) == degree:
                best = min(best, pos[-1] - pos[0] + 1)
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track first occurrence, count, current degree, and best length during a single scan.
```python
class Solution:
    def findShortestSubArray(self, nums):
        first = {}
        count = {}
        degree = 0
        best = 0
        for i, x in enumerate(nums):
            if x not in first:
                first[x] = i
            count[x] = count.get(x, 0) + 1
            length = i - first[x] + 1
            if count[x] > degree:
                degree = count[x]
                best = length
            elif count[x] == degree:
                best = min(best, length)
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A single-element array has answer 1.
- Multiple values can share the degree; choose the shortest span.
- The shortest valid subarray for a value must include its first and last occurrence.

## Related
- Maximum Frequency Stack
- Top K Frequent Elements
