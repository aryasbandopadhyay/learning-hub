# 09. Letter Combinations of a Phone Number

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Meta, Apple

## Problem
Given a string `digits` containing digits from `2` to `9`, return all possible letter combinations the number could represent on a phone keypad. `0 <= len(digits) <= 4`. Return an empty list for an empty input.

## Examples
```text
Input: digits = "23"
Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
Explanation: 2 maps to abc and 3 maps to def.
```

## Understanding & Intuition
Each digit contributes one character choice. The recursion depth equals the number of digits. When a path reaches that depth, it forms one complete combination.

## Approach 1 — Naive / Brute Force
**Idea:** Build the Cartesian product iteratively by appending every letter for each digit.
```python
from typing import List

class Solution:
    def letterCombinations(self, digits: str) -> List[str]:
        if not digits:
            return []
        phone = {"2": "abc", "3": "def", "4": "ghi", "5": "jkl",
                 "6": "mno", "7": "pqrs", "8": "tuv", "9": "wxyz"}
        result = [""]
        for digit in digits:
            next_result = []
            for prefix in result:
                for ch in phone[digit]:
                    next_result.append(prefix + ch)
            result = next_result
        return result
```
- **Time:** O(4^n * n) — **Space:** O(4^n * n)

## Approach 2 — Better
**Idea:** Use backtracking with a mutable path instead of repeatedly creating intermediate prefix lists.
```python
from typing import List

class Solution:
    def letterCombinations(self, digits: str) -> List[str]:
        if not digits:
            return []
        phone = {"2": "abc", "3": "def", "4": "ghi", "5": "jkl",
                 "6": "mno", "7": "pqrs", "8": "tuv", "9": "wxyz"}
        result = []
        path = []

        def backtrack(i: int) -> None:
            if i == len(digits):
                result.append("".join(path))
                return
            for ch in phone[digits[i]]:
                path.append(ch)
                backtrack(i + 1)
                path.pop()

        backtrack(0)
        return result
```
- **Time:** O(4^n * n) — **Space:** O(n) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Preallocate a character array of length `n`, assigning each position directly during DFS.
```python
from typing import List

class Solution:
    def letterCombinations(self, digits: str) -> List[str]:
        if not digits:
            return []
        phone = {"2": "abc", "3": "def", "4": "ghi", "5": "jkl",
                 "6": "mno", "7": "pqrs", "8": "tuv", "9": "wxyz"}
        result = []
        chars = [""] * len(digits)

        def dfs(i: int) -> None:
            if i == len(digits):
                result.append("".join(chars))
                return
            for ch in phone[digits[i]]:
                chars[i] = ch
                dfs(i + 1)

        dfs(0)
        return result
```
- **Time:** O(4^n * n) — **Space:** O(n) auxiliary plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(4^n * n) | O(4^n * n) |
| Better | O(4^n * n) | O(n) plus output |
| Optimal | O(4^n * n) | O(n) plus output |

## Edge Cases & Pitfalls
- Empty `digits` returns `[]`, not `[""]`.
- Digits `7` and `9` have four letters.
- Results order follows keypad order.

## Related
- Permutations
- Generate Parentheses
- Combinations
