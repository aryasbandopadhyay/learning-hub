# 12. Remove Comments

- **Difficulty:** Medium
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given C++ source code as list `source`, remove all line comments `//...` and block comments `/*...*/`. Return the remaining non-empty lines in order. Comment markers inside comments do not nest.

Implement `Solution.removeComments` with the parameters below and return the requested value.

**Input**
- `source`: a `list[str]`; source-code lines in original order.

**Output**
- The remaining non-empty source lines in their original order after comments are removed.

## Constraints
- 1 <= source.length <= 100
- 0 <= source[i].length <= 80
- `source[i]` contains printable ASCII characters
- Block comments may span multiple lines; comment delimiters inside comments are ignored

## Examples
```text
Input: source = ["/*Test program */", "int main()", "{ ", "// variable", "int a, b;", "a = b + c;", "}"]
Output: ["int main()","{ ","int a, b;","a = b + c;","}"]
Explanation: Comments are removed and empty lines are omitted. The result is shown in the required order.
```

## Understanding & Intuition
There are two states: normal code and inside a block comment. Line comments discard the rest of a line only in normal state, while block comments may span lines.

## Approach 1 — Naive / Brute Force
**Idea:** Scan each line character by character while carrying block state.
```python
class Solution:
    def removeComments(self, source: list[str]) -> list[str]:
        ans=[]; block=False; buf=[]
        for line in source:
            if not block: buf=[]
            i=0
            while i<len(line):
                if block:
                    if line[i:i+2]=='*/': block=False; i+=2
                    else: i+=1
                elif line[i:i+2]=='//': break
                elif line[i:i+2]=='/*': block=True; i+=2
                else: buf.append(line[i]); i+=1
            if not block and buf: ans.append(''.join(buf))
        return ans
```
- **Time:** O(total characters) — **Space:** O(total output)

## Approach 2 — Better
**Idea:** Use an explicit state name and append characters only in code state.
```python
class Solution:
    def removeComments(self, source: list[str]) -> list[str]:
        out=[]; state='code'; cur=[]
        for line in source:
            if state=='code': cur=[]
            i=0
            while i<len(line):
                two=line[i:i+2]
                if state=='block':
                    if two=='*/': state='code'; i+=2
                    else: i+=1
                elif two=='/*': state='block'; i+=2
                elif two=='//': break
                else: cur.append(line[i]); i+=1
            if state=='code' and cur: out.append(''.join(cur))
        return out
```
- **Time:** O(total characters) — **Space:** O(total output)

## Approach 3 — Optimal
**Idea:** Treat the file as one newline-separated stream and split kept code afterward.
```python
class Solution:
    def removeComments(self, source: list[str]) -> list[str]:
        text='\n'.join(source); kept=[]; i=0; block=False
        while i<len(text):
            two=text[i:i+2]
            if block:
                if two=='*/': block=False; i+=2
                else: i+=1
            elif two=='/*': block=True; i+=2
            elif two=='//':
                while i<len(text) and text[i]!='\n': i+=1
            else: kept.append(text[i]); i+=1
        return [line for line in ''.join(kept).split('\n') if line]
```
- **Time:** O(total characters) — **Space:** O(total output)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(total characters) | O(total output) |
| Better | O(total characters) | O(total output) |
| Optimal | O(total characters) | O(total output) |

## Edge Cases & Pitfalls
- Block comments can span multiple lines.
- Comment delimiters inside comments are ignored.
- Empty output lines are removed.

## Related
- Parsing Boolean Expression
- Valid Number

