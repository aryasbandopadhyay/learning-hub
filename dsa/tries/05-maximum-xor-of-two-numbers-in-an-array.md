# 05. Maximum XOR of Two Numbers in an Array

- **Difficulty:** Medium
- **Pattern:** Tries
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given a list of non-negative integers `nums`, find the maximum possible value of `nums[i] XOR nums[j]` over all pairs of indices.

**Input**
- `nums`: a list of non-negative integers.

**Output**
- The maximum bitwise XOR value obtainable from two array elements.

## Constraints
- `1 <= nums.length <= 2 * 10^5`
- `0 <= nums[i] <= 2^31 - 1`

## Examples
```text
Input: nums = [3,10,5,25,2,8]
Output: 28
Explanation: The pair `5` and `25` gives `5 XOR 25 = 28`, larger than any other pair.
```

## Understanding & Intuition
XOR is larger when high bits differ. A binary trie stores bits from most significant to least significant, so each number can greedily choose the opposite bit when possible. This creates the best partner for that number bit by bit.

## Approach 1 — Naive / Brute Force
**Idea:** Try every pair and keep the largest XOR.
```python
from typing import List


class Solution:
    def findMaximumXOR(self, nums: List[int]) -> int:
        best = 0
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                # XOR all pairs directly.
                best = max(best, nums[i] ^ nums[j])
        return best
```
- **Time:** O(N²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build the answer one bit at a time using prefixes and check whether a candidate XOR is possible.
```python
from typing import List


class Solution:
    def findMaximumXOR(self, nums: List[int]) -> int:
        best = 0
        mask = 0
        max_bit = max(nums).bit_length() - 1
        for bit in range(max_bit, -1, -1):
            mask |= 1 << bit
            prefixes = {num & mask for num in nums}
            candidate = best | (1 << bit)
            # If p ^ q = candidate, then q = p ^ candidate.
            if any((p ^ candidate) in prefixes for p in prefixes):
                best = candidate
        return best
```
- **Time:** O(BN) — **Space:** O(N)

## Approach 3 — Optimal
**Idea:** Insert each number into a binary trie and greedily walk opposite bits to maximize XOR.
```python
from typing import List


class TrieNode:
    def __init__(self):
        self.children = {}


class Solution:
    def findMaximumXOR(self, nums: List[int]) -> int:
        root = TrieNode()
        max_bit = max(nums).bit_length() - 1

        for num in nums:
            node = root
            for bit in range(max_bit, -1, -1):
                b = (num >> bit) & 1
                if b not in node.children:
                    node.children[b] = TrieNode()
                node = node.children[b]

        best = 0
        for num in nums:
            node = root
            value = 0
            for bit in range(max_bit, -1, -1):
                b = (num >> bit) & 1
                want = 1 - b
                if want in node.children:
                    value |= 1 << bit
                    node = node.children[want]
                else:
                    node = node.children[b]
            best = max(best, value)
        return best
```
- **Time:** O(BN) — **Space:** O(BN)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(N²) | O(1) |
| Better | O(BN) | O(N) |
| Optimal | O(BN) | O(BN) |

## Edge Cases & Pitfalls
- If all numbers are equal, the answer is `0`.
- Include the highest set bit of the maximum number.
- Binary tries use bits as branches, not characters.

## Related
- Maximum XOR With an Element From Array
- Bitwise prefix greedy problems
