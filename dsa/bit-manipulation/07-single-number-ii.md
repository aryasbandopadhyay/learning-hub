# 07. Single Number II

- **Difficulty:** Medium
- **Pattern:** Bit Manipulation
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
Given an integer array `nums` where every element appears exactly three times except for one element that appears exactly once, return the single element. Constraints: `1 <= len(nums) <= 3 * 10^4`, `-2^31 <= nums[i] <= 2^31 - 1`.

## Examples
```text
Input: nums = [2,2,3,2]
Output: 3
Explanation: 2 appears three times, while 3 appears once.
```

## Understanding & Intuition
For each bit position, triplicate numbers contribute counts divisible by three. Taking each bit count modulo three leaves the unique number's bits. A finite-state bitmask version tracks bits seen once and twice.

## Approach 1 — Naive / Brute Force
**Idea:** Count occurrences using nested scans.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> int:
        for x in nums:
            count = 0
            for y in nums:
                if x == y:
                    count += 1
            if count == 1:
                return x
        return 0
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Count each of the 32 bit positions modulo three.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> int:
        result = 0
        for i in range(32):
            bit_sum = 0
            for x in nums:
                bit_sum += (x >> i) & 1
            if bit_sum % 3:
                result |= 1 << i
        # Convert from unsigned 32-bit to signed Python integer.
        return result if result <= 0x7FFFFFFF else result - 0x100000000
```
- **Time:** O(32n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use two masks, `ones` and `twos`, as a state machine modulo three.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> int:
        ones = 0
        twos = 0
        for x in nums:
            # Move bits through states: unseen -> ones -> twos -> unseen.
            ones = (ones ^ x) & ~twos
            twos = (twos ^ x) & ~ones
        return ones
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(32n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Negative unique values must be converted correctly in bit-count solutions.
- The state-machine approach works directly with Python's signed integers.
- Do not use XOR alone; triplicates do not cancel with plain XOR.

## Related
- Single Number
- Single Number III
- Number of 1 Bits
