# 06. Partition Labels

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Amazon, Meta, Google, Microsoft

## Problem
Given a string `s`, partition it into as many parts as possible so each letter appears in at most one part. Return a list of the sizes of these parts. Constraints: `1 <= len(s) <= 500`, `s` contains lowercase English letters.

## Examples
```text
Input: s = "ababcbacadefegdehijhklij"
Output: [9,7,8]
Explanation: The partitions are "ababcbaca", "defegde", and "hijhklij".
```

## Understanding & Intuition
For a partition to be valid, it must include the last occurrence of every character it contains. Greedily extend the current partition to the farthest last occurrence seen. When the scan reaches that boundary, closing the partition is safe and maximizes the number of parts.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible cut recursively and validate that no character crosses partitions.
```python
from typing import List

class Solution:
    def partitionLabels(self, s: str) -> List[int]:
        n = len(s)

        def valid(parts: List[str]) -> bool:
            owner = {}
            for idx, part in enumerate(parts):
                for ch in part:
                    if ch in owner and owner[ch] != idx:
                        return False
                    owner[ch] = idx
            return True

        best: List[int] = []

        def dfs(start: int, parts: List[str]) -> None:
            nonlocal best
            if start == n:
                if valid(parts) and len(parts) > len(best):
                    best = [len(part) for part in parts]
                return
            for end in range(start + 1, n + 1):
                dfs(end, parts + [s[start:end]])

        dfs(0, [])
        return best
```
- **Time:** O(2^n * n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Precompute last occurrences and test each candidate partition endpoint.
```python
from typing import List

class Solution:
    def partitionLabels(self, s: str) -> List[int]:
        last = {ch: i for i, ch in enumerate(s)}
        ans = []
        start = 0

        while start < len(s):
            end = last[s[start]]
            i = start
            while i <= end:
                end = max(end, last[s[i]])
                i += 1
            ans.append(end - start + 1)
            start = end + 1
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Single scan with the current partition boundary.
```python
from typing import List

class Solution:
    def partitionLabels(self, s: str) -> List[int]:
        last = {ch: i for i, ch in enumerate(s)}
        result = []
        start = 0
        end = 0

        for i, ch in enumerate(s):
            end = max(end, last[ch])
            if i == end:
                result.append(end - start + 1)
                start = i + 1
        return result
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n * n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A character appearing once can form a one-character partition.
- Use last occurrence, not first occurrence.
- Space is O(1) because the alphabet has only 26 lowercase letters.

## Related
- Merge Intervals
- Greedy Interval Boundaries
