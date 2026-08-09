# 05. Merge Triplets to Form Target Triplet

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Amazon, Google, Meta

## Problem
You are given triplets of non-negative integers and a target triplet. You may choose any subset of
triplets and merge them by taking the coordinate-wise maximum. Determine whether some subset can merge
exactly into `target`.

**Input**
- `triplets`: a list of three-element integer lists.
- `target`: the desired three-element list.

**Output**
- A boolean: `True` if the target triplet can be formed, otherwise `False`.

## Constraints
- 1 <= triplets.length <= 10^5
- triplets[i].length == target.length == 3
- 1 <= triplets[i][j], target[j] <= 1000

## Examples
```text
Input: triplets = [[2,5,3],[1,8,4],[1,7,5]], target = [2,7,5]
Output: True
Explanation: Ignore triplets with a coordinate above the target. The first and third triplets can provide the needed `2`, `7`, and `5` without exceeding the target.
```

## Understanding & Intuition
Any triplet with a coordinate greater than target can never participate, because merging only increases values. Among safe triplets, we only need to cover each target coordinate exactly at least once. This greedy filtering is safe because coordinate-wise maxima are independent.

## Approach 1 — Naive / Brute Force
**Idea:** Try every subset of triplets and compute its merged maximum.
```python
from typing import List

class Solution:
    def mergeTriplets(self, triplets: List[List[int]], target: List[int]) -> bool:
        n = len(triplets)
        for mask in range(1 << n):
            merged = [0, 0, 0]
            for i in range(n):
                if mask & (1 << i):
                    for j in range(3):
                        merged[j] = max(merged[j], triplets[i][j])
            if merged == target:
                return True
        return False
```
- **Time:** O(2^n * n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Keep only triplets that do not exceed target, then merge all safe triplets.
```python
from typing import List

class Solution:
    def mergeTriplets(self, triplets: List[List[int]], target: List[int]) -> bool:
        merged = [0, 0, 0]
        for triplet in triplets:
            if all(triplet[i] <= target[i] for i in range(3)):
                for i in range(3):
                    merged[i] = max(merged[i], triplet[i])
        return merged == target
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Mark target coordinates matched by safe triplets and stop after all three are covered.
```python
from typing import List

class Solution:
    def mergeTriplets(self, triplets: List[List[int]], target: List[int]) -> bool:
        matched = [False, False, False]

        for triplet in triplets:
            if any(triplet[i] > target[i] for i in range(3)):
                continue
            for i in range(3):
                if triplet[i] == target[i]:
                    matched[i] = True
            if all(matched):
                return True
        return False
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n * n) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A triplet exceeding target in any coordinate must be ignored.
- The same triplet may cover multiple coordinates.
- Do not require one triplet to equal the full target.

## Related
- Coordinate-wise Maximum
- Set Cover Greedy
