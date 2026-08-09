# 08. Maximum Points You Can Obtain from Cards

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Amazon, Microsoft

## Problem
You are given card points in a row and an integer `k`.

On each move, take exactly one card from either the left end or the right end. After exactly `k` moves, return the maximum points you can collect.

**Input**
- `cardPoints`: a list of card values from left to right.
- `k`: the number of cards to take.

**Output**
- The maximum score after taking exactly `k` end cards.

## Constraints
- `1 <= cardPoints.length <= 10^5`
- `1 <= cardPoints[i] <= 10^4`
- `1 <= k <= cardPoints.length`

## Examples
```text
Input: cardPoints = [1,2,3,4,5,6,1], k = 3
Output: 12
Explanation: Taking the three cards from the right gives `5 + 6 + 1 = 12`, which is optimal.
```

## Understanding & Intuition
Choosing cards from both ends is equivalent to leaving one contiguous middle block of length `n - k`. Maximizing taken points means minimizing the sum of that middle block. Prefix sums or a fixed-size sliding window both compute this efficiently.

## Approach 1 — Naive / Brute Force
**Idea:** Try taking `left` cards from the front and `k - left` from the back, summing each choice directly.
```python
class Solution:
    def maxScore(self, cardPoints: list[int], k: int) -> int:
        n = len(cardPoints)
        best = 0
        for left in range(k + 1):
            total = 0
            for i in range(left):
                total += cardPoints[i]
            for i in range(n - (k - left), n):
                total += cardPoints[i]
            best = max(best, total)
        return best
```
- **Time:** O(k^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Precompute prefix sums from both ends and combine every front/back split in O(1).
```python
class Solution:
    def maxScore(self, cardPoints: list[int], k: int) -> int:
        front = [0]
        for x in cardPoints:
            front.append(front[-1] + x)
        back = [0]
        for x in reversed(cardPoints):
            back.append(back[-1] + x)
        best = 0
        for left in range(k + 1):
            best = max(best, front[left] + back[k - left])
        return best
```
- **Time:** O(n + k) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Find the minimum sum contiguous block of length `n - k` to leave behind, then subtract it from the total.
```python
class Solution:
    def maxScore(self, cardPoints: list[int], k: int) -> int:
        n = len(cardPoints)
        total = sum(cardPoints)
        keep = n - k
        if keep == 0:
            return total
        window = sum(cardPoints[:keep])
        min_keep = window
        for i in range(keep, n):
            window += cardPoints[i] - cardPoints[i - keep]
            min_keep = min(min_keep, window)
        return total - min_keep
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k^2) | O(1) |
| Better | O(n + k) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- When `k == n`, all cards must be taken.
- Front/back prefix arrays should include a leading zero.
- The middle-block transformation avoids end-picking simulation.

## Related
- Maximum Average Subarray I
- Minimum Size Subarray Sum
