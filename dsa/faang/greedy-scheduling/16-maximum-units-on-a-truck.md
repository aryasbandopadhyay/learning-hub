# 16. Maximum Units on a Truck

- **Difficulty:** Easy
- **Pattern:** greedy scheduling
- **Asked at:** Amazon, Bloomberg, Microsoft

## Problem
You are given `boxTypes`, where `boxTypes[i] = [numberOfBoxes, unitsPerBox]`, and an integer `truckSize` representing the maximum number of boxes the truck can carry. Return the maximum total number of units that can be put on the truck.

Constraints: `1 <= len(boxTypes) <= 1000`, `1 <= numberOfBoxes, unitsPerBox <= 1000`, and `1 <= truckSize <= 10^6`.

## Examples
```text
Input: boxTypes = [[1,3],[2,2],[3,1]], truckSize = 4
Output: 8
Explanation: Take 1 box with 3 units and 2 boxes with 2 units, then 1 box with 1 unit.
```

## Understanding & Intuition
Every box consumes the same truck capacity, so only units per box matter. A box with more units always dominates a box with fewer units. Therefore, taking higher-unit boxes first is optimal.

## Approach 1 — Naive / Brute Force
**Idea:** Expand every box into its unit value, sort all boxes, and take the best `truckSize` values.
```python
class Solution:
    def maximumUnits(self, boxTypes: list[list[int]], truckSize: int) -> int:
        boxes = []
        for count, units in boxTypes:
            boxes.extend([units] * count)
        boxes.sort(reverse=True)
        return sum(boxes[:truckSize])
```
- **Time:** O(B log B) — **Space:** O(B)

## Approach 2 — Better
**Idea:** Sort box types by units per box and take as many boxes as possible from each type.
```python
class Solution:
    def maximumUnits(self, boxTypes: list[list[int]], truckSize: int) -> int:
        total = 0
        for count, units in sorted(boxTypes, key=lambda item: item[1], reverse=True):
            take = min(count, truckSize)
            total += take * units
            truckSize -= take
            if truckSize == 0:
                break
        return total
```
- **Time:** O(m log m) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Count how many boxes have each unit value, then consume unit buckets from high to low.
```python
class Solution:
    def maximumUnits(self, boxTypes: list[list[int]], truckSize: int) -> int:
        max_units = 0
        for count, units in boxTypes:
            max_units = max(max_units, units)
        buckets = [0] * (max_units + 1)
        for count, units in boxTypes:
            buckets[units] += count
        total = 0
        for units in range(max_units, 0, -1):
            if buckets[units] == 0:
                continue
            take = min(buckets[units], truckSize)
            total += take * units
            truckSize -= take
            if truckSize == 0:
                break
        return total
```
- **Time:** O(m + U) — **Space:** O(U)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(B log B) | O(B) |
| Better | O(m log m) | O(m) |
| Optimal | O(m + U) | O(U) |

## Edge Cases & Pitfalls
- Do not take more boxes than `truckSize` allows.
- Sorting by number of boxes is irrelevant; sort by units per box.
- The truck may fill before all types are considered.

## Related
- Maximum Ice Cream Bars
- Assign Cookies
