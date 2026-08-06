# 02. Online Stock Span as a Batch

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
Given daily stock `prices`, return an array where `answer[i]` is the number of consecutive days ending at `i` whose price is less than or equal to `prices[i]`. This is the classic stock-span problem expressed as an array-in/array-out function, not a class API. Constraints: `1 <= len(prices) <= 10^5`, `1 <= prices[i] <= 10^5`.

## Examples
```text
Input: prices = [100,80,60,70,60,75,85]
Output: [1,1,1,2,1,4,6]
Explanation: At price 75, the span covers 75, 60, 70, and 60.
```

## Understanding & Intuition
Each span stops at the nearest previous strictly greater price. Rechecking days is simple but repetitive. A decreasing stack stores only prices that can stop future spans.

## Approach 1 — Naive / Brute Force
**Idea:** Walk left from every day while prices are no larger than today's price.
```python
from typing import List

class Solution:
    def calculateSpan(self, prices: List[int]) -> List[int]:
        spans = []
        for i, price in enumerate(prices):
            span = 1
            j = i - 1
            while j >= 0 and prices[j] <= price:
                span += 1
                j -= 1
            spans.append(span)
        return spans
```
- **Time:** O(n^2) — **Space:** O(1) excluding output

## Approach 2 — Better
**Idea:** Reuse already computed spans to jump over blocks of smaller or equal prices.
```python
from typing import List

class Solution:
    def calculateSpan(self, prices: List[int]) -> List[int]:
        spans = [1] * len(prices)
        for i in range(len(prices)):
            j = i - 1
            while j >= 0 and prices[j] <= prices[i]:
                spans[i] += spans[j]
                j -= spans[j]
        return spans
```
- **Time:** O(n^2) worst case — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep indexes of previous strictly greater prices in a decreasing stack.
```python
from typing import List

class Solution:
    def calculateSpan(self, prices: List[int]) -> List[int]:
        stack = []
        spans = []
        for i, price in enumerate(prices):
            while stack and prices[stack[-1]] <= price:
                stack.pop()
            spans.append(i - stack[-1] if stack else i + 1)
            stack.append(i)
        return spans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Equal prices are included in the span.
- Return an array, not a stateful class design API.
- Strictly decreasing prices produce all spans equal to `1`.

## Related
- Daily Temperatures
- Next Greater Element
