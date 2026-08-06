# 14. Text Justification

- **Difficulty:** Hard
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google, LeetCode

## Problem
Given an array of words and an integer `maxWidth`, format the text so each line has exactly `maxWidth` characters and is fully justified. Pack as many words as possible into each line. Extra spaces between words should be distributed as evenly as possible; if they cannot be divided evenly, the left slots receive more spaces. The last line and lines with one word are left-justified.

Constraints: `1 <= len(words) <= 300`; `1 <= len(words[i]) <= 20`; `len(words[i]) <= maxWidth <= 100`; words contain only English letters and symbols without spaces.

## Examples
```text
Input: words = ["This", "is", "an", "example", "of", "text", "justification."], maxWidth = 16
Output: ["This    is    an", "example  of text", "justification.  "]
Explanation: Spaces are spread evenly in non-last lines, with extra spaces assigned from the left.
```

## Understanding & Intuition
The greedy choice is to place as many words as fit on the current line. Once a line's words are fixed, formatting is mechanical: last or single-word lines are left-justified, while other lines distribute all missing spaces across gaps. The output is unique because extra spaces always go to the leftmost gaps.

## Approach 1 — Naive / Brute Force
**Idea:** Greedily choose each line and build the result string by repeated concatenation.
```python
class Solution:
    def fullJustify(self, words: list[str], maxWidth: int) -> list[str]:
        ans = []
        i = 0
        n = len(words)
        while i < n:
            j = i
            letters = 0
            while j < n and letters + len(words[j]) + (j - i) <= maxWidth:
                letters += len(words[j])
                j += 1
            gaps = j - i - 1
            if j == n or gaps == 0:
                line = " ".join(words[i:j])
                line += " " * (maxWidth - len(line))
            else:
                spaces = maxWidth - letters
                base = spaces // gaps
                extra = spaces % gaps
                line = ""
                for k in range(i, j - 1):
                    line += words[k]
                    line += " " * (base + (1 if k - i < extra else 0))
                line += words[j - 1]
            ans.append(line)
            i = j
        return ans
```
- **Time:** O(n * maxWidth) — **Space:** O(n * maxWidth)

## Approach 2 — Better
**Idea:** First record each greedy line's word range and letter count, then format each recorded range using joined pieces.
```python
class Solution:
    def fullJustify(self, words: list[str], maxWidth: int) -> list[str]:
        groups = []
        i = 0
        n = len(words)
        while i < n:
            j = i
            letters = 0
            while j < n and letters + len(words[j]) + (j - i) <= maxWidth:
                letters += len(words[j])
                j += 1
            groups.append((i, j, letters))
            i = j
        ans = []
        for idx, (start, end, letters) in enumerate(groups):
            line_words = words[start:end]
            gaps = len(line_words) - 1
            if idx == len(groups) - 1 or gaps == 0:
                line = " ".join(line_words)
                ans.append(line + " " * (maxWidth - len(line)))
            else:
                total_spaces = maxWidth - letters
                base, extra = divmod(total_spaces, gaps)
                parts = []
                for offset, word in enumerate(line_words[:-1]):
                    parts.append(word)
                    parts.append(" " * (base + (1 if offset < extra else 0)))
                parts.append(line_words[-1])
                ans.append("".join(parts))
        return ans
```
- **Time:** O(n * maxWidth) — **Space:** O(n * maxWidth)

## Approach 3 — Optimal
**Idea:** Stream through words, flushing a line as soon as the next word would not fit.
```python
class Solution:
    def fullJustify(self, words: list[str], maxWidth: int) -> list[str]:
        ans = []
        line_words = []
        letters = 0
        for word in words:
            if line_words and letters + len(word) + len(line_words) > maxWidth:
                gaps = len(line_words) - 1
                if gaps == 0:
                    ans.append(line_words[0] + " " * (maxWidth - letters))
                else:
                    total_spaces = maxWidth - letters
                    base, extra = divmod(total_spaces, gaps)
                    line = []
                    for i, w in enumerate(line_words[:-1]):
                        line.append(w)
                        line.append(" " * (base + (1 if i < extra else 0)))
                    line.append(line_words[-1])
                    ans.append("".join(line))
                line_words = []
                letters = 0
            line_words.append(word)
            letters += len(word)
        last = " ".join(line_words)
        ans.append(last + " " * (maxWidth - len(last)))
        return ans
```
- **Time:** O(n * maxWidth) — **Space:** O(n * maxWidth)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * maxWidth) | O(n * maxWidth) |
| Better | O(n * maxWidth) | O(n * maxWidth) |
| Optimal | O(n * maxWidth) | O(n * maxWidth) |

## Edge Cases & Pitfalls
- Last lines are left-justified, not fully justified.
- Lines with one word get all trailing spaces at the end.
- Extra spaces in a fully justified line must go to the leftmost gaps.

## Related
- Add Bold Tag in String
- Reorder Spaces Between Words
