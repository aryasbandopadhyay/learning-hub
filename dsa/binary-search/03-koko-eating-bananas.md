# 03. Koko Eating Bananas

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Google, Amazon, Microsoft, DoorDash

## Problem
Koko has several piles of bananas and `h` hours before the guards return. She chooses one integer
eating speed `k` bananas per hour. Each hour, she chooses one pile and eats up to `k` bananas from it;
if the pile has fewer than `k`, she finishes it and does not start another pile that hour.

Return the minimum integer `k` that lets her eat all bananas within `h` hours.

**Input**
- `piles`: a list where `piles[i]` is the size of the `i`th pile.
- `h`: the number of available hours.

**Output**
- An integer: the minimum feasible eating speed.

## Constraints
- 1 <= piles.length <= 10^4
- piles.length <= h <= 10^9
- 1 <= piles[i] <= 10^9

## Examples
```text
Input: piles = [3,6,7,11], h = 8
Output: 4
Explanation: At speed `4`, the piles take `1 + 2 + 2 + 3 = 8` hours. Any smaller speed takes more than `8` hours.
```

## Understanding & Intuition
This is binary search on the answer. The predicate "can finish at speed `k`" is monotonic: if a speed works, every larger speed also works. Therefore the smallest working speed can be found by searching between `1` and `max(piles)`.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible speed from slowest to fastest.
```python
from typing import List

class Solution:
    def minEatingSpeed(self, piles: List[int], h: int) -> int:
        for speed in range(1, max(piles) + 1):
            hours = sum((pile + speed - 1) // speed for pile in piles)
            if hours <= h:
                return speed
        return max(piles)
```
- **Time:** O(nm) where `m = max(piles)` — **Space:** O(1)

## Approach 2 — Better
**Idea:** Start from the average lower bound, then still scan upward.
```python
from typing import List

class Solution:
    def minEatingSpeed(self, piles: List[int], h: int) -> int:
        # Koko must eat at least this many bananas per hour on average.
        start = max(1, (sum(piles) + h - 1) // h)
        for speed in range(start, max(piles) + 1):
            hours = sum((pile + speed - 1) // speed for pile in piles)
            if hours <= h:
                return speed
        return max(piles)
```
- **Time:** O(n(m - avg)) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search for the first speed whose required hours are at most `h`.
```python
from typing import List

class Solution:
    def minEatingSpeed(self, piles: List[int], h: int) -> int:
        left, right = 1, max(piles)
        while left < right:
            mid = (left + right) // 2
            hours = sum((pile + mid - 1) // mid for pile in piles)
            if hours <= h:
                right = mid
            else:
                left = mid + 1
        return left
```
- **Time:** O(n log m) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nm) | O(1) |
| Better | O(n(m - avg)) | O(1) |
| Optimal | O(n log m) | O(1) |

## Edge Cases & Pitfalls
- Use ceiling division for each pile.
- The lower bound cannot be zero.
- If `h == len(piles)`, the answer is `max(piles)`.

## Related
- Capacity To Ship Packages Within D Days
- Split Array Largest Sum

