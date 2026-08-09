# 15. Maximum Units on a Truck

- **Difficulty:** Easy
- **Pattern:** Greedy
- **Asked at:** Amazon, Microsoft, Google

## Problem
Each `boxTypes[i] = [numberOfBoxes, unitsPerBox]` describes boxes of one type. You may load at most
`truckSize` boxes total. Return the maximum number of units that can be loaded onto the truck.

**Input**
- `boxTypes`: a list of `[numberOfBoxes, unitsPerBox]` pairs.
- `truckSize`: the maximum number of boxes the truck can carry.

**Output**
- An integer: the maximum total units loadable.

## Constraints
- 1 <= boxTypes.length <= 1000
- 1 <= numberOfBoxes, unitsPerBox <= 1000
- 1 <= truckSize <= 10^6

## Examples
```text
Input: boxTypes = [[1,3],[2,2],[3,1]], truckSize = 4
Output: 8
Explanation: Load the one box with `3` units and two boxes with `2` units each, then one box with `1` unit, for `8` total units.
```

## Understanding & Intuition
Every box consumes the same truck capacity, so the best choice is always the box with more units per box. Sorting by units descending makes the local best choice globally optimal. Take as many as possible from each highest-value type before moving on.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try taking every possible count from each box type.
```python
from typing import List

class Solution:
    def maximumUnits(self, boxTypes: List[List[int]], truckSize: int) -> int:
        def dfs(i: int, remaining: int) -> int:
            if i == len(boxTypes) or remaining == 0:
                return 0
            boxes, units = boxTypes[i]
            best = 0
            for take in range(min(boxes, remaining) + 1):
                best = max(best, take * units + dfs(i + 1, remaining - take))
            return best

        return dfs(0, truckSize)
```
- **Time:** O(truckSize^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Dynamic programming by capacity over box types.
```python
from typing import List

class Solution:
    def maximumUnits(self, boxTypes: List[List[int]], truckSize: int) -> int:
        dp = [0] * (truckSize + 1)
        for boxes, units in boxTypes:
            next_dp = dp[:]
            for capacity in range(truckSize + 1):
                for take in range(min(boxes, capacity) + 1):
                    next_dp[capacity] = max(
                        next_dp[capacity],
                        dp[capacity - take] + take * units
                    )
            dp = next_dp
        return dp[truckSize]
```
- **Time:** O(n * truckSize * maxBoxes) — **Space:** O(truckSize)

## Approach 3 — Optimal
**Idea:** Sort by units per box descending and load greedily.
```python
from typing import List

class Solution:
    def maximumUnits(self, boxTypes: List[List[int]], truckSize: int) -> int:
        boxTypes.sort(key=lambda box: box[1], reverse=True)
        total = 0

        for boxes, units in boxTypes:
            take = min(boxes, truckSize)
            total += take * units
            truckSize -= take
            if truckSize == 0:
                break
        return total
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(truckSize^n) | O(n) |
| Better | O(n * truckSize * maxBoxes) | O(truckSize) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- Stop once the truck is full.
- Sorting ascending by units gives the worst result.
- Taking a partial number of boxes from a type is allowed.

## Related
- Fractional Knapsack
- Sort by Value Greedy
