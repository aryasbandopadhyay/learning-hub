# 04. Maximum Frequency Stack

- **Difficulty:** Hard
- **Pattern:** Stack / Hash Map
- **Asked at:** Salesforce, Amazon, Bloomberg

## Problem
Design `FreqStack` so `pop()` returns the most frequent value, breaking ties by most recent push.

## Examples
```text
Input: push(5), push(7), push(5), push(7), push(4), push(5), pop(), pop(), pop(), pop()
Output: [null,null,null,null,null,null,5,7,5,4]
Explanation: Frequency wins first, then recency.
```

## Understanding & Intuition
If every push of value `x` with new frequency `f` is placed onto stack `f`, then the highest nonempty frequency stack contains exactly the correct pop candidates in recency order.

## Approach 1 — Naive / Brute Force
**Idea:** Recount the stack on each pop and remove the best index.
```python
class FreqStack:
    def __init__(self):
        self.stack = []
    def push(self, val: int) -> None:
        self.stack.append(val)
    def pop(self) -> int:
        counts = {}; best_freq = 0; best_index = 0
        for i, val in enumerate(self.stack):
            counts[val] = counts.get(val, 0) + 1
            if counts[val] >= best_freq:
                best_freq = counts[val]; best_index = i
        return self.stack.pop(best_index)
```
- **Time:** O(n) pop, O(1) push — **Space:** O(n)

## Approach 2 — Better
**Idea:** Maintain counts but still scan from the end to find the most recent max-frequency value.
```python
class FreqStack:
    def __init__(self):
        self.stack = []; self.counts = {}; self.max_freq = 0
    def push(self, val: int) -> None:
        self.counts[val] = self.counts.get(val, 0) + 1
        self.max_freq = max(self.max_freq, self.counts[val]); self.stack.append(val)
    def pop(self) -> int:
        for i in range(len(self.stack) - 1, -1, -1):
            val = self.stack[i]
            if self.counts[val] == self.max_freq:
                self.stack.pop(i); self.counts[val] -= 1
                if all(f < self.max_freq for f in self.counts.values()): self.max_freq -= 1
                return val
        return -1
```
- **Time:** O(n) pop, O(1) push — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Group values by frequency and pop from the current maximum-frequency stack.
```python
class FreqStack:
    def __init__(self):
        self.counts = {}; self.groups = {}; self.max_freq = 0
    def push(self, val: int) -> None:
        freq = self.counts.get(val, 0) + 1
        self.counts[val] = freq; self.max_freq = max(self.max_freq, freq)
        self.groups.setdefault(freq, []).append(val)
    def pop(self) -> int:
        val = self.groups[self.max_freq].pop()
        self.counts[val] -= 1
        if not self.groups[self.max_freq]: self.max_freq -= 1
        return val
```
- **Time:** O(1) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) pop, O(1) push | O(n) |
| Better | O(n) pop, O(1) push | O(n) |
| Optimal | O(1) | O(n) |

## Edge Cases & Pitfalls
- Recency tie-breaking uses push order.
- Decrease `max_freq` when its stack empties.
- A value can exist in multiple frequency stacks over time.

## Related
- LFU Cache
- Top K Frequent Elements
