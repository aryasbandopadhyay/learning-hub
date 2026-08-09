# 10. Get Equal Substrings Within Budget

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given two equal-length strings `s` and `t` and an integer `maxCost`, changing `s[i]` to `t[i]` costs `abs(ord(s[i]) - ord(t[i]))`. Return the maximum length of a contiguous substring of `s` that can be changed to match `t` with total cost at most `maxCost`.

Implement `Solution.equalSubstring` with the parameters below and return the requested value.

**Input**
- `s`: a `str`; the input string described above.
- `t`: a `str`; the target string described above.
- `maxCost`: a `int`; the maximum total conversion cost.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(s) == len(t) <= 100000`
- `0 <= maxCost <= 1000000`

## Examples
```text
Input: s = "abcd", t = "bcdf", maxCost = 3
Output: 3
Explanation: Changing `abc` to `bcd` costs 3.
```

## Understanding & Intuition
Each index has an independent nonnegative conversion cost. The task becomes finding the longest contiguous subarray with sum at most `maxCost`. A standard variable window works because all costs are nonnegative.

## Approach 1 — Naive / Brute Force
**Idea:** Try every substring and recompute its conversion cost.
```python
class Solution:
    def equalSubstring(self, s: str, t: str, maxCost: int) -> int:
        n = len(s)
        ans = 0
        for i in range(n):
            for j in range(i, n):
                cost = 0
                for p in range(i, j + 1):
                    cost += abs(ord(s[p]) - ord(t[p]))
                if cost <= maxCost:
                    ans = max(ans, j - i + 1)
        return ans
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Fix the left edge and accumulate cost while extending right.
```python
class Solution:
    def equalSubstring(self, s: str, t: str, maxCost: int) -> int:
        n = len(s)
        ans = 0
        for i in range(n):
            cost = 0
            for j in range(i, n):
                cost += abs(ord(s[j]) - ord(t[j]))
                if cost > maxCost:
                    break
                ans = max(ans, j - i + 1)
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Maintain a cost window and shrink until it fits the budget.
```python
class Solution:
    def equalSubstring(self, s: str, t: str, maxCost: int) -> int:
        left = 0
        cost = 0
        ans = 0
        for right in range(len(s)):
            cost += abs(ord(s[right]) - ord(t[right]))
            while cost > maxCost:
                cost -= abs(ord(s[left]) - ord(t[left]))
                left += 1
            ans = max(ans, right - left + 1)
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `maxCost = 0` still allows already-equal positions.
- This works because per-index costs are never negative.

## Related
- Minimum Size Subarray Sum
- Replace the Substring for Balanced String
