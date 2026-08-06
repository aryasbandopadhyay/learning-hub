# 12. Maximum XOR of Two Numbers in an Array

- **Difficulty:** Medium
- **Pattern:** Bit Manipulation
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given an integer array `nums`, return the maximum value of `nums[i] XOR nums[j]` for `0 <= i <= j < len(nums)`. Constraints: `1 <= len(nums) <= 2 * 10^5`, `0 <= nums[i] <= 2^31 - 1`.

## Examples
```text
Input: nums = [3,10,5,25,2,8]
Output: 28
Explanation: 5 XOR 25 = 28, which is the maximum possible XOR.
```

## Understanding & Intuition
To maximize XOR, prefer opposite bits from most significant to least significant. A trie stores previous numbers by bit and greedily walks the opposite branch when possible. A prefix-set method can also prove each candidate bit from high to low.

## Approach 1 — Naive / Brute Force
**Idea:** Test every pair and keep the largest XOR.
```python
from typing import List

class Solution:
    def findMaximumXOR(self, nums: List[int]) -> int:
        best = 0
        for i in range(len(nums)):
            for j in range(i, len(nums)):
                best = max(best, nums[i] ^ nums[j])
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build the answer one bit at a time using masked prefixes and a set.
```python
from typing import List

class Solution:
    def findMaximumXOR(self, nums: List[int]) -> int:
        best = 0
        mask = 0
        for bit in range(30, -1, -1):
            mask |= 1 << bit
            prefixes = {x & mask for x in nums}
            candidate = best | (1 << bit)
            for prefix in prefixes:
                if (prefix ^ candidate) in prefixes:
                    best = candidate
                    break
        return best
```
- **Time:** O(31n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Insert numbers into a bit trie and greedily choose opposite bits for each query.
```python
from typing import List

class Solution:
    def findMaximumXOR(self, nums: List[int]) -> int:
        trie = {}
        for x in nums:
            node = trie
            for bit in range(30, -1, -1):
                b = (x >> bit) & 1
                node = node.setdefault(b, {})

        best = 0
        for x in nums:
            node = trie
            current = 0
            for bit in range(30, -1, -1):
                b = (x >> bit) & 1
                want = 1 - b
                if want in node:
                    current |= 1 << bit
                    node = node[want]
                else:
                    node = node[b]
            best = max(best, current)
        return best
```
- **Time:** O(31n) — **Space:** O(31n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(31n) | O(n) |
| Optimal | O(31n) | O(31n) |

## Edge Cases & Pitfalls
- A single-element array returns `0` because `nums[i] XOR nums[i] = 0`.
- Iterate from the most significant bit to make greedy choices valid.
- Constraints are non-negative, so 31 bits cover `0` through `2^31 - 1`.

## Related
- Single Number III
- Counting Bits
- Bitwise AND of Numbers Range
