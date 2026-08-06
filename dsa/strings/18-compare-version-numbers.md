# 18. Compare Version Numbers

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Amazon, Microsoft, Google, Apple

## Problem
Given two version strings `version1` and `version2`, compare them. Revisions are separated by dots, leading zeros are ignored, and missing revisions are treated as `0`. Return `1` if `version1 > version2`, `-1` if `version1 < version2`, otherwise `0`. Constraints: `1 <= len(version1), len(version2) <= 500`.

## Examples
```text
Input: version1 = "1.01", version2 = "1.001"
Output: 0
Explanation: Both second revisions equal 1 after ignoring leading zeros.
```

## Understanding & Intuition
Versions compare revision by revision, not lexicographically as raw strings. Leading zeros do not matter. Missing components behave like zeros.

## Approach 1 — Naive / Brute Force
**Idea:** Split both versions, convert every revision to an integer, pad shorter list with zeros, and compare.
```python
class Solution:
    def compareVersion(self, version1: str, version2: str) -> int:
        parts1 = [int(part) for part in version1.split('.')]
        parts2 = [int(part) for part in version2.split('.')]
        length = max(len(parts1), len(parts2))
        parts1 += [0] * (length - len(parts1))
        parts2 += [0] * (length - len(parts2))
        if parts1 > parts2:
            return 1
        if parts1 < parts2:
            return -1
        return 0
```
- **Time:** O(n + m) — **Space:** O(n + m)

## Approach 2 — Better
**Idea:** Split but compare components with `zip_longest` to avoid explicit padding.
```python
from itertools import zip_longest

class Solution:
    def compareVersion(self, version1: str, version2: str) -> int:
        for a, b in zip_longest(version1.split('.'), version2.split('.'), fillvalue='0'):
            x, y = int(a), int(b)
            if x > y:
                return 1
            if x < y:
                return -1
        return 0
```
- **Time:** O(n + m) — **Space:** O(n + m)

## Approach 3 — Optimal
**Idea:** Parse one numeric revision at a time with two pointers and compare immediately.
```python
class Solution:
    def compareVersion(self, version1: str, version2: str) -> int:
        i = j = 0
        n, m = len(version1), len(version2)
        while i < n or j < m:
            x = 0
            while i < n and version1[i] != '.':
                x = x * 10 + ord(version1[i]) - ord('0')
                i += 1
            y = 0
            while j < m and version2[j] != '.':
                y = y * 10 + ord(version2[j]) - ord('0')
                j += 1
            if x > y:
                return 1
            if x < y:
                return -1
            i += 1  # Skip dot or harmlessly move past end.
            j += 1
        return 0
```
- **Time:** O(n + m) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n + m) | O(n + m) |
| Better | O(n + m) | O(n + m) |
| Optimal | O(n + m) | O(1) |

## Edge Cases & Pitfalls
- `"1.0"` equals `"1"`.
- Compare revisions numerically, not lexicographically.
- Leading zeros should not affect value.

## Related
- String Parsing
- Semantic Versioning
- Two Pointers
