# 01. Longest Substring with At Most Two Distinct Characters

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given a string `s`, return the length of the longest contiguous substring that contains at most two distinct characters.

Implement `Solution.lengthOfLongestSubstringTwoDistinct` with the parameters below and return the requested value.

**Input**
- `s`: a `str`; the input string described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- `0 <= len(s) <= 100000`
- `s` contains printable ASCII characters

## Examples
```text
Input: s = "eceba"
Output: 3
Explanation: "ece" has length 3 and contains only two distinct characters.
```

## Understanding & Intuition
A valid answer is always a contiguous segment. When a segment has too many distinct characters, removing characters from the left is the only way to make it valid again. The optimal solution keeps exactly that valid window online.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every substring and count its distinct characters from scratch.
```python
class Solution:
    def lengthOfLongestSubstringTwoDistinct(self, s: str) -> int:
        n = len(s)
        ans = 0
        for i in range(n):
            for j in range(i, n):
                seen = set()
                for k in range(i, j + 1):
                    seen.add(s[k])
                if len(seen) <= 2 and j - i + 1 > ans:
                    ans = j - i + 1
        return ans
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Fix the left edge and extend right while maintaining a set for that start.
```python
class Solution:
    def lengthOfLongestSubstringTwoDistinct(self, s: str) -> int:
        n = len(s)
        ans = 0
        for i in range(n):
            seen = set()
            for j in range(i, n):
                seen.add(s[j])
                if len(seen) > 2:
                    break
                ans = max(ans, j - i + 1)
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Maintain counts in a variable-size window and shrink while more than two characters appear.
```python
class Solution:
    def lengthOfLongestSubstringTwoDistinct(self, s: str) -> int:
        count = {}
        left = 0
        ans = 0
        for right, ch in enumerate(s):
            count[ch] = count.get(ch, 0) + 1
            while len(count) > 2:
                old = s[left]
                count[old] -= 1
                if count[old] == 0:
                    del count[old]
                left += 1
            ans = max(ans, right - left + 1)
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Empty strings return `0`.
- Do not shrink when there are exactly two distinct characters.

## Related
- Longest Substring with At Most K Distinct Characters
- Fruit Into Baskets
