# 08. Single Number III

- **Difficulty:** Medium
- **Pattern:** Bit Manipulation
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given an integer array `nums` where exactly two elements appear once and all other elements appear exactly twice, return the two single elements in any order. Constraints: `2 <= len(nums) <= 3 * 10^4`, `-2^31 <= nums[i] <= 2^31 - 1`.

## Examples
```text
Input: nums = [1,2,1,3,2,5]
Output: [3,5]
Explanation: 3 and 5 appear once; all other values appear twice.
```

## Understanding & Intuition
XOR of all numbers equals `a ^ b`, where `a` and `b` are the two unique values. Any set bit in `a ^ b` marks a position where they differ. Splitting numbers by that bit puts `a` and `b` in different groups, and duplicates still cancel inside each group.

## Approach 1 — Naive / Brute Force
**Idea:** For each value, scan the array to find values with count one.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> List[int]:
        ans = []
        for x in nums:
            count = 0
            for y in nums:
                if x == y:
                    count += 1
            if count == 1:
                ans.append(x)
        return ans
```
- **Time:** O(n^2) — **Space:** O(1) extra, excluding output

## Approach 2 — Better
**Idea:** Count frequencies with a hash map and return values with count one.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> List[int]:
        freq = {}
        for x in nums:
            freq[x] = freq.get(x, 0) + 1
        return [x for x, count in freq.items() if count == 1]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use `xor_all & -xor_all` to isolate a differing bit and partition the array.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> List[int]:
        xor_all = 0
        for x in nums:
            xor_all ^= x
        diff_bit = xor_all & -xor_all
        a = 0
        b = 0
        for x in nums:
            if x & diff_bit:
                a ^= x
            else:
                b ^= x
        return [a, b]
```
- **Time:** O(n) — **Space:** O(1) extra, excluding output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) extra |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) extra |

## Edge Cases & Pitfalls
- Return order is usually irrelevant; tests may compare as a set.
- The isolated differing bit is guaranteed non-zero because the two unique values differ.
- Negative numbers still partition correctly with `xor_all & -xor_all`.

## Related
- Single Number
- Single Number II
- Missing Number
