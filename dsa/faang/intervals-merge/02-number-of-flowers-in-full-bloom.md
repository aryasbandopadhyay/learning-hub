# 02. Number of Flowers in Full Bloom

- **Difficulty:** Hard
- **Pattern:** interval merging & sweep line
- **Asked at:** Google, Amazon, Meta

## Problem
Each flower is represented by `[start, end]` and is blooming on every integer time from `start` through `end`, inclusive. For each arrival time in `people`, return how many flowers are in bloom at that time.

**Input**
- `flowers`: a `list[list[int]]`; flower blooming intervals.
- `people`: a `list[int]`; query arrival times.

**Output**
- A `list[int]`. Return how many flowers are in bloom at that time. This judge compares the sequence exactly: `answer[i]` must answer `people[i]`, preserving the original `people` order.

## Constraints
- `1 <= len(flowers), len(people) <= 10^5`, `1 <= start <= end <= 10^9`, and `1 <= people[i] <= 10^9`.

## Examples
```text
Input: flowers = [[1,6],[3,7],[9,12],[4,13]], people = [2,3,7,11]
Output: [1,2,2,2]
Explanation: Count intervals with start <= person <= end for each arrival. The output is written in the required deterministic order.
```

## Understanding & Intuition
Each flower contributes `+1` from its start and stops contributing after its end. Because queries are independent, they can be answered by either scanning all intervals, sweeping events and queries together, or binary-searching separate start/end arrays.

## Approach 1 — Naive / Brute Force
**Idea:** For every person, count all intervals containing that arrival time.
```python
class Solution:
    def fullBloomFlowers(self, flowers: list[list[int]], people: list[int]) -> list[int]:
        ans = []
        for t in people:
            count = 0
            for s, e in flowers:
                if s <= t <= e:
                    count += 1
            ans.append(count)
        return ans
```
- **Time:** O(nq) — **Space:** O(1) besides output

## Approach 2 — Better
**Idea:** Sweep flower starts, flower ends, and indexed people queries in chronological order.
```python
class Solution:
    def fullBloomFlowers(self, flowers: list[list[int]], people: list[int]) -> list[int]:
        events = []
        for s, e in flowers:
            events.append((s, 0, -1))
            events.append((e, 2, -1))
        for i, t in enumerate(people):
            events.append((t, 1, i))
        events.sort()
        ans = [0] * len(people)
        active = 0
        for _, kind, idx in events:
            if kind == 0:
                active += 1
            elif kind == 1:
                ans[idx] = active
            else:
                active -= 1
        return ans
```
- **Time:** O((n + q) log(n + q)) — **Space:** O(n + q)

## Approach 3 — Optimal
**Idea:** Sort starts and ends; at time `t`, blooming flowers are starts `<= t` minus ends `< t`.
```python
class Solution:
    def fullBloomFlowers(self, flowers: list[list[int]], people: list[int]) -> list[int]:
        from bisect import bisect_right, bisect_left
        starts = sorted(s for s, _ in flowers)
        ends = sorted(e for _, e in flowers)
        ans = []
        for t in people:
            ans.append(bisect_right(starts, t) - bisect_left(ends, t))
        return ans
```
- **Time:** O((n + q) log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nq) | O(1) |
| Better | O((n + q) log(n + q)) | O(n + q) |
| Optimal | O((n + q) log n) | O(n) |

## Edge Cases & Pitfalls
- Flower end times are inclusive.
- Preserve the original order of `people`.
- Query events must occur after starts and before ends at the same time.

## Related
- Range Addition
- Minimum Interval to Include Each Query
