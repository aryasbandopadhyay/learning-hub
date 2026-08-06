# 13. Partition Labels

- **Difficulty:** Medium
- **Pattern:** Two Pointers
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given a string `s`, partition it into as many parts as possible so each letter appears in at most one part. Return a list of partition sizes. Constraints: `1 <= len(s) <= 500`.

## Examples
```text
Input: s = "ababcbacadefegdehijhklij"
Output: [9,7,8]
Explanation: The partitions are "ababcbaca", "defegde", and "hijhklij".
```

## Understanding & Intuition
A partition cannot end before the last occurrence of any character already inside it. Track the farthest last occurrence seen so far; when the scan reaches it, a partition closes.

## Approach 1 — Naive / Brute Force
**Idea:** Grow a partition and repeatedly rescan it until every included character's last occurrence is covered.
```python
from typing import List

class Solution:
    def partitionLabels(self, s: str) -> List[int]:
        result = []
        start = 0
        while start < len(s):
            end = start
            changed = True
            while changed:
                changed = False
                for i in range(start, end + 1):
                    last = s.rfind(s[i])
                    if last > end:
                        end = last
                        changed = True
            result.append(end - start + 1)
            start = end + 1
        return result
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Precompute each character's last index, then expand partitions with a while loop.
```python
from typing import List

class Solution:
    def partitionLabels(self, s: str) -> List[int]:
        last = {ch: i for i, ch in enumerate(s)}
        result = []
        start = 0
        while start < len(s):
            end = last[s[start]]
            i = start
            while i <= end:
                end = max(end, last[s[i]])
                i += 1
            result.append(end - start + 1)
            start = end + 1
        return result
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Single pass after last-index preprocessing, closing a partition when the current index reaches the active boundary.
```python
from typing import List

class Solution:
    def partitionLabels(self, s: str) -> List[int]:
        last = {ch: i for i, ch in enumerate(s)}
        result = []
        start = end = 0
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
| Naive | O(n²) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A character's last occurrence can extend the current partition.
- Space is O(1) for lowercase English letters.
- Do not cut as soon as one character ends; all active characters must end.

## Related
- Merge Intervals
- Greedy String Partitioning
