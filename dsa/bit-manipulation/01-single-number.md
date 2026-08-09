# 01. Single Number

- **Difficulty:** Easy
- **Pattern:** Bit Manipulation
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
Given a non-empty integer list `nums`, every value appears exactly twice except for one value that appears exactly once. Find that single value.

**Input**
- `nums`: a list of integers containing pairs plus one unpaired value.

**Output**
- The integer that appears exactly once.

## Constraints
- `1 <= nums.length <= 3 * 10^4`
- `nums.length` is odd.
- `-3 * 10^4 <= nums[i] <= 3 * 10^4`
- Exactly one value appears once; every other value appears exactly twice.

## Examples
```text
Input: nums = [4,1,2,1,2]
Output: 4
Explanation: The values `1` and `2` each occur twice and cancel as pairs. The only value with one occurrence is `4`.
```

```text
Input: nums = [1]
Output: 1
Explanation: With only one element, that element is the single number.
```

## Understanding & Intuition
XOR is ideal because `x ^ x = 0` and `x ^ 0 = x`. Since XOR is commutative and associative, all duplicate pairs cancel regardless of order. The remaining value is the unique number.

## Approach 1 — Naive / Brute Force
**Idea:** For each number, count its occurrences by scanning the whole array.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> int:
        # Try each candidate and count by a full scan.
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
**Idea:** Count frequencies with a hash map and return the value with frequency one.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> int:
        freq = {}
        for x in nums:
            freq[x] = freq.get(x, 0) + 1
        for x, count in freq.items():
            if count == 1:
                return x
        return 0
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** XOR all numbers so duplicate pairs cancel out.
```python
from typing import List

class Solution:
    def singleNumber(self, nums: List[int]) -> int:
        ans = 0
        for x in nums:
            ans ^= x
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A length-one array should return its only element.
- XOR works with negative integers in Python for this duplicate-canceling use case.
- Do not sort if the input order must be preserved elsewhere.

## Related
- Missing Number
- Single Number II
- Single Number III
