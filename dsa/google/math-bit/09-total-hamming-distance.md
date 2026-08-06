# 09. Total Hamming Distance

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given a list of integers `nums`, return the sum of Hamming distances between every pair of distinct elements. The Hamming distance between two integers is the number of bit positions where they differ. Constraints: `1 <= len(nums) <= 10^4` and `0 <= nums[i] <= 10^9`.

## Examples
```text
Input: nums = [4, 14, 2]
Output: 6
Explanation: Distances are 2 for (4,14), 2 for (4,2), and 2 for (14,2).
```

## Understanding & Intuition
Hamming distance adds independently over bit positions. At one bit, each zero-one pair contributes exactly one to the total. Counting ones and zeros per bit avoids enumerating all pairs.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every pair and count differing bits with XOR.
```python
class Solution:
    def totalHammingDistance(self, nums: list[int]) -> int:
        ans = 0
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                ans += (nums[i] ^ nums[j]).bit_count()
        return ans
```
- **Time:** O(n^2 log M) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Scan only up to the maximum value's bit length and count ones column by column.
```python
class Solution:
    def totalHammingDistance(self, nums: list[int]) -> int:
        width = max(1, max(nums).bit_length())
        ans = 0
        for bit in range(width):
            ones = 0
            for x in nums:
                ones += (x >> bit) & 1
            ans += ones * (len(nums) - ones)
        return ans
```
- **Time:** O(n log M) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Since values are at most `10^9`, scan the 30 relevant bit positions and sum zero-one pair counts.
```python
class Solution:
    def totalHammingDistance(self, nums: list[int]) -> int:
        n = len(nums)
        ans = 0
        for bit in range(30):
            ones = sum((x >> bit) & 1 for x in nums)
            ans += ones * (n - ones)
        return ans
```
- **Time:** O(30n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log M) | O(1) |
| Better | O(n log M) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Each unordered pair should be counted once.
- Bits with all zeros or all ones contribute zero.
- Include enough bit positions for the maximum allowed value.

## Related
- Counting Bits
- Maximum XOR of Two Numbers in an Array
