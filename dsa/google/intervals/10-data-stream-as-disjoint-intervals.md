# 10. Data Stream as Disjoint Intervals

- **Difficulty:** Hard
- **Pattern:** intervals
- **Asked at:** Google, Twitter, Amazon

## Problem
Given a list `nums` representing numbers received from a data stream in order, return the final set of disjoint sorted intervals that summarize all distinct values seen. Consecutive integers belong to the same interval.

Constraints: `0 <= len(nums) <= 10^4`, `0 <= nums[i] <= 10^9`.

## Examples
```text
Input: nums = [1,3,7,2,6]
Output: [[1, 3], [6, 7]]
Explanation: Values 1, 2, 3 merge into [1,3], and 6, 7 merge into [6,7].
```

## Understanding & Intuition
The design version of this problem can be converted into a deterministic function over the stream. Duplicates do not change the final summary. The core task is maintaining or constructing maximal consecutive runs.

## Approach 1 — Naive / Brute Force
**Idea:** After processing the stream, sort unique numbers and compress consecutive runs.
```python
class Solution:
    def summaryRanges(self, nums: list[int]) -> list[list[int]]:
        values = sorted(set(nums))
        answer = []
        for value in values:
            if not answer or value > answer[-1][1] + 1:
                answer.append([value, value])
            else:
                answer[-1][1] = value
        return answer
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Insert unique values one by one into sorted intervals, merging with neighboring intervals when adjacent.
```python
class Solution:
    def summaryRanges(self, nums: list[int]) -> list[list[int]]:
        from bisect import bisect_left
        intervals = []
        seen = set()
        for value in nums:
            if value in seen:
                continue
            seen.add(value)
            i = bisect_left(intervals, [value, value])
            left_join = i > 0 and intervals[i - 1][1] + 1 == value
            right_join = i < len(intervals) and intervals[i][0] - 1 == value
            if left_join and right_join:
                intervals[i - 1][1] = intervals[i][1]
                intervals.pop(i)
            elif left_join:
                intervals[i - 1][1] = value
            elif right_join:
                intervals[i][0] = value
            else:
                intervals.insert(i, [value, value])
        return intervals
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use interval-boundary hash maps so each unique value creates or merges runs in expected constant time, then sort starts once.
```python
class Solution:
    def summaryRanges(self, nums: list[int]) -> list[list[int]]:
        seen = set()
        start_to_end = {}
        end_to_start = {}
        for value in nums:
            if value in seen:
                continue
            seen.add(value)
            left_start = end_to_start.pop(value - 1, None)
            right_end = start_to_end.pop(value + 1, None)
            if left_start is None:
                left_start = value
            else:
                start_to_end.pop(left_start)
            if right_end is None:
                right_end = value
            else:
                end_to_start.pop(right_end)
            start_to_end[left_start] = right_end
            end_to_start[right_end] = left_start
        return [[start, start_to_end[start]] for start in sorted(start_to_end)]
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Ignore duplicate stream values.
- A value can bridge two existing intervals at once.
- Return intervals sorted by start for deterministic judging.

## Related
- Amount of New Area Painted Each Day
- Merge Intervals
- Insert Interval
