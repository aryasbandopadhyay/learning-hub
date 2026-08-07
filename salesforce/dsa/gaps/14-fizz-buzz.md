# 14. Fizz Buzz

- **Difficulty:** Easy
- **Pattern:** Simulation
- **Asked at:** Salesforce, Amazon, Microsoft

## Problem
Return strings from `1` to `n`, replacing multiples of 3 with `Fizz`, multiples of 5 with `Buzz`, and both with `FizzBuzz`.

## Examples
```text
Input: n = 5
Output: ["1","2","Fizz","4","Buzz"]
Explanation: 3 maps to Fizz and 5 maps to Buzz.
```

## Understanding & Intuition
Each output depends only on divisibility. Either check 15 first or compose the word from matching divisors.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct conditional branches.
```python
class Solution:
    def fizzBuzz(self, n: int) -> list[str]:
        ans = []
        for i in range(1, n + 1):
            if i % 15 == 0: ans.append("FizzBuzz")
            elif i % 3 == 0: ans.append("Fizz")
            elif i % 5 == 0: ans.append("Buzz")
            else: ans.append(str(i))
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Append matching words to a token.
```python
class Solution:
    def fizzBuzz(self, n: int) -> list[str]:
        ans = []
        for i in range(1, n + 1):
            word = ""
            if i % 3 == 0: word += "Fizz"
            if i % 5 == 0: word += "Buzz"
            ans.append(word or str(i))
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store rules as data for easy extension.
```python
class Solution:
    def fizzBuzz(self, n: int) -> list[str]:
        rules = ((3, "Fizz"), (5, "Buzz")); ans = []
        for i in range(1, n + 1):
            word = "".join(text for div, text in rules if i % div == 0)
            ans.append(word or str(i))
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Multiples of both produce `FizzBuzz`.
- Output elements are strings.
- LeetCode has `n >= 1`.

## Related
- Count Primes
- Number of Steps to Reduce a Number to Zero
