# 23. Third Maximum Number

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
Return the third distinct maximum value in `nums`; if fewer than three distinct values exist, return the maximum.

## Examples
```text
Input: nums = [3,2,1]
Output: 1
Explanation: The third distinct maximum is 1.
```

## Understanding & Intuition
Distinctness is the trap. Sorting a set is easy, while maintaining three distinct maxima does the same work in one pass.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan for the next lower maximum.
```python
class Solution:
    def thirdMax(self, nums: list[int]) -> int:
        first = max(nums)
        prev = None
        cur = first
        for _ in range(3):
            cand = None
            for x in nums:
                if (prev is None or x < prev) and (cand is None or x > cand):
                    cand = x
            if cand is None:
                return first
            cur = prev = cand
        return cur
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort distinct values.
```python
class Solution:
    def thirdMax(self, nums: list[int]) -> int:
        vals = sorted(set(nums))
        return vals[-3] if len(vals) >= 3 else vals[-1]
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track top three distinct values.
```python
class Solution:
    def thirdMax(self, nums: list[int]) -> int:
        a = b = c = None
        for x in nums:
            if x == a or x == b or x == c:
                continue
            if a is None or x > a:
                a, b, c = x, a, b
            elif b is None or x > b:
                b, c = x, b
            elif c is None or x > c:
                c = x
        return c if c is not None else a
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Third maximum is distinct.
- Use None sentinels.
- Fallback is maximum.

## Related
- Kth Largest Element in an Array
- Top K Frequent Elements
