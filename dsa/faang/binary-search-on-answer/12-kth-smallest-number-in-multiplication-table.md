# 12. Kth Smallest Number in Multiplication Table

- **Difficulty:** Hard
- **Pattern:** Binary Search on the Answer
- **Asked at:** Google, Amazon, Microsoft

## Problem
In an `m x n` multiplication table, return the `k`-th smallest value counting duplicates. Constraints: `1 <= m,n <= 3 * 10^4`, `1 <= k <= m*n`.

## Examples
```text
Input: m = 3, n = 3, k = 5
Output: 3
Explanation: Sorted values are 1, 2, 2, 3, 3, 4, 6, 6, 9.
```

## Understanding & Intuition
For value `x`, row `i` contributes `min(n, x // i)` values at most `x`. This count is monotone, so the kth value is the first value whose count reaches `k`.

## Approach 1 — Naive / Brute Force
**Idea:** generate and sort all products.
```python
class Solution:
    def findKthNumber(self, m, n, k):
        vals=[]
        for i in range(1,m+1):
            for j in range(1,n+1):
                vals.append(i*j)
        vals.sort()
        return vals[k-1]
```

- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** scan values using the count function.
```python
class Solution:
    def findKthNumber(self, m, n, k):
        def count_le(x):
            total = 0
            for i in range(1, m + 1):
                total += min(n, x // i)
            return total
        for x in range(1, m*n + 1):
            if count_le(x) >= k: return x
        return m*n
```

- **Time:** O(m^2 n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** binary-search the value range using the count.
```python
class Solution:
    def findKthNumber(self, m, n, k):
        if m > n: m, n = n, m
        def count_le(x):
            total=0
            for i in range(1,m+1):
                total += min(n, x // i)
            return total
        lo,hi=1,m*n
        while lo<hi:
            mid=(lo+hi)//2
            if count_le(mid) >= k: hi=mid
            else: lo=mid+1
        return lo
```

- **Time:** O(min(m,n) log(mn)) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn log(mn)) | O(mn) |
| Better | O(m^2 n) | O(1) |
| Optimal | O(min(m,n) log(mn)) | O(1) |


## Edge Cases & Pitfalls
- Count duplicates separately.
- Binary-search values, not coordinates.
- Swapping dimensions speeds up counting.


## Related
- Kth Smallest Element in a Sorted Matrix
- Ugly Number III
