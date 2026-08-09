# 14. Boats to Save People

- **Difficulty:** Medium
- **Pattern:** Two Pointers
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given people weights and a boat weight limit, find the fewest boats needed to carry everyone. Each boat carries at most two people and cannot exceed `limit`.

**Input**
- `people`: a list of positive integer weights.
- `limit`: the maximum weight per boat.

**Output**
- The minimum number of boats required.

## Constraints
- `1 <= people.length <= 5 * 10^4`
- `1 <= people[i] <= limit <= 3 * 10^4`

## Examples
```text
Input: people = [3,2,2,1], limit = 3
Output: 3
Explanation: One boat can carry `1` and `2`; the remaining `2` and `3` require two more boats, so the minimum total is `3`.
```

## Understanding & Intuition
The heaviest remaining person must ride now. Pairing them with the lightest possible person, when it fits, never hurts because no heavier partner would fit better.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly choose the heaviest remaining person and scan for the heaviest partner that fits.
```python
from typing import List

class Solution:
    def numRescueBoats(self, people: List[int], limit: int) -> int:
        people = people[:]
        boats = 0
        while people:
            people.sort()
            heavy = people.pop()
            partner_index = -1
            for i, weight in enumerate(people):
                if weight + heavy <= limit:
                    partner_index = i
            if partner_index != -1:
                people.pop(partner_index)
            boats += 1
        return boats
```
- **Time:** O(n² log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort once, then use a used array while scanning for partners.
```python
from typing import List

class Solution:
    def numRescueBoats(self, people: List[int], limit: int) -> int:
        people.sort()
        used = [False] * len(people)
        boats = 0
        for i in range(len(people) - 1, -1, -1):
            if used[i]:
                continue
            used[i] = True
            for j in range(i):
                if not used[j] and people[i] + people[j] <= limit:
                    used[j] = True
                    break
            boats += 1
        return boats
```
- **Time:** O(n²) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort and pair the lightest with the heaviest whenever they fit.
```python
from typing import List

class Solution:
    def numRescueBoats(self, people: List[int], limit: int) -> int:
        people.sort()
        left, right = 0, len(people) - 1
        boats = 0
        while left <= right:
            if people[left] + people[right] <= limit:
                left += 1
            right -= 1
            boats += 1
        return boats
```
- **Time:** O(n log n) — **Space:** O(1) extra

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n² log n) | O(n) |
| Better | O(n²) | O(n) |
| Optimal | O(n log n) | O(1) extra |

## Edge Cases & Pitfalls
- Each boat carries at most two people.
- The heaviest person always consumes one boat.
- Pair with the lightest only if it fits.

## Related
- Assign Cookies
- Two Sum II
