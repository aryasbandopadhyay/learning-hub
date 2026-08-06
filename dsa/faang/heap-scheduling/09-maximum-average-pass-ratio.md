# 09. Maximum Average Pass Ratio

- **Difficulty:** Medium
- **Pattern:** heaps for scheduling/simulation
- **Asked at:** Amazon, Google, TikTok

## Problem
Each class is `[pass, total]`. You have `extraStudents` guaranteed to pass. Assign each extra student to one class to maximize the average pass ratio across all classes.

Return the maximum average pass ratio, rounded to 5 decimal places.

Constraints: `1 <= len(classes) <= 10^5`, `1 <= pass <= total <= 10^5`, `1 <= extraStudents <= 10^5`.

## Examples
```text
Input: classes = [[1,2],[3,5],[2,2]], extraStudents = 2
Output: 0.78333
Explanation: Assign both extra students to the first two classes for the best average.
```

## Understanding & Intuition
Adding a passing student has diminishing returns for a class. Therefore, each step should give the next student to the class with the largest marginal gain. A max-heap efficiently updates that gain after each assignment.

## Approach 1 — Naive / Brute Force
**Idea:** For every extra student, scan all classes and update the class with the largest gain.
```python
class Solution:
    def maxAverageRatio(self, classes: list[list[int]], extraStudents: int) -> float:
        def gain(p, t):
            return (p + 1) / (t + 1) - p / t
        classes = [c[:] for c in classes]
        for _ in range(extraStudents):
            best = 0
            for i, (p, t) in enumerate(classes):
                if gain(p, t) > gain(classes[best][0], classes[best][1]):
                    best = i
            classes[best][0] += 1
            classes[best][1] += 1
        return round(sum(p / t for p, t in classes) / len(classes), 5)
```
- **Time:** O(extraStudents · n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep classes sorted by marginal gain using a sorted list, reinserting the updated class each time.
```python
class Solution:
    def maxAverageRatio(self, classes: list[list[int]], extraStudents: int) -> float:
        import bisect
        def gain(p, t):
            return (p + 1) / (t + 1) - p / t
        ordered = []
        for idx, (p, t) in enumerate(classes):
            bisect.insort(ordered, (gain(p, t), idx, p, t))
        for _ in range(extraStudents):
            _, idx, p, t = ordered.pop()
            p += 1
            t += 1
            bisect.insort(ordered, (gain(p, t), idx, p, t))
        return round(sum(p / t for _, _, p, t in ordered) / len(classes), 5)
```
- **Time:** O((n + extraStudents) n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a max-heap keyed by marginal gain; after assigning one student, push the class back with its new gain.
```python
class Solution:
    def maxAverageRatio(self, classes: list[list[int]], extraStudents: int) -> float:
        import heapq
        def gain(p, t):
            return (p + 1) / (t + 1) - p / t
        heap = [(-gain(p, t), p, t) for p, t in classes]
        heapq.heapify(heap)
        for _ in range(extraStudents):
            _, p, t = heapq.heappop(heap)
            p += 1
            t += 1
            heapq.heappush(heap, (-gain(p, t), p, t))
        return round(sum(p / t for _, p, t in heap) / len(classes), 5)
```
- **Time:** O((n + extraStudents) log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(extraStudents · n) | O(n) |
| Better | O((n + extraStudents) n) | O(n) |
| Optimal | O((n + extraStudents) log n) | O(n) |

## Edge Cases & Pitfalls
- Compare marginal gain, not current ratio.
- A perfect class has zero gain but can remain in the heap.
- Round only the final average.

## Related
- Minimum Cost to Hire K Workers
- IPO
