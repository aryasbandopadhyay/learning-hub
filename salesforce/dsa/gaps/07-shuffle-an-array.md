# 07. Shuffle an Array

- **Difficulty:** Medium
- **Pattern:** Randomized Algorithm
- **Asked at:** Salesforce, Amazon, Google

## Problem
Design a class that resets an array to its original configuration and returns a uniformly random shuffle.

## Examples
```text
Input: Solution([1,2,3]), shuffle(), reset()
Output: [null, [random permutation], [1,2,3]]
Explanation: Each permutation should be equally likely.
```

## Understanding & Intuition
Uniformity requires each remaining element to be equally likely at each position. Fisher-Yates does exactly that with swaps.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly pop a random remaining element.
```python
import random
class Solution:
    def __init__(self, nums: list[int]): self.original = nums[:]
    def reset(self) -> list[int]: return self.original[:]
    def shuffle(self) -> list[int]:
        pool = self.original[:]; ans = []
        while pool:
            ans.append(pool.pop(random.randrange(len(pool))))
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort elements by random keys.
```python
import random
class Solution:
    def __init__(self, nums: list[int]): self.original = nums[:]
    def reset(self) -> list[int]: return self.original[:]
    def shuffle(self) -> list[int]:
        return [x for _, x in sorted((random.random(), x) for x in self.original)]
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Fisher-Yates swap each index with a random index from the unshuffled suffix.
```python
import random
class Solution:
    def __init__(self, nums: list[int]): self.original = nums[:]
    def reset(self) -> list[int]: return self.original[:]
    def shuffle(self) -> list[int]:
        nums = self.original[:]
        for i in range(len(nums)):
            j = random.randrange(i, len(nums))
            nums[i], nums[j] = nums[j], nums[i]
        return nums
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Return copies from `reset()`.
- Pick `j` from `[i, n)` for Fisher-Yates.
- Do not mutate the stored original.

## Related
- Random Pick Index
- Random Pick with Weight
