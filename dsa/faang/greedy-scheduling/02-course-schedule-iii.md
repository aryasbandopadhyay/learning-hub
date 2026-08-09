# 02. Course Schedule III

- **Difficulty:** Hard
- **Pattern:** greedy scheduling & assignment
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Implement `scheduleCourse` for **Course Schedule III**. Given `courses[i] = [duration, lastDay]`, start on day `0` and take one course at a time. A selected course must finish on or before its deadline. Return the maximum number of courses you can take.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `courses`: list; course duration/deadline pairs.

**Output**
- A single integer.

## Constraints
- Use the standard constraints for this problem as implied by the judge manifest and examples.

## Examples
```text
Input: courses = [[100,200],[200,1300],[1000,1250],[2000,3200]]
Output: 3
Explanation: Take durations 100, 1000, and 200. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Considering courses by deadline makes each prefix a deadline-feasible scheduling problem. When total time is too large, dropping the longest selected course keeps the count as high as possible while freeing maximum time. This exchange is the greedy core.

## Approach 1 — Naive / Brute Force
**Idea:** Time-indexed dynamic programming stores the best number of courses for each possible finish time.
```python
class Solution:
    def scheduleCourse(self, courses: list[list[int]]) -> int:
        courses.sort(key=lambda x: x[1])
        limit = max(d for _, d in courses)
        dp = [-10**9] * (limit + 1)
        dp[0] = 0
        for duration, last in courses:
            for t in range(last, duration - 1, -1):
                dp[t] = max(dp[t], dp[t - duration] + 1)
        return max(dp)
```
- **Time:** O(nT) — **Space:** O(T)

## Approach 2 — Better
**Idea:** Let `dp[k]` be the minimum total time needed to take exactly `k` courses so far.
```python
class Solution:
    def scheduleCourse(self, courses: list[list[int]]) -> int:
        courses.sort(key=lambda x: x[1])
        n = len(courses)
        inf = 10**18
        dp = [0] + [inf] * n
        best = 0
        for duration, last in courses:
            for k in range(best, -1, -1):
                if dp[k] + duration <= last and dp[k] + duration < dp[k + 1]:
                    dp[k + 1] = dp[k] + duration
                    best = max(best, k + 1)
        return best
```
- **Time:** O(n²) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep chosen durations in a max-heap; whenever a deadline is missed, remove the longest chosen course.
```python
class Solution:
    def scheduleCourse(self, courses: list[list[int]]) -> int:
        import heapq
        total = 0
        heap = []
        for duration, last in sorted(courses, key=lambda x: x[1]):
            total += duration
            heapq.heappush(heap, -duration)
            if total > last:
                total += heapq.heappop(heap)
        return len(heap)
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nT) | O(T) |
| Better | O(n²) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- A course longer than its own deadline will be removed if selected.
- Sort by deadline, not by duration.
- Heap values are negated to simulate a max-heap.

## Related
- Maximum Number of Events That Can Be Attended
- Minimum Number of Refueling Stops
