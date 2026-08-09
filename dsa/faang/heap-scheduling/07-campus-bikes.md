# 07. Campus Bikes

- **Difficulty:** Medium
- **Pattern:** heaps for scheduling/simulation
- **Asked at:** Google, Amazon, Uber

## Problem
On a campus, `workers[i] = [xi, yi]` and `bikes[j] = [xj, yj]`. Assign exactly one bike to each worker. Repeatedly choose the unassigned worker-bike pair with the smallest Manhattan distance, breaking ties by smaller worker index, then smaller bike index.

Return `answer`, where `answer[i]` is the bike assigned to worker `i`.

**Input**
- `workers`: a `list[list[int]]`; worker coordinates or strengths, as described above.
- `bikes`: a `list[list[int]]`; bike coordinates.

**Output**
- A `list[int]`. Return `answer`, where `answer[i]` is the bike assigned to worker `i`. This judge compares the sequence exactly: `answer[i]` must be the bike assigned to worker `i`, in increasing worker index order.

## Constraints
- `1 <= len(workers) <= len(bikes) <= 1000`, coordinates are between `0` and `1000`.

## Examples
```text
Input: workers = [[0,0],[2,1]], bikes = [[1,2],[3,3]]
Output: [1,0]
Explanation: Pair worker 0 with bike 1 at distance 6 only after worker 1 takes bike 0 at distance 2. The output is written in the required deterministic order.
```

## Understanding & Intuition
The assignment process is not globally optimal matching; it is a deterministic greedy simulation over all pairs. A heap can expose the next smallest pair without sorting every pair repeatedly. Because tie-breaking is fixed, all valid implementations must return the same list.

## Approach 1 — Naive / Brute Force
**Idea:** Generate and sort every possible worker-bike pair, then greedily accept pairs whose worker and bike are both unused.
```python
class Solution:
    def assignBikes(self, workers: list[list[int]], bikes: list[list[int]]) -> list[int]:
        pairs = []
        for i, (wx, wy) in enumerate(workers):
            for j, (bx, by) in enumerate(bikes):
                pairs.append((abs(wx - bx) + abs(wy - by), i, j))
        pairs.sort()
        ans = [-1] * len(workers)
        used_bikes = set()
        assigned = 0
        for _, i, j in pairs:
            if ans[i] == -1 and j not in used_bikes:
                ans[i] = j
                used_bikes.add(j)
                assigned += 1
                if assigned == len(workers):
                    break
        return ans
```
- **Time:** O(wb log(wb)) — **Space:** O(wb)

## Approach 2 — Better
**Idea:** Pre-sort each worker's bike choices, then use a global heap containing each worker's current best available candidate.
```python
class Solution:
    def assignBikes(self, workers: list[list[int]], bikes: list[list[int]]) -> list[int]:
        import heapq
        choices = []
        for i, (wx, wy) in enumerate(workers):
            row = []
            for j, (bx, by) in enumerate(bikes):
                row.append((abs(wx - bx) + abs(wy - by), i, j))
            row.sort()
            choices.append(row)
        ptr = [0] * len(workers)
        heap = [choices[i][0] for i in range(len(workers))]
        heapq.heapify(heap)
        ans = [-1] * len(workers)
        used = set()
        assigned = 0
        while assigned < len(workers):
            _, i, j = heapq.heappop(heap)
            if ans[i] != -1:
                continue
            if j in used:
                ptr[i] += 1
                while choices[i][ptr[i]][2] in used:
                    ptr[i] += 1
                heapq.heappush(heap, choices[i][ptr[i]])
            else:
                ans[i] = j
                used.add(j)
                assigned += 1
        return ans
```
- **Time:** O(wb log b + wb log w) — **Space:** O(wb)

## Approach 3 — Optimal
**Idea:** Bucket pairs by Manhattan distance and scan buckets in increasing distance while respecting worker and bike tie-breaks.
```python
class Solution:
    def assignBikes(self, workers: list[list[int]], bikes: list[list[int]]) -> list[int]:
        max_dist = 2000
        buckets = [[] for _ in range(max_dist + 1)]
        for i, (wx, wy) in enumerate(workers):
            for j, (bx, by) in enumerate(bikes):
                d = abs(wx - bx) + abs(wy - by)
                buckets[d].append((i, j))
        ans = [-1] * len(workers)
        used = set()
        assigned = 0
        for bucket in buckets:
            bucket.sort()
            for i, j in bucket:
                if ans[i] == -1 and j not in used:
                    ans[i] = j
                    used.add(j)
                    assigned += 1
                    if assigned == len(workers):
                        return ans
        return ans
```
- **Time:** O(wb + D + sorting within buckets) — **Space:** O(wb + D)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(wb log(wb)) | O(wb) |
| Better | O(wb log b + wb log w) | O(wb) |
| Optimal | O(wb + D + bucket sorting) | O(wb + D) |

## Edge Cases & Pitfalls
- This greedy process is defined by pair order, not by minimum total distance.
- Tie-breaks are distance, worker index, then bike index.
- Stop once every worker has a bike.

## Related
- Find K Pairs with Smallest Sums
- Total Cost to Hire K Workers
