# 14. Candy

- **Difficulty:** Hard
- **Pattern:** greedy scheduling
- **Asked at:** Google, Amazon, Microsoft

## Problem
There are children standing in a line with integer `ratings`. Give each child at least one candy. Any child with a higher rating than an immediate neighbor must receive more candies than that neighbor. Return the minimum total candies needed.

Constraints: `1 <= len(ratings) <= 2 * 10^4` and `0 <= ratings[i] <= 2 * 10^4`.

## Examples
```text
Input: ratings = [1,0,2]
Output: 5
Explanation: The minimum distribution is [2, 1, 2].
```

## Understanding & Intuition
Each increasing run imposes left-to-right constraints, and each decreasing run imposes right-to-left constraints. The answer is the smallest distribution satisfying both directions. Greedy passes work because only adjacent comparisons matter.

## Approach 1 — Naive / Brute Force
**Idea:** Start with one candy each and repeatedly fix any violated neighbor constraint until stable.
```python
class Solution:
    def candy(self, ratings: list[int]) -> int:
        n = len(ratings)
        candies = [1] * n
        changed = True
        while changed:
            changed = False
            for i in range(n):
                if i > 0 and ratings[i] > ratings[i - 1] and candies[i] <= candies[i - 1]:
                    candies[i] = candies[i - 1] + 1
                    changed = True
                if i + 1 < n and ratings[i] > ratings[i + 1] and candies[i] <= candies[i + 1]:
                    candies[i] = candies[i + 1] + 1
                    changed = True
        return sum(candies)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Compute candies required by the left neighbor and right neighbor separately, then take the maximum for each child.
```python
class Solution:
    def candy(self, ratings: list[int]) -> int:
        n = len(ratings)
        left = [1] * n
        right = [1] * n
        for i in range(1, n):
            if ratings[i] > ratings[i - 1]:
                left[i] = left[i - 1] + 1
        for i in range(n - 2, -1, -1):
            if ratings[i] > ratings[i + 1]:
                right[i] = right[i + 1] + 1
        return sum(max(left[i], right[i]) for i in range(n))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use one candy array, first satisfying left constraints and then repairing right constraints in place.
```python
class Solution:
    def candy(self, ratings: list[int]) -> int:
        n = len(ratings)
        candies = [1] * n
        for i in range(1, n):
            if ratings[i] > ratings[i - 1]:
                candies[i] = candies[i - 1] + 1
        for i in range(n - 2, -1, -1):
            if ratings[i] > ratings[i + 1]:
                candies[i] = max(candies[i], candies[i + 1] + 1)
        return sum(candies)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Equal ratings do not require unequal candies.
- Strictly decreasing arrays need a right-to-left correction.
- Each child must receive at least one candy.

## Related
- Gas Station
- Assign Cookies
