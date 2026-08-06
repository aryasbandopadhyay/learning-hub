# 16. The K Weakest Rows in a Matrix

- **Difficulty:** Easy
- **Pattern:** Heap / Binary Search
- **Asked at:** Amazon, Google, Bloomberg

## Problem
You are given a binary matrix `mat` where each row has all `1`s before any `0`s. A row's strength is the number of soldiers (`1`s) in it. Row `i` is weaker than row `j` if it has fewer soldiers, or the same number of soldiers and `i < j`. Return the indices of the `k` weakest rows.

Constraints: `1 <= len(mat), len(mat[0]) <= 100`, `1 <= k <= len(mat)`, and each row is sorted as ones followed by zeros.

## Examples
```text
Input: mat = [[1,1,0,0,0],[1,1,1,1,0],[1,0,0,0,0],[1,1,0,0,0],[1,1,1,1,1]], k = 3
Output: [2,0,3]
Explanation: The row strengths are [2, 4, 1, 2, 5], so rows 2, 0, and 3 are the three weakest.
```

## Understanding & Intuition
Every row can be ranked by the pair `(soldier_count, row_index)`. Because rows are sorted, soldier counts can be found by scanning or binary search. A heap is useful when `k` is much smaller than the number of rows.

## Approach 1 — Naive / Brute Force
**Idea:** Count soldiers in every row by scanning all cells, sort `(count, index)` pairs, and return the first `k` indices.
```python
class Solution:
    def kWeakestRows(self, mat, k):
        strengths = []
        for i, row in enumerate(mat):
            count = 0
            for value in row:
                if value == 1:
                    count += 1
            strengths.append((count, i))
        strengths.sort()
        return [idx for count, idx in strengths[:k]]
```
- **Time:** O(m n + m log m) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Maintain a max-heap of size at most `k` using negative `(count, index)` values, so stronger rows are removed when the heap grows too large.
```python
class Solution:
    def kWeakestRows(self, mat, k):
        import heapq
        heap = []
        for i, row in enumerate(mat):
            count = sum(row)
            heapq.heappush(heap, (-count, -i))
            if len(heap) > k:
                heapq.heappop(heap)
        pairs = [(-count, -idx) for count, idx in heap]
        pairs.sort()
        return [idx for count, idx in pairs]
```
- **Time:** O(m n + m log k + k log k) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Use binary search in each row to count soldiers in O(log n), then sort the row ranks and return the weakest indices.
```python
class Solution:
    def kWeakestRows(self, mat, k):
        from bisect import bisect_left
        ranks = []
        for i, row in enumerate(mat):
            inverted = [-value for value in row]
            count = bisect_left(inverted, 0)
            ranks.append((count, i))
        ranks.sort()
        return [idx for count, idx in ranks[:k]]
```
- **Time:** O(m log n + m log m) — **Space:** O(m n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m n + m log m) | O(m) |
| Better | O(m n + m log k + k log k) | O(k) |
| Optimal | O(m log n + m log m) | O(m n) |

## Edge Cases & Pitfalls
- Ties are broken by smaller row index.
- Rows are sorted as `1`s then `0`s, which enables binary search.
- The output contains indices only, not row contents.

## Related
- Kth Largest Element in an Array
- Binary Search
- Top K Frequent Elements
