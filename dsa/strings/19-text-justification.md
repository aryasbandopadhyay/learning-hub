# 19. Text Justification

- **Difficulty:** Hard
- **Pattern:** Strings
- **Asked at:** Google, Meta, Amazon, Microsoft

## Problem
Given an array of words and an integer `maxWidth`, format the text so each line has exactly `maxWidth` characters and is fully justified. Pack as many words as possible on each line. For non-last lines, distribute spaces as evenly as possible; left slots get extra spaces. The last line is left-justified. Constraints: `1 <= len(words) <= 300`; `1 <= len(words[i]) <= 20`; `1 <= maxWidth <= 100`.

## Examples
```text
Input: words = ["This","is","an","example","of","text","justification."], maxWidth = 16
Output: ["This    is    an","example  of text","justification.  "]
Explanation: Each line has width 16 and spaces are distributed by the rules.
```

## Understanding & Intuition
The problem has two phases: choose which words fit on each line, then format that line. Space distribution depends on whether the line is last or contains one word. Extra spaces go to earlier gaps.

## Approach 1 — Naive / Brute Force
**Idea:** Build each line incrementally and append spaces one at a time round-robin across gaps.
```python
from typing import List

class Solution:
    def fullJustify(self, words: List[str], maxWidth: int) -> List[str]:
        result = []
        i = 0
        while i < len(words):
            line_words = []
            length = 0
            while i < len(words) and length + len(words[i]) + len(line_words) <= maxWidth:
                line_words.append(words[i])
                length += len(words[i])
                i += 1
            spaces = maxWidth - length
            if i == len(words) or len(line_words) == 1:
                line = ' '.join(line_words)
                result.append(line + ' ' * (maxWidth - len(line)))
            else:
                gaps = [''] * (len(line_words) - 1)
                gap = 0
                while spaces:
                    gaps[gap] += ' '
                    spaces -= 1
                    gap = (gap + 1) % len(gaps)
                line = ''.join(word + (gaps[j] if j < len(gaps) else '') for j, word in enumerate(line_words))
                result.append(line)
        return result
```
- **Time:** O(n * w) — **Space:** O(w)

## Approach 2 — Better
**Idea:** Compute base and extra spaces per gap using division.
```python
from typing import List

class Solution:
    def fullJustify(self, words: List[str], maxWidth: int) -> List[str]:
        result = []
        i = 0
        while i < len(words):
            j = i
            letters = 0
            while j < len(words) and letters + len(words[j]) + (j - i) <= maxWidth:
                letters += len(words[j])
                j += 1
            line_words = words[i:j]
            gaps = len(line_words) - 1
            if j == len(words) or gaps == 0:
                line = ' '.join(line_words)
                result.append(line + ' ' * (maxWidth - len(line)))
            else:
                total_spaces = maxWidth - letters
                base, extra = divmod(total_spaces, gaps)
                pieces = []
                for k, word in enumerate(line_words[:-1]):
                    pieces.append(word)
                    pieces.append(' ' * (base + (1 if k < extra else 0)))
                pieces.append(line_words[-1])
                result.append(''.join(pieces))
            i = j
        return result
```
- **Time:** O(n * w) — **Space:** O(w)

## Approach 3 — Optimal
**Idea:** Same greedy packing, but isolate line formatting in a helper for clear O(total output) construction.
```python
from typing import List

class Solution:
    def fullJustify(self, words: List[str], maxWidth: int) -> List[str]:
        def format_line(line_words: List[str], letters: int, is_last: bool) -> str:
            if is_last or len(line_words) == 1:
                line = ' '.join(line_words)
                return line + ' ' * (maxWidth - len(line))
            gaps = len(line_words) - 1
            base, extra = divmod(maxWidth - letters, gaps)
            parts = []
            for idx, word in enumerate(line_words[:-1]):
                parts.append(word)
                parts.append(' ' * (base + (idx < extra)))
            parts.append(line_words[-1])
            return ''.join(parts)

        result = []
        i = 0
        while i < len(words):
            j = i
            letters = 0
            while j < len(words) and letters + len(words[j]) + (j - i) <= maxWidth:
                letters += len(words[j])
                j += 1
            result.append(format_line(words[i:j], letters, j == len(words)))
            i = j
        return result
```
- **Time:** O(T) — **Space:** O(w)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * w) | O(w) |
| Better | O(n * w) | O(w) |
| Optimal | O(T) | O(w) |

## Edge Cases & Pitfalls
- Last line is left-justified, not fully justified.
- Lines with one word are left-justified.
- Extra spaces are assigned to left gaps first.

## Related
- Greedy
- String Formatting
- Word Wrap
