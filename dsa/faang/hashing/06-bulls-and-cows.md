# 06. Bulls and Cows

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Google, Microsoft, Amazon

## Problem
You are playing Bulls and Cows. Given two equal-length digit strings `secret` and `guess`, return a hint formatted as `"xAyB"`, where `x` is the number of bulls and `y` is the number of cows. A bull is a digit in the correct position. A cow is a correct digit in the wrong position, counted only after bull positions are removed.

Constraints: `1 <= len(secret) == len(guess) <= 10^5`, and both strings contain only digits.

## Examples
```text
Input: secret = "1807", guess = "7810"
Output: "1A3B"
Explanation: Digit 8 is a bull; digits 0, 1, and 7 are cows.
```

## Understanding & Intuition
Bulls are direct position matches and must be removed before counting cows. For the remaining positions, cows are the sum over digits of the minimum unmatched counts in `secret` and `guess`. A balance array can compute cows in one pass.

## Approach 1 — Naive / Brute Force
**Idea:** Mark bulls first, then for every unmatched guess digit scan for one unused equal secret digit.
```python
class Solution:
    def getHint(self, secret: str, guess: str) -> str:
        n = len(secret)
        bulls = 0
        used_secret = [False] * n
        used_guess = [False] * n
        for i in range(n):
            if secret[i] == guess[i]:
                bulls += 1
                used_secret[i] = True
                used_guess[i] = True
        cows = 0
        for i in range(n):
            if used_guess[i]:
                continue
            for j in range(n):
                if not used_secret[j] and guess[i] == secret[j]:
                    used_secret[j] = True
                    cows += 1
                    break
        return str(bulls) + "A" + str(cows) + "B"
```
- **Time:** O(n²) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count unmatched secret and guess digits separately, then add the overlap for each digit.
```python
class Solution:
    def getHint(self, secret, guess):
        bulls = 0
        secret_count = [0] * 10
        guess_count = [0] * 10
        for s, g in zip(secret, guess):
            if s == g:
                bulls += 1
            else:
                secret_count[ord(s) - ord('0')] += 1
                guess_count[ord(g) - ord('0')] += 1
        cows = sum(min(secret_count[i], guess_count[i]) for i in range(10))
        return str(bulls) + "A" + str(cows) + "B"
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Maintain a digit balance; when a new unmatched digit closes a previous deficit or surplus, it forms a cow.
```python
class Solution:
    def getHint(self, secret, guess):
        bulls = 0
        cows = 0
        balance = [0] * 10
        for s, g in zip(secret, guess):
            if s == g:
                bulls += 1
            else:
                si = ord(s) - ord('0')
                gi = ord(g) - ord('0')
                if balance[si] < 0:
                    cows += 1
                if balance[gi] > 0:
                    cows += 1
                balance[si] += 1
                balance[gi] -= 1
        return str(bulls) + "A" + str(cows) + "B"
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Do not count a digit as both a bull and a cow.
- Repeated digits require minimum unmatched counts.
- The return value must exactly match the `xAyB` string format.

## Related
- Valid Anagram
- Find All Anagrams in a String
