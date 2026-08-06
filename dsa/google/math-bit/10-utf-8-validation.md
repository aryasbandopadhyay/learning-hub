# 10. UTF-8 Validation

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given a list of integers `data`, where each integer represents one byte, return whether it is a valid UTF-8 encoding. A character may be 1 to 4 bytes long. Continuation bytes must have leading bits `10`, and leading bytes determine the number of required continuation bytes. Constraints: `1 <= len(data) <= 2 * 10^4` and `0 <= data[i] <= 255`.

## Examples
```text
Input: data = [240, 162, 138, 147, 17]
Output: True
Explanation: The first four bytes form one valid 4-byte character and 17 is a valid 1-byte character.
```

## Understanding & Intuition
UTF-8 validity depends on leading bit patterns. A start byte beginning with `0`, `110`, `1110`, or `11110` determines how many continuation bytes must follow. Every continuation byte must begin with binary `10`.

## Approach 1 — Naive / Brute Force
**Idea:** Convert each byte to an 8-character binary string and validate prefixes directly.
```python
class Solution:
    def validUtf8(self, data: list[int]) -> bool:
        bits = [format(x, "08b") for x in data]
        i = 0
        while i < len(bits):
            b = bits[i]
            if b[0] == "0":
                need = 0
            elif b.startswith("110"):
                need = 1
            elif b.startswith("1110"):
                need = 2
            elif b.startswith("11110"):
                need = 3
            else:
                return False
            if i + need >= len(bits):
                return False
            for j in range(i + 1, i + need + 1):
                if not bits[j].startswith("10"):
                    return False
            i += need + 1
        return True
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count leading one bits arithmetically for start bytes, then check continuation-byte masks.
```python
class Solution:
    def validUtf8(self, data: list[int]) -> bool:
        i = 0
        while i < len(data):
            byte = data[i]
            mask = 128
            leading = 0
            while byte & mask:
                leading += 1
                mask >>= 1
            if leading == 0:
                i += 1
                continue
            if leading == 1 or leading > 4 or i + leading > len(data):
                return False
            for j in range(i + 1, i + leading):
                if (data[j] & 192) != 128:
                    return False
            i += leading
        return True
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Maintain the number of expected continuation bytes while scanning once.
```python
class Solution:
    def validUtf8(self, data: list[int]) -> bool:
        remaining = 0
        for byte in data:
            if remaining:
                if (byte >> 6) != 0b10:
                    return False
                remaining -= 1
            else:
                if (byte >> 7) == 0:
                    remaining = 0
                elif (byte >> 5) == 0b110:
                    remaining = 1
                elif (byte >> 4) == 0b1110:
                    remaining = 2
                elif (byte >> 3) == 0b11110:
                    remaining = 3
                else:
                    return False
        return remaining == 0
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A byte starting with `10` cannot start a character.
- Five-byte leading patterns are invalid in modern UTF-8.
- The scan must end with no pending continuation bytes.

## Related
- Reverse Bits
- Number of 1 Bits
