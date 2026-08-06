# 10. Minimum Length After Deleting Similar Ends

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Facebook, Amazon, Google

## Problem
Given `s`, repeatedly delete a non-empty prefix and suffix made of the same character without overlap. Return the minimum remaining length.

Constraints: `0 <= len(s) <= 10^5`, `s` contains lowercase English letters.

## Examples
```text
Input: s = "cabaabac"
Output: 0
Explanation: Delete c/c, then a/a, then b/b.
```

## Understanding & Intuition
Only equal end characters can be removed. When they match, removing all equal runs at both ends is always safe. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def minimumLength(self, s: str) -> int:
        while len(s) > 1 and s[0] == s[-1]:
            ch=s[0]; l=0
            while l < len(s) and s[l] == ch: l += 1
            r=len(s)-1
            while r >= l and s[r] == ch: r -= 1
            s=s[l:r+1]
        return len(s)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def minimumLength(self, s: str) -> int:
        seen=[]; l,r=0,len(s)-1
        while l < r and s[l] == s[r]:
            ch=s[l]
            while l <= r and s[l] == ch: l += 1
            while l <= r and s[r] == ch: r -= 1
            seen.append((l,r))
        return max(0,r-l+1)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def minimumLength(self, s: str) -> int:
        l,r=0,len(s)-1
        while l < r and s[l] == s[r]:
            ch=s[l]
            while l <= r and s[l] == ch: l += 1
            while l <= r and s[r] == ch: r -= 1
        return max(0,r-l+1)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Do not let prefix and suffix overlap.
- All one-character strings reduce to length 1.
- A whole string of one repeated character reduces to 0.

## Related
- Valid Palindrome II
- Remove Duplicates from Sorted Array II
