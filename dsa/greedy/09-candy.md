# 09. Candy

- **Difficulty:** Hard
- **Pattern:** Greedy
- **Asked at:** Google, Amazon, Microsoft, Bloomberg

## Problem
There are `n` children standing in a line, and `ratings[i]` is the rating of child `i`. Give each
child at least one candy. Any child with a higher rating than an immediate neighbor must receive more
candies than that neighbor.

Return the minimum total number of candies needed.

**Input**
- `ratings`: a list of child ratings in line order.

**Output**
- An integer: the minimum total candy count.

## Constraints
- 1 <= ratings.length <= 2 * 10^4
- 0 <= ratings[i] <= 2 * 10^4

## Examples
```text
Input: ratings = [1,0,2]
Output: 5
Explanation: A minimum valid distribution is `[2,1,2]`, totaling `5` candies.
```

## Understanding & Intuition
Each child is constrained by the neighbor on the left and the neighbor on the right. A left-to-right pass satisfies increasing slopes from the left. A right-to-left pass then fixes decreasing slopes without breaking the previous constraints.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan and increment candies until every neighbor rule is satisfied.
```python
from typing import List

class Solution:
    def candy(self, ratings: List[int]) -> int:
        candies = [1] * len(ratings)
        changed = True

        while changed:
            changed = False
            for i in range(len(ratings)):
                if i > 0 and ratings[i] > ratings[i - 1] and candies[i] <= candies[i - 1]:
                    candies[i] = candies[i - 1] + 1
                    changed = True
                if i + 1 < len(ratings) and ratings[i] > ratings[i + 1] and candies[i] <= candies[i + 1]:
                    candies[i] = candies[i + 1] + 1
                    changed = True
        return sum(candies)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use two arrays for left and right requirements, then take the maximum per child.
```python
from typing import List

class Solution:
    def candy(self, ratings: List[int]) -> int:
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
**Idea:** Reuse one candies array and update it in two directional passes.
```python
from typing import List

class Solution:
    def candy(self, ratings: List[int]) -> int:
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
- Equal ratings do not require different candy counts.
- Decreasing sequences need the right-to-left pass.
- Use `max` in the second pass to preserve left-side constraints.

## Related
- Trapping Rain Water
- Wiggle Subsequence
