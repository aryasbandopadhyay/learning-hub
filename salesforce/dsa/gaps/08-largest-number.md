# 08. Largest Number

- **Difficulty:** Medium
- **Pattern:** Sorting / Greedy
- **Asked at:** Salesforce, Amazon, Microsoft

## Problem
Arrange non-negative integers to form the largest possible number and return it as a string.

## Examples
```text
Input: nums = [3,30,34,5,9]
Output: "9534330"
Explanation: Concatenation order puts 9 before 5 before 34 before 3 before 30.
```

## Understanding & Intuition
For strings `a` and `b`, put `a` first when `a+b` is larger than `b+a`. This comparator captures the only ordering that matters.

## Approach 1 — Naive / Brute Force
**Idea:** Try all permutations.
```python
from itertools import permutations
class Solution:
    def largestNumber(self, nums: list[int]) -> str:
        best = max("".join(p) for p in permutations(map(str, nums)))
        return "0" if best[0] == "0" else best
```
- **Time:** O(n! * n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Insertion sort using the concatenation comparator.
```python
class Solution:
    def largestNumber(self, nums: list[int]) -> str:
        arr = list(map(str, nums))
        for i in range(1, len(arr)):
            cur = arr[i]; j = i - 1
            while j >= 0 and cur + arr[j] > arr[j] + cur:
                arr[j + 1] = arr[j]; j -= 1
            arr[j + 1] = cur
        ans = "".join(arr)
        return "0" if ans[0] == "0" else ans
```
- **Time:** O(n^2 * m) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort with a custom comparator based on `a+b` versus `b+a`.
```python
from functools import cmp_to_key
class Solution:
    def largestNumber(self, nums: list[int]) -> str:
        def cmp(a: str, b: str) -> int:
            return -1 if a + b > b + a else (1 if a + b < b + a else 0)
        ans = "".join(sorted(map(str, nums), key=cmp_to_key(cmp)))
        return "0" if ans[0] == "0" else ans
```
- **Time:** O(n log n * m) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n! * n) | O(n) |
| Better | O(n^2 * m) | O(n) |
| Optimal | O(n log n * m) | O(n) |

## Edge Cases & Pitfalls
- All zeros should return `"0"`.
- Numeric descending sort fails for `3` and `30`.
- Compare concatenated strings, not sums.

## Related
- Custom Sort String
- Sort Colors
