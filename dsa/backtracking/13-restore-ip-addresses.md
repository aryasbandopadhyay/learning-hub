# 13. Restore IP Addresses

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a string `s` containing only digits, return all possible valid IP addresses by inserting three dots. A valid IP address has exactly four integers from `0` to `255`, and no segment may have leading zeros unless it is exactly `"0"`. `1 <= len(s) <= 20`.

## Examples
```text
Input: s = "25525511135"
Output: ["255.255.11.135","255.255.111.35"]
Explanation: These are the valid four-segment splits.
```

## Understanding & Intuition
Restoring an IP address is choosing four segment lengths. Each segment length is 1 to 3, so the branching factor is small. Length bounds and segment validity prune the recursion.

## Approach 1 — Naive / Brute Force
**Idea:** Try every triple of dot positions and validate the resulting four pieces.
```python
from typing import List

class Solution:
    def restoreIpAddresses(self, s: str) -> List[str]:
        result = []

        def valid(part: str) -> bool:
            return bool(part) and (part == "0" or part[0] != "0") and int(part) <= 255

        n = len(s)
        for i in range(1, n):
            for j in range(i + 1, n):
                for k in range(j + 1, n):
                    parts = [s[:i], s[i:j], s[j:k], s[k:]]
                    if all(valid(part) for part in parts):
                        result.append(".".join(parts))
        return result
```
- **Time:** O(n^3) — **Space:** O(1) auxiliary plus output

## Approach 2 — Better
**Idea:** Backtrack through four segments, trying lengths 1 through 3 and validating each segment.
```python
from typing import List

class Solution:
    def restoreIpAddresses(self, s: str) -> List[str]:
        result = []
        path = []

        def valid(part: str) -> bool:
            if len(part) > 1 and part[0] == "0":
                return False
            return int(part) <= 255

        def backtrack(index: int) -> None:
            if len(path) == 4:
                if index == len(s):
                    result.append(".".join(path))
                return
            for length in range(1, 4):
                if index + length > len(s):
                    break
                part = s[index:index + length]
                if valid(part):
                    path.append(part)
                    backtrack(index + length)
                    path.pop()

        backtrack(0)
        return result
```
- **Time:** O(3^4) — **Space:** O(1) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Add remaining-length pruning so recursion only continues when the rest of the string can fill the remaining segments.
```python
from typing import List

class Solution:
    def restoreIpAddresses(self, s: str) -> List[str]:
        result = []
        path = []

        def backtrack(index: int) -> None:
            remaining_segments = 4 - len(path)
            remaining_chars = len(s) - index
            if remaining_chars < remaining_segments or remaining_chars > 3 * remaining_segments:
                return
            if len(path) == 4:
                result.append(".".join(path))
                return
            for length in range(1, 4):
                if index + length > len(s):
                    break
                part = s[index:index + length]
                if (len(part) > 1 and part[0] == "0") or int(part) > 255:
                    continue
                path.append(part)
                backtrack(index + length)
                path.pop()

        backtrack(0)
        return result
```
- **Time:** O(3^4) — **Space:** O(1) auxiliary plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) plus output |
| Better | O(3^4) | O(1) plus output |
| Optimal | O(3^4) | O(1) plus output |

## Edge Cases & Pitfalls
- Reject leading zeros like `"01"`.
- Reject segments greater than `255`.
- The final answer must use every digit and exactly four segments.

## Related
- Palindrome Partitioning
- Generate Parentheses
- Combination Sum
