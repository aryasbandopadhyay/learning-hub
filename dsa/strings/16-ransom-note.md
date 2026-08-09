# 16. Ransom Note

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given `ransomNote` and `magazine`, return whether the note can be built using letters from the magazine. Each magazine character may be used at most once.

**Input**
- `ransomNote`: the target text.
- `magazine`: available characters.

**Output**
- A boolean: `True` if all note characters are available with enough frequency, otherwise `False`.

## Constraints
- `1 <= ransomNote.length, magazine.length <= 10^5`
- Both strings contain lowercase English letters.

## Examples
```text
Input: ransomNote = "aa", magazine = "aab"
Output: true
Explanation: The magazine contains two `a` characters, enough to form `aa`.
```

## Understanding & Intuition
This is a frequency availability problem. The magazine provides counts, and the note consumes them. If any needed character count goes negative, construction is impossible.

## Approach 1 — Naive / Brute Force
**Idea:** For each note character, search and remove a matching magazine character.
```python
class Solution:
    def canConstruct(self, ransomNote: str, magazine: str) -> bool:
        letters = list(magazine)
        for ch in ransomNote:
            if ch not in letters:
                return False
            letters.remove(ch)
        return True
```
- **Time:** O(n * m) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Count magazine characters with a dictionary and decrement for the note.
```python
from collections import Counter

class Solution:
    def canConstruct(self, ransomNote: str, magazine: str) -> bool:
        available = Counter(magazine)
        for ch in ransomNote:
            if available[ch] == 0:
                return False
            available[ch] -= 1
        return True
```
- **Time:** O(n + m) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use a fixed lowercase-letter count array for lower overhead.
```python
class Solution:
    def canConstruct(self, ransomNote: str, magazine: str) -> bool:
        counts = [0] * 26
        for ch in magazine:
            counts[ord(ch) - ord('a')] += 1
        for ch in ransomNote:
            idx = ord(ch) - ord('a')
            counts[idx] -= 1
            if counts[idx] < 0:
                return False
        return True
```
- **Time:** O(n + m) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * m) | O(m) |
| Better | O(n + m) | O(1) |
| Optimal | O(n + m) | O(1) |

## Edge Cases & Pitfalls
- Magazine characters cannot be reused.
- If the note is longer than the magazine, it may still be checked but cannot succeed unless constraints differ.
- Space is constant only because there are 26 lowercase letters.

## Related
- Valid Anagram
- First Unique Character in a String
- Counting
