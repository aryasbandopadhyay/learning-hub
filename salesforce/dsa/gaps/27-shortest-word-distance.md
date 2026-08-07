# 27. Shortest Word Distance

- **Difficulty:** Easy
- **Pattern:** Arrays / Two Pointers
- **Asked at:** Salesforce, LinkedIn, Amazon

## Problem
Given a list of words and two different target words, return the shortest index distance between them.

## Examples
```text
Input: wordsDict = ["practice","makes","perfect","coding","makes"], word1 = "coding", word2 = "practice"
Output: 3
Explanation: Indices 3 and 0 differ by 3.
```

## Understanding & Intuition
As we scan, only the most recent occurrence of each target can improve the minimum distance.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every occurrence of word1 with every occurrence of word2.
```python
class Solution:
    def shortestDistance(self, wordsDict: list[str], word1: str, word2: str) -> int:
        a = [i for i, w in enumerate(wordsDict) if w == word1]
        b = [i for i, w in enumerate(wordsDict) if w == word2]
        return min(abs(i - j) for i in a for j in b)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Merge sorted position lists with two pointers.
```python
class Solution:
    def shortestDistance(self, wordsDict: list[str], word1: str, word2: str) -> int:
        a = [i for i, w in enumerate(wordsDict) if w == word1]
        b = [i for i, w in enumerate(wordsDict) if w == word2]
        i = j = 0; best = len(wordsDict)
        while i < len(a) and j < len(b):
            best = min(best, abs(a[i] - b[j]))
            if a[i] < b[j]: i += 1
            else: j += 1
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan once and remember latest positions.
```python
class Solution:
    def shortestDistance(self, wordsDict: list[str], word1: str, word2: str) -> int:
        last1 = last2 = -1; best = len(wordsDict)
        for i, word in enumerate(wordsDict):
            if word == word1:
                last1 = i
                if last2 != -1: best = min(best, last1 - last2)
            elif word == word2:
                last2 = i
                if last1 != -1: best = min(best, last2 - last1)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- LC 243 has different target words.
- Update answer only after seeing both words.
- Distance is absolute index difference.

## Related
- Shortest Word Distance II
- Shortest Word Distance III
