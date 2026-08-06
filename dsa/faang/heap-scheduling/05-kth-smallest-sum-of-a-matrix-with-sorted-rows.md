# 05. Kth Smallest Sum of a Matrix With Sorted Rows

- **Difficulty:** Hard
- **Pattern:** heaps for scheduling/simulation
- **Asked at:** Google, Amazon, ByteDance

## Problem
You are given a matrix `mat` whose rows are sorted in nondecreasing order, and an integer `k`. Choose exactly one element from each row and sum the chosen elements.

Return the `k`th smallest possible sum.

Constraints: `1 <= len(mat), len(mat[i]) <= 40`, `1 <= k <= min(200, product of row lengths)`, `1 <= mat[i][j] <= 5000`.

## Examples
```text
Input: mat = [[1,3,11],[2,4,6]], k = 5
Output: 7
Explanation: The sorted sums are 3, 5, 5, 7, 7, so the 5th is 7.
```

## Understanding & Intuition
Each row contributes one choice, and rows are sorted, so partial sums can be expanded from smaller choices to larger choices. Keeping only the smallest `k` sums after each row prevents the state space from exploding. A heap can also simulate the global next-smallest state directly.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every possible row-choice sum, sort them, and return the `k`th.
```python
class Solution:
    def kthSmallest(self, mat: list[list[int]], k: int) -> int:
        sums = [0]
        for row in mat:
            new_sums = []
            for s in sums:
                for x in row:
                    new_sums.append(s + x)
            sums = new_sums
        sums.sort()
        return sums[k - 1]
```
- **Time:** O(P log P) — **Space:** O(P), where P is the product of row lengths

## Approach 2 — Better
**Idea:** Merge rows one at a time, sorting candidate sums and trimming to the smallest `k` after each row.
```python
class Solution:
    def kthSmallest(self, mat: list[list[int]], k: int) -> int:
        sums = [0]
        for row in mat:
            merged = []
            for s in sums:
                for x in row:
                    merged.append(s + x)
            merged.sort()
            sums = merged[:k]
        return sums[k - 1]
```
- **Time:** O(mk n log(kn)) — **Space:** O(kn)

## Approach 3 — Optimal
**Idea:** Treat each tuple of row indices as a state. Start with all zeros, pop the smallest sum, and push states formed by advancing one row index.
```python
class Solution:
    def kthSmallest(self, mat: list[list[int]], k: int) -> int:
        import heapq
        m = len(mat)
        start = tuple([0] * m)
        start_sum = sum(row[0] for row in mat)
        heap = [(start_sum, start)]
        seen = {start}
        ans = start_sum
        for _ in range(k):
            ans, state = heapq.heappop(heap)
            for r in range(m):
                c = state[r]
                if c + 1 < len(mat[r]):
                    nxt = list(state)
                    nxt[r] += 1
                    nxt = tuple(nxt)
                    if nxt not in seen:
                        seen.add(nxt)
                        ns = ans - mat[r][c] + mat[r][c + 1]
                        heapq.heappush(heap, (ns, nxt))
        return ans
```
- **Time:** O(km log(km)) — **Space:** O(km)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(P log P) | O(P) |
| Better | O(mk n log(kn)) | O(kn) |
| Optimal | O(km log(km)) | O(km) |

## Edge Cases & Pitfalls
- Duplicate sums count separately.
- The matrix rows are sorted, but columns do not matter.
- Track visited index tuples to avoid pushing the same state multiple times.

## Related
- Kth Smallest Element in a Sorted Matrix
- Find K Pairs with Smallest Sums
