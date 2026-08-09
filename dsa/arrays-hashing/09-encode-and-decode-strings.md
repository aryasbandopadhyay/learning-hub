# 09. Encode and Decode Strings

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Google, Meta, Amazon, Microsoft

## Problem
Design a codec for a list of strings. `encode(strs)` must convert the whole list into one string, and `decode(s)` must recover the original list exactly, even when strings contain delimiters or digits.

**Input**
- `strs`: a list of strings passed to `encode`.
- `s`: an encoded string previously produced by `encode`, passed to `decode`.

**Output**
- `decode(encode(strs))` must return the original list in the same order with every character unchanged. **This judge compares exactly**.

## Constraints
- `0 <= strs.length <= 200`
- `0 <= strs[i].length <= 200`
- `strs[i]` may contain any valid character.

## Examples
```text
Input: strs = ["lint","code","love","you"]
Output: ["lint","code","love","you"]
Explanation: After encoding and decoding, each of the four strings is recovered unchanged and in its original position.
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
