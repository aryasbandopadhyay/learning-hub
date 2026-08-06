# 14. Boats To Save People

- **Difficulty:** Medium
- **Pattern:** Arrays Two Pointers
- **Asked at:** Amazon, Facebook, Google

## Problem
You are given an array `people` where `people[i]` is a person's weight and an integer `limit`, the maximum weight a boat can carry. Each boat carries at most two people. Return the minimum number of boats needed to carry everyone. Constraints: `1 <= len(people) <= 5 * 10^4`, `1 <= people[i] <= limit <= 3 * 10^4`.

## Examples
```text
Input: people = [3,2,2,1], limit = 3
Output: 3
Explanation: Use boats for (1,2), (2), and (3).
```

## Understanding & Intuition
Each boat can take either one heavy person or a heavy person paired with a light enough partner. Pairing the heaviest remaining person with the lightest possible partner is safe because the heaviest must leave now. Sorting exposes that greedy choice.

## Approach 1 — Naive / Brute Force
**Idea:** repeatedly take the heaviest remaining person and linearly scan for the lightest partner that fits.
```python
class Solution:
    def numRescueBoats(self, people, limit):
        remaining = sorted(people)
        boats = 0
        while remaining:
            heaviest = remaining.pop()
            partner = -1
            for i in range(len(remaining)):
                if remaining[i] + heaviest <= limit:
                    partner = i
                    break
            if partner != -1:
                remaining.pop(partner)
            boats += 1
        return boats
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** repeatedly take the heaviest person and use binary search in a sorted list to find the heaviest partner that fits.
```python
class Solution:
    def numRescueBoats(self, people, limit):
        import bisect
        remaining = sorted(people)
        boats = 0
        while remaining:
            heaviest = remaining.pop()
            partner_limit = limit - heaviest
            idx = bisect.bisect_right(remaining, partner_limit) - 1
            if idx >= 0:
                remaining.pop(idx)
            boats += 1
        return boats
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** sort weights, then pair the heaviest remaining person with the lightest remaining person whenever they fit.
```python
class Solution:
    def numRescueBoats(self, people, limit):
        people = sorted(people)
        left = 0
        right = len(people) - 1
        boats = 0
        while left <= right:
            if people[left] + people[right] <= limit:
                left += 1
            right -= 1
            boats += 1
        return boats
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- A boat can carry at most two people, not any number up to the weight limit.
- The heaviest person always needs a boat; only decide whether to pair them.
- Sorting the input copy avoids mutating the caller's list.

## Related
- Two Sum II Input Array Is Sorted
- Bag of Tokens
