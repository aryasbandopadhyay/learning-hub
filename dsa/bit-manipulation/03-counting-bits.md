# 03. Counting Bits

- **Difficulty:** Easy
- **Pattern:** Bit Manipulation
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given an integer `n`, compute the number of `1` bits in every integer from `0` through `n`.

**Input**
- `n`: a non-negative integer upper bound.

**Output**
- A list `ans` of length `n + 1`, where `ans[i]` is the number of `1` bits in the binary representation of `i`. This judge compares exactly, so entries must be returned in increasing integer order from index `0` to index `n`.

## Constraints
- `0 <= n <= 10^5`

## Examples
```text
Input: n = 5
Output: [0,1,1,2,1,2]
Explanation: The numbers `0` through `5` are `0`, `1`, `10`, `11`, `100`, and `101` in binary, with set-bit counts `0,1,1,2,1,2` in that exact index order.
```

```text
Input: n = 0
Output: [0]
Explanation: The only required value is the bit count of `0`.
```

## Understanding & Intuition
Each number is closely related to a smaller number. The relation `bits[i] = bits[i >> 1] + (i & 1)` removes the last bit and adds whether that bit was one. This gives a simple dynamic program over all values from `0` to `n`.

## Approach 1 — Naive / Brute Force
**Idea:** For each number, convert to binary and count ones.
```python
from typing import List

class Solution:
    def countBits(self, n: int) -> List[int]:
        ans = []
        for x in range(n + 1):
            count = 0
            for ch in bin(x):
                if ch == "1":
                    count += 1
            ans.append(count)
        return ans
```
- **Time:** O(n log n) — **Space:** O(1) extra, excluding output

## Approach 2 — Better
**Idea:** Use Brian Kernighan's trick for each number.
```python
from typing import List

class Solution:
    def countBits(self, n: int) -> List[int]:
        ans = []
        for x in range(n + 1):
            count = 0
            y = x
            while y:
                y &= y - 1
                count += 1
            ans.append(count)
        return ans
```
- **Time:** O(total set bits) — **Space:** O(1) extra, excluding output

## Approach 3 — Optimal
**Idea:** Build counts from smaller values using `i >> 1`.
```python
from typing import List

class Solution:
    def countBits(self, n: int) -> List[int]:
        ans = [0] * (n + 1)
        for i in range(1, n + 1):
            # Drop the last bit, then add it back if it was 1.
            ans[i] = ans[i >> 1] + (i & 1)
        return ans
```
- **Time:** O(n) — **Space:** O(1) extra, excluding output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(1) extra |
| Better | O(total set bits) | O(1) extra |
| Optimal | O(n) | O(1) extra |

## Edge Cases & Pitfalls
- `n = 0` should return `[0]`.
- The output array itself is required and not counted as extra space.
- Avoid recomputing strings in the optimal DP.

## Related
- Number of 1 Bits
- Power of Two
- Single Number
