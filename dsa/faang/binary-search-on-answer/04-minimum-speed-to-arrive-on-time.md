# 04. Minimum Speed to Arrive on Time

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Google, DoorDash, Amazon

## Problem
Implement `minSpeedOnTime` for **Minimum Speed to Arrive on Time**. Take trains through `dist` in order. All but the last train round travel time up to the next integer hour. Return the minimum integer speed to arrive within `hour`, or `-1`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `dist`: list; distances in order.
- `hour`: number; maximum allowed time.

**Output**
- A single integer.

## Constraints
- `1 <= len(dist) <= 10^5`, speed answer at most `10^7`

## Examples
```text
Input: dist = [1, 3, 2], hour = 2.7
Output: 3
Explanation: Time is 1 + 1 + 2/3, which fits. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Higher speed never increases travel time. Waiting applies before every train except the last, so feasibility is monotone in speed.

## Approach 1 — Naive / Brute Force
**Idea:** scan speeds from 1 upward.
```python
class Solution:
    def minSpeedOnTime(self, dist, hour):
        if hour <= len(dist) - 1:
            return -1
        limit = int(round(hour * 100))
        def can(speed):
            used = 0
            for d in dist[:-1]:
                used += ((d + speed - 1) // speed) * 100
                if used > limit: return False
            return used + dist[-1] * 100 <= limit * speed
        for s in range(1,10000001):
            if can(s): return s
        return -1
```

- **Time:** O(nV) — **Space:** O(1)

## Approach 2 — Better
**Idea:** binary-search using floating-time checks.
```python
class Solution:
    def minSpeedOnTime(self, dist, hour):
        if hour <= len(dist) - 1: return -1
        def can(speed):
            total=0.0
            for d in dist[:-1]: total += (d + speed - 1) // speed
            return total + dist[-1] / speed <= hour + 1e-12
        lo,hi,ans=1,10000000,-1
        while lo<=hi:
            mid=(lo+hi)//2
            if can(mid): ans=mid; hi=mid-1
            else: lo=mid+1
        return ans
```

- **Time:** O(n log V) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** binary-search using integer hundredths.
```python
class Solution:
    def minSpeedOnTime(self, dist, hour):
        if hour <= len(dist) - 1:
            return -1
        limit = int(round(hour * 100))
        def can(speed):
            used = 0
            for d in dist[:-1]:
                used += ((d + speed - 1) // speed) * 100
                if used > limit: return False
            return used + dist[-1] * 100 <= limit * speed
        lo,hi,ans=1,10000000,-1
        while lo<=hi:
            mid=(lo+hi)//2
            if can(mid): ans=mid; hi=mid-1
            else: lo=mid+1
        return ans
```

- **Time:** O(n log V) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nV) | O(1) |
| Better | O(n log V) | O(1) |
| Optimal | O(n log V) | O(1) |


## Edge Cases & Pitfalls
- If `hour <= len(dist)-1`, arrival is impossible.
- Do not round the last train.
- Integer comparisons avoid precision bugs.


## Related
- Koko Eating Bananas
- Capacity to Ship Packages Within D Days
