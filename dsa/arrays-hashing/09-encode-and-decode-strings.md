# 09. Encode and Decode Strings

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Google, Meta, Amazon, Microsoft

## Problem
Design `encode(strs)` and `decode(s)` so a list of arbitrary strings round-trips exactly, including empty strings and delimiter characters.

## Examples
```text
Input: strs = ["lint","code","love","you"]
Output: ["lint","code","love","you"]
Explanation: decode(encode(strs)) returns the original list.
```

## Understanding & Intuition
Safe serialization must know string boundaries. Delimiters need escaping, while length prefixes let the decoder read exact character counts.

## Approach 1 — Naive / Brute Force
**Idea:** Join with a delimiter; safe only if delimiter is absent.
```python
class Solution:
    def encode(self, strs: list[str]) -> str:
        return '|'.join(strs)

    def decode(self, s: str) -> list[str]:
        if s == '':
            return ['']
        return s.split('|')
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Escape delimiters and escape characters.
```python
class Solution:
    def encode(self, strs: list[str]) -> str:
        return '|'.join(w.replace('\\', '\\\\').replace('|', '\\|') for w in strs)

    def decode(self, s: str) -> list[str]:
        ans, cur, esc = [], [], False
        for ch in s:
            if esc:
                cur.append(ch); esc = False
            elif ch == '\\':
                esc = True
            elif ch == '|':
                ans.append(''.join(cur)); cur = []
            else:
                cur.append(ch)
        ans.append(''.join(cur))
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Prefix each string with its length and `#`.
```python
class Solution:
    def encode(self, strs: list[str]) -> str:
        return ''.join(str(len(w)) + '#' + w for w in strs)

    def decode(self, s: str) -> list[str]:
        ans = []
        i = 0
        while i < len(s):
            j = i
            while s[j] != '#':
                j += 1
            length = int(s[i:j])
            start = j + 1
            ans.append(s[start:start+length])
            i = start + length
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
- Naive delimiters fail if input contains them.
- Empty strings must round-trip.
- Length prefixes allow any content.

## Related
- Serialize and Deserialize Binary Tree
- String Compression
