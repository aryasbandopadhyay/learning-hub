# 04. Remove K Digits

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
Given a non-negative integer string `num` and an integer `k`, remove exactly `k` digits so the remaining number is as small as possible. Return the answer without leading zeroes, or `"0"` if no digit remains.

Constraints: `1 <= len(num) <= 10^5`, `0 <= k <= len(num)`, and `num` contains only digits.

## Examples
```text
Input: num = "1432219", k = 3
Output: "1219"
Explanation: Removing 4, 3, and 2 gives the smallest possible number 1219.
```

## Understanding & Intuition
A larger digit before a smaller following digit should be removed if deletions remain. Maintaining an increasing stack applies that exchange repeatedly. After scanning, any remaining deletions must remove digits from the end.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly remove the first digit that is greater than its next digit; if none exists, remove the last digit.
```python
class Solution:
    def removeKdigits(self, num: str, k: int) -> str:
        digits = list(num)
        for _ in range(k):
            i = 0
            while i + 1 < len(digits) and digits[i] <= digits[i + 1]:
                i += 1
            if digits:
                digits.pop(i)
        ans = ''.join(digits).lstrip('0')
        return ans if ans else "0"
```
- **Time:** O(nk) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build the answer left to right, choosing the smallest feasible next digit from the remaining window.
```python
class Solution:
    def removeKdigits(self, num, k):
        keep = len(num) - k
        if keep <= 0:
            return "0"
        start = 0
        out = []
        for pos in range(keep):
            end = len(num) - (keep - pos) + 1
            best = start
            for i in range(start, end):
                if num[i] < num[best]:
                    best = i
                    if num[i] == '0':
                        break
            out.append(num[best])
            start = best + 1
        ans = ''.join(out).lstrip('0')
        return ans if ans else "0"
```
- **Time:** O(nk) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a monotonic increasing stack, popping larger previous digits while deletions remain.
```python
class Solution:
    def removeKdigits(self, num, k):
        stack = []
        for ch in num:
            while k and stack and stack[-1] > ch:
                stack.pop()
                k -= 1
            stack.append(ch)
        if k:
            stack = stack[:-k]
        ans = ''.join(stack).lstrip('0')
        return ans if ans else "0"
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nk) | O(n) |
| Better | O(nk) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- If `k == len(num)`, return `"0"`.
- Strip leading zeroes only after all removals.
- When digits are nondecreasing, removals come from the right end.

## Related
- Create Maximum Number
- Monotonic Stack
