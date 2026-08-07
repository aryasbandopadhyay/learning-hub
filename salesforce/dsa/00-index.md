# Salesforce — 150 Most-Asked DSA Questions (Index)

This is a **curated index** of the 150 DSA questions most frequently reported in Salesforce
interviews (SDE / MTS / SMTS). The list is compiled from public LeetCode *Salesforce* company tags
(union of two community frequency sheets) plus widely-reported Salesforce classics.

**Salesforce DSA overlaps almost entirely with the FAANG/Google set** — so instead of duplicating
solutions, each question below **links to its existing full write-up** in this repo (understanding +
naive / better / optimal, with complexity). Only the questions that were *not* already covered
anywhere got brand-new solutions, authored in the same format under [`gaps/`](gaps/).

## Legend
| Marker | Meaning |
| --- | --- |
| ✅ | Already covered — links to the existing solution in the DSA / Google / FAANG banks. |
| 🆕 | New solution authored for this section (was a genuine gap). See [`gaps/`](gaps/). |
| 🧩 | A "design a data structure/system" question — built as a full LLD project. See the **LLD tab**. |

> Ordering: the first ~100 rows are ordered by reported Salesforce interview frequency (highest
> first); the remainder are high-frequency Salesforce-relevant classics. Difficulty is shown where a
> source provided it.

## How to use this list
1. Skim top-to-bottom — the highest-frequency questions are first.
2. Click any ✅ link to jump to the full multi-approach solution already in the repo.
3. The 🆕 rows are the only net-new DSA material; the 🧩 rows are covered on the LLD side.

---

| # | Question | Difficulty | Solution |
| --- | --- | --- | --- || 1 | LRU Cache | Medium | ✅ [`dsa/linked-list/08-lru-cache.md`](dsa/linked-list/08-lru-cache.md) |
| 2 | Reaching Points | Hard | 🆕 [`gaps/01-reaching-points.md`](gaps/01-reaching-points.md) |
| 3 | Letter Combinations of a Phone Number | Medium | ✅ [`dsa/backtracking/09-letter-combinations-of-a-phone-number.md`](dsa/backtracking/09-letter-combinations-of-a-phone-number.md) |
| 4 | Merge Intervals | Medium | ✅ [`dsa/intervals/02-merge-intervals.md`](dsa/intervals/02-merge-intervals.md) |
| 5 | K-diff Pairs in an Array | Easy | 🆕 [`gaps/02-k-diff-pairs-in-an-array.md`](gaps/02-k-diff-pairs-in-an-array.md) |
| 6 | Design HashMap | Easy | 🧩 LLD build → see **LLD tab** `salesforce/lld/custom-hashmap/` |
| 7 | Find the Smallest Divisor Given a Threshold | Medium | ✅ [`dsa/faang/binary-search-on-answer/08-find-the-smallest-divisor-given-a-threshold.md`](dsa/faang/binary-search-on-answer/08-find-the-smallest-divisor-given-a-threshold.md) |
| 8 | LFU Cache | Hard | 🆕 [`gaps/03-lfu-cache.md`](gaps/03-lfu-cache.md) |
| 9 | Implement Queue using Stacks | Easy | ✅ [`dsa/stack/15-implement-queue-using-stacks.md`](dsa/stack/15-implement-queue-using-stacks.md) |
| 10 | Design Search Autocomplete System | Hard | 🧩 LLD build → see **LLD tab** `salesforce/lld/autocomplete-system/` |
| 11 | Maximum Frequency Stack | Hard | 🆕 [`gaps/04-maximum-frequency-stack.md`](gaps/04-maximum-frequency-stack.md) |
| 12 | The Skyline Problem | Hard | ✅ [`dsa/faang/intervals-merge/01-the-skyline-problem.md`](dsa/faang/intervals-merge/01-the-skyline-problem.md) |
| 13 | Flatten 2D Vector | Medium | 🆕 [`gaps/05-flatten-2d-vector.md`](gaps/05-flatten-2d-vector.md) |
| 14 | Two Sum | Easy | ✅ [`dsa/arrays-hashing/01-two-sum.md`](dsa/arrays-hashing/01-two-sum.md) |
| 15 | Remove Duplicates from Sorted List II | Medium | 🆕 [`gaps/06-remove-duplicates-from-sorted-list-ii.md`](gaps/06-remove-duplicates-from-sorted-list-ii.md) |
| 16 | Design Tic-Tac-Toe | Medium | 🧩 Existing LLD → see **LLD tab** `tic-tac-toe/` |
| 17 | Shuffle an Array | Medium | 🆕 [`gaps/07-shuffle-an-array.md`](gaps/07-shuffle-an-array.md) |
| 18 | Word Ladder | Medium | ✅ [`dsa/graphs/13-word-ladder.md`](dsa/graphs/13-word-ladder.md) |
| 19 | Largest Number | Medium | 🆕 [`gaps/08-largest-number.md`](gaps/08-largest-number.md) |
| 20 | Number of Islands | Medium | ✅ [`dsa/graphs/01-number-of-islands.md`](dsa/graphs/01-number-of-islands.md) |
| 21 | Trapping Rain Water | Hard | ✅ [`dsa/two-pointers/06-trapping-rain-water.md`](dsa/two-pointers/06-trapping-rain-water.md) |
| 22 | Valid Parentheses | Easy | ✅ [`dsa/stack/01-valid-parentheses.md`](dsa/stack/01-valid-parentheses.md) |
| 23 | Find the Duplicate Number | Medium | ✅ [`dsa/arrays-hashing/19-find-the-duplicate-number.md`](dsa/arrays-hashing/19-find-the-duplicate-number.md) |
| 24 | Merge k Sorted Lists | Hard | ✅ [`dsa/revision/heap-priority-queue/08-merge-k-sorted-lists.md`](dsa/revision/heap-priority-queue/08-merge-k-sorted-lists.md) |
| 25 | Design Snake Game | Medium | 🆕 [`gaps/09-design-snake-game.md`](gaps/09-design-snake-game.md) |
| 26 | Insert Delete GetRandom O(1) | Medium | ✅ [`dsa/arrays-hashing/13-insert-delete-getrandom-o1.md`](dsa/arrays-hashing/13-insert-delete-getrandom-o1.md) |
| 27 | Lowest Common Ancestor of a Binary Tree | Medium | ✅ [`dsa/revision/trees/22-lowest-common-ancestor-of-a-binary-tree.md`](dsa/revision/trees/22-lowest-common-ancestor-of-a-binary-tree.md) |
| 28 | Remove Duplicates from Sorted List | Easy | ✅ [`dsa/linked-list/19-remove-duplicates-from-sorted-list.md`](dsa/linked-list/19-remove-duplicates-from-sorted-list.md) |
| 29 | Reverse Words in a String | Medium | ✅ [`dsa/strings/04-reverse-words-in-a-string.md`](dsa/strings/04-reverse-words-in-a-string.md) |
| 30 | Decode String | Medium | ✅ [`dsa/faang/stack-parsing/13-decode-string.md`](dsa/faang/stack-parsing/13-decode-string.md) |
| 31 | Longest Increasing Subsequence | Medium | ✅ [`dsa/dp-1d/11-longest-increasing-subsequence.md`](dsa/dp-1d/11-longest-increasing-subsequence.md) |
| 32 | Group Anagrams | Medium | ✅ [`dsa/arrays-hashing/04-group-anagrams.md`](dsa/arrays-hashing/04-group-anagrams.md) |
| 33 | Isomorphic Strings | Easy | ✅ [`dsa/strings/13-isomorphic-strings.md`](dsa/strings/13-isomorphic-strings.md) |
| 34 | Find Minimum in Rotated Sorted Array | Medium | ✅ [`dsa/binary-search/04-find-minimum-in-rotated-sorted-array.md`](dsa/binary-search/04-find-minimum-in-rotated-sorted-array.md) |
| 35 | Rotate Image | Medium | ✅ [`dsa/math-geometry/01-rotate-image.md`](dsa/math-geometry/01-rotate-image.md) |
| 36 | 3Sum | Medium | ✅ [`dsa/two-pointers/03-3sum.md`](dsa/two-pointers/03-3sum.md) |
| 37 | First Missing Positive | Hard | 🆕 [`gaps/10-first-missing-positive.md`](gaps/10-first-missing-positive.md) |
| 38 | Search in Rotated Sorted Array | Medium | ✅ [`dsa/binary-search/05-search-in-rotated-sorted-array.md`](dsa/binary-search/05-search-in-rotated-sorted-array.md) |
| 39 | Word Search II | Hard | ✅ [`dsa/tries/03-word-search-ii.md`](dsa/tries/03-word-search-ii.md) |
| 40 | Generate Parentheses | Medium | ✅ [`dsa/backtracking/12-generate-parentheses.md`](dsa/backtracking/12-generate-parentheses.md) |
| 41 | Find Pivot Index | Easy | 🆕 [`gaps/11-find-pivot-index.md`](gaps/11-find-pivot-index.md) |
| 42 | Search a 2D Matrix II | Medium | ✅ [`dsa/matrix/08-search-a-2d-matrix-ii.md`](dsa/matrix/08-search-a-2d-matrix-ii.md) |
| 43 | Candy | Hard | ✅ [`dsa/faang/greedy-scheduling/14-candy.md`](dsa/faang/greedy-scheduling/14-candy.md) |
| 44 | Top K Frequent Words | Medium | ✅ [`dsa/heap-priority-queue/09-top-k-frequent-words.md`](dsa/heap-priority-queue/09-top-k-frequent-words.md) |
| 45 | Serialize and Deserialize Binary Tree | Hard | ✅ [`dsa/revision/trees/15-serialize-and-deserialize-binary-tree.md`](dsa/revision/trees/15-serialize-and-deserialize-binary-tree.md) |
| 46 | Validate Binary Search Tree | Medium | ✅ [`dsa/trees/11-validate-binary-search-tree.md`](dsa/trees/11-validate-binary-search-tree.md) |
| 47 | Permutations | Medium | ✅ [`dsa/backtracking/04-permutations.md`](dsa/backtracking/04-permutations.md) |
| 48 | Minimum Window Substring | Hard | ✅ [`dsa/sliding-window/04-minimum-window-substring.md`](dsa/sliding-window/04-minimum-window-substring.md) |
| 49 | Kth Largest Element in an Array | Medium | ✅ [`dsa/heap-priority-queue/01-kth-largest-element-in-an-array.md`](dsa/heap-priority-queue/01-kth-largest-element-in-an-array.md) |
| 50 | Add and Search Word - Data structure design | Medium | 🆕 [`gaps/12-add-and-search-word-data-structure-design.md`](gaps/12-add-and-search-word-data-structure-design.md) |
| 51 | Invert Binary Tree | Easy | ✅ [`dsa/trees/01-invert-binary-tree.md`](dsa/trees/01-invert-binary-tree.md) |
| 52 | Find Median from Data Stream | Hard | ✅ [`dsa/heap-priority-queue/07-find-median-from-data-stream.md`](dsa/heap-priority-queue/07-find-median-from-data-stream.md) |
| 53 | Course Schedule | Medium | ✅ [`dsa/graphs/08-course-schedule.md`](dsa/graphs/08-course-schedule.md) |
| 54 | Maximum Subarray | Easy | ✅ [`dsa/greedy/08-maximum-subarray.md`](dsa/greedy/08-maximum-subarray.md) |
| 55 | Binary Tree Vertical Order Traversal | Medium | 🆕 [`gaps/13-binary-tree-vertical-order-traversal.md`](gaps/13-binary-tree-vertical-order-traversal.md) |
| 56 | Simplify Path | Medium | ✅ [`dsa/faang/stack-parsing/14-simplify-path.md`](dsa/faang/stack-parsing/14-simplify-path.md) |
| 57 | Fizz Buzz | Easy | 🆕 [`gaps/14-fizz-buzz.md`](gaps/14-fizz-buzz.md) |
| 58 | Product of Array Except Self | Medium | ✅ [`dsa/arrays-hashing/06-product-of-array-except-self.md`](dsa/arrays-hashing/06-product-of-array-except-self.md) |
| 59 | Word Break | Medium | ✅ [`dsa/dp-1d/10-word-break.md`](dsa/dp-1d/10-word-break.md) |
| 60 | Add Two Numbers | Medium | ✅ [`dsa/linked-list/07-add-two-numbers.md`](dsa/linked-list/07-add-two-numbers.md) |
| 61 | Reverse Words in a String III | Easy | 🆕 [`gaps/15-reverse-words-in-a-string-iii.md`](gaps/15-reverse-words-in-a-string-iii.md) |
| 62 | Symmetric Tree | Easy | ✅ [`dsa/trees/19-symmetric-tree.md`](dsa/trees/19-symmetric-tree.md) |
| 63 | Friend Circles | Medium | 🆕 [`gaps/16-friend-circles-number-of-provinces.md`](gaps/16-friend-circles-number-of-provinces.md) |
| 64 | Integer to Roman | Medium | ✅ [`dsa/math-geometry/15-integer-to-roman.md`](dsa/math-geometry/15-integer-to-roman.md) |
| 65 | Valid Sudoku | Medium | ✅ [`dsa/arrays-hashing/07-valid-sudoku.md`](dsa/arrays-hashing/07-valid-sudoku.md) |
| 66 | Set Matrix Zeroes | Medium | ✅ [`dsa/math-geometry/03-set-matrix-zeroes.md`](dsa/math-geometry/03-set-matrix-zeroes.md) |
| 67 | Longest Substring Without Repeating Characters | Medium | ✅ [`dsa/sliding-window/01-longest-substring-without-repeating-characters.md`](dsa/sliding-window/01-longest-substring-without-repeating-characters.md) |
| 68 | Intersection of Two Arrays II | Easy | ✅ [`dsa/arrays-hashing/22-intersection-of-two-arrays-ii.md`](dsa/arrays-hashing/22-intersection-of-two-arrays-ii.md) |
| 69 | Check If a Number Is Majority Element in a Sorted Array | Easy | 🆕 [`gaps/17-check-if-a-number-is-majority-element-in-a-sorted-array.md`](gaps/17-check-if-a-number-is-majority-element-in-a-sorted-array.md) |
| 70 | Height Checker | Easy | 🆕 [`gaps/18-height-checker.md`](gaps/18-height-checker.md) |
| 71 | Check Whether Two Strings are Almost Equivalent | — | 🆕 [`gaps/19-check-whether-two-strings-are-almost-equivalent.md`](gaps/19-check-whether-two-strings-are-almost-equivalent.md) |
| 72 | Construct the Lexicographically Largest Valid Sequence | — | 🆕 [`gaps/20-construct-the-lexicographically-largest-valid-sequence.md`](gaps/20-construct-the-lexicographically-largest-valid-sequence.md) |
| 73 | Count Sorted Vowel Strings | — | 🆕 [`gaps/21-count-sorted-vowel-strings.md`](gaps/21-count-sorted-vowel-strings.md) |
| 74 | Least Number of Unique Integers after K Removals | — | 🆕 [`gaps/22-least-number-of-unique-integers-after-k-removals.md`](gaps/22-least-number-of-unique-integers-after-k-removals.md) |
| 75 | Count Good Nodes in Binary Tree | — | ✅ [`dsa/trees/10-count-good-nodes-in-binary-tree.md`](dsa/trees/10-count-good-nodes-in-binary-tree.md) |
| 76 | Maximum Number of Events That Can Be Attended | — | ✅ [`dsa/google/intervals/03-maximum-number-of-events-that-can-be-attended.md`](dsa/google/intervals/03-maximum-number-of-events-that-can-be-attended.md) |
| 77 | Minimum Absolute Difference | — | 🆕 [`gaps/23-minimum-absolute-difference.md`](gaps/23-minimum-absolute-difference.md) |
| 78 | Last Stone Weight | — | ✅ [`dsa/heap-priority-queue/03-last-stone-weight.md`](dsa/heap-priority-queue/03-last-stone-weight.md) |
| 79 | Boats to Save People | — | ✅ [`dsa/faang/arrays-two-pointers/14-boats-to-save-people.md`](dsa/faang/arrays-two-pointers/14-boats-to-save-people.md) |
| 80 | Daily Temperatures | — | ✅ [`dsa/google/stack-monotonic/10-daily-temperatures.md`](dsa/google/stack-monotonic/10-daily-temperatures.md) |
| 81 | Maximum Product of Three Numbers | — | 🆕 [`gaps/24-maximum-product-of-three-numbers.md`](gaps/24-maximum-product-of-three-numbers.md) |
| 82 | Design In-Memory File System | — | 🧩 LLD build → see **LLD tab** `salesforce/lld/in-memory-file-system/` |
| 83 | Teemo Attacking | — | ✅ [`dsa/faang/intervals-merge/15-teemo-attacking.md`](dsa/faang/intervals-merge/15-teemo-attacking.md) |
| 84 | Zuma Game | — | 🆕 [`gaps/25-zuma-game.md`](gaps/25-zuma-game.md) |
| 85 | Pacific Atlantic Water Flow | — | ✅ [`dsa/graphs/06-pacific-atlantic-water-flow.md`](dsa/graphs/06-pacific-atlantic-water-flow.md) |
| 86 | Flatten Nested List Iterator | — | 🆕 [`gaps/26-flatten-nested-list-iterator.md`](gaps/26-flatten-nested-list-iterator.md) |
| 87 | Remove Duplicate Letters | — | ✅ [`dsa/faang/strings-advanced/09-remove-duplicate-letters.md`](dsa/faang/strings-advanced/09-remove-duplicate-letters.md) |
| 88 | Integer to English Words | — | ✅ [`dsa/faang/strings-advanced/14-integer-to-english-words.md`](dsa/faang/strings-advanced/14-integer-to-english-words.md) |
| 89 | Missing Number | — | ✅ [`dsa/arrays-hashing/20-missing-number.md`](dsa/arrays-hashing/20-missing-number.md) |
| 90 | Shortest Word Distance | — | 🆕 [`gaps/27-shortest-word-distance.md`](gaps/27-shortest-word-distance.md) |
| 91 | Sliding Window Maximum | — | ✅ [`dsa/sliding-window/05-sliding-window-maximum.md`](dsa/sliding-window/05-sliding-window-maximum.md) |
| 92 | Maximal Square | — | ✅ [`dsa/dp-2d/15-maximal-square.md`](dsa/dp-2d/15-maximal-square.md) |
| 93 | Course Schedule II | — | ✅ [`dsa/graphs/09-course-schedule-ii.md`](dsa/graphs/09-course-schedule-ii.md) |
| 94 | Rotate Array | — | 🆕 [`gaps/28-rotate-array.md`](gaps/28-rotate-array.md) |
| 95 | Binary Search Tree Iterator | — | 🆕 [`gaps/29-binary-search-tree-iterator.md`](gaps/29-binary-search-tree-iterator.md) |
| 96 | Min Stack | — | ✅ [`dsa/stack/02-min-stack.md`](dsa/stack/02-min-stack.md) |
| 97 | Best Time to Buy and Sell Stock | — | ✅ [`dsa/arrays-hashing/18-best-time-to-buy-and-sell-stock.md`](dsa/arrays-hashing/18-best-time-to-buy-and-sell-stock.md) |
| 98 | Flatten Binary Tree to Linked List | — | ✅ [`dsa/trees/23-flatten-binary-tree-to-linked-list.md`](dsa/trees/23-flatten-binary-tree-to-linked-list.md) |
| 99 | Sort Colors | — | ✅ [`dsa/arrays-hashing/14-sort-colors.md`](dsa/arrays-hashing/14-sort-colors.md) |
| 100 | Search a 2D Matrix | — | ✅ [`dsa/binary-search/02-search-a-2d-matrix.md`](dsa/binary-search/02-search-a-2d-matrix.md) |
| 101 | Jump Game | — | ✅ [`dsa/greedy/01-jump-game.md`](dsa/greedy/01-jump-game.md) |
| 102 | Sudoku Solver | — | ✅ [`dsa/revision/backtracking/14-sudoku-solver.md`](dsa/revision/backtracking/14-sudoku-solver.md) |
| 103 | Merge Two Sorted Lists | — | ✅ [`dsa/linked-list/02-merge-two-sorted-lists.md`](dsa/linked-list/02-merge-two-sorted-lists.md) |
| 104 | Reverse Linked List | — | ✅ [`dsa/linked-list/01-reverse-linked-list.md`](dsa/linked-list/01-reverse-linked-list.md) |
| 105 | Climbing Stairs | — | ✅ [`dsa/dp-1d/01-climbing-stairs.md`](dsa/dp-1d/01-climbing-stairs.md) |
| 106 | Coin Change | — | ✅ [`dsa/dp-1d/08-coin-change.md`](dsa/dp-1d/08-coin-change.md) |
| 107 | Top K Frequent Elements | — | ✅ [`dsa/arrays-hashing/05-top-k-frequent-elements.md`](dsa/arrays-hashing/05-top-k-frequent-elements.md) |
| 108 | Valid Anagram | — | ✅ [`dsa/arrays-hashing/03-valid-anagram.md`](dsa/arrays-hashing/03-valid-anagram.md) |
| 109 | Contains Duplicate | — | ✅ [`dsa/arrays-hashing/02-contains-duplicate.md`](dsa/arrays-hashing/02-contains-duplicate.md) |
| 110 | Container With Most Water | — | ✅ [`dsa/two-pointers/05-container-with-most-water.md`](dsa/two-pointers/05-container-with-most-water.md) |
| 111 | Longest Palindromic Substring | — | ✅ [`dsa/dp-1d/05-longest-palindromic-substring.md`](dsa/dp-1d/05-longest-palindromic-substring.md) |
| 112 | Longest Common Subsequence | — | ✅ [`dsa/dp-2d/04-longest-common-subsequence.md`](dsa/dp-2d/04-longest-common-subsequence.md) |
| 113 | Clone Graph | — | ✅ [`dsa/revision/graphs/03-clone-graph.md`](dsa/revision/graphs/03-clone-graph.md) |
| 114 | Rotting Oranges | — | ✅ [`dsa/graphs/05-rotting-oranges.md`](dsa/graphs/05-rotting-oranges.md) |
| 115 | Binary Tree Level Order Traversal | — | ✅ [`dsa/trees/08-binary-tree-level-order-traversal.md`](dsa/trees/08-binary-tree-level-order-traversal.md) |
| 116 | Kth Smallest Element In A Bst | — | ✅ [`dsa/trees/12-kth-smallest-element-in-a-bst.md`](dsa/trees/12-kth-smallest-element-in-a-bst.md) |
| 117 | Construct Binary Tree From Preorder And Inorder Traversal | — | ✅ [`dsa/trees/13-construct-binary-tree-from-preorder-and-inorder-traversal.md`](dsa/trees/13-construct-binary-tree-from-preorder-and-inorder-traversal.md) |
| 118 | Implement Trie Prefix Tree | — | ✅ [`dsa/tries/01-implement-trie-prefix-tree.md`](dsa/tries/01-implement-trie-prefix-tree.md) |
| 119 | Word Search | — | ✅ [`dsa/backtracking/07-word-search.md`](dsa/backtracking/07-word-search.md) |
| 120 | Combination Sum | — | ✅ [`dsa/backtracking/02-combination-sum.md`](dsa/backtracking/02-combination-sum.md) |
| 121 | Subsets | — | ✅ [`dsa/backtracking/01-subsets.md`](dsa/backtracking/01-subsets.md) |
| 122 | Insert Interval | — | ✅ [`dsa/intervals/01-insert-interval.md`](dsa/intervals/01-insert-interval.md) |
| 123 | Non Overlapping Intervals | — | ✅ [`dsa/greedy/12-non-overlapping-intervals.md`](dsa/greedy/12-non-overlapping-intervals.md) |
| 124 | Meeting Rooms | — | ✅ [`dsa/intervals/04-meeting-rooms.md`](dsa/intervals/04-meeting-rooms.md) |
| 125 | Meeting Rooms Ii | — | ✅ [`dsa/intervals/05-meeting-rooms-ii.md`](dsa/intervals/05-meeting-rooms-ii.md) |
| 126 | Koko Eating Bananas | — | ✅ [`dsa/binary-search/03-koko-eating-bananas.md`](dsa/binary-search/03-koko-eating-bananas.md) |
| 127 | Time Based Key Value Store | — | ✅ [`dsa/binary-search/06-time-based-key-value-store.md`](dsa/binary-search/06-time-based-key-value-store.md) |
| 128 | Car Fleet | — | ✅ [`dsa/stack/06-car-fleet.md`](dsa/stack/06-car-fleet.md) |
| 129 | Largest Rectangle In Histogram | — | ✅ [`dsa/stack/07-largest-rectangle-in-histogram.md`](dsa/stack/07-largest-rectangle-in-histogram.md) |
| 130 | Evaluate Reverse Polish Notation | — | ✅ [`dsa/stack/03-evaluate-reverse-polish-notation.md`](dsa/stack/03-evaluate-reverse-polish-notation.md) |
| 131 | Task Scheduler | — | ✅ [`dsa/faang/greedy-scheduling/13-task-scheduler.md`](dsa/faang/greedy-scheduling/13-task-scheduler.md) |
| 132 | Reorder List | — | ✅ [`dsa/linked-list/04-reorder-list.md`](dsa/linked-list/04-reorder-list.md) |
| 133 | Copy List With Random Pointer | — | ✅ [`dsa/revision/linked-list/06-copy-list-with-random-pointer.md`](dsa/revision/linked-list/06-copy-list-with-random-pointer.md) |
| 134 | Remove Nth Node From End Of List | — | ✅ [`dsa/linked-list/05-remove-nth-node-from-end-of-list.md`](dsa/linked-list/05-remove-nth-node-from-end-of-list.md) |
| 135 | Linked List Cycle | — | ✅ [`dsa/revision/linked-list/03-linked-list-cycle.md`](dsa/revision/linked-list/03-linked-list-cycle.md) |
| 136 | Reverse Nodes In K Group | — | ✅ [`dsa/linked-list/10-reverse-nodes-in-k-group.md`](dsa/linked-list/10-reverse-nodes-in-k-group.md) |
| 137 | Spiral Matrix | — | ✅ [`dsa/math-geometry/02-spiral-matrix.md`](dsa/math-geometry/02-spiral-matrix.md) |
| 138 | Maximum Product Subarray | — | ✅ [`dsa/dp-1d/09-maximum-product-subarray.md`](dsa/dp-1d/09-maximum-product-subarray.md) |
| 139 | House Robber | — | ✅ [`dsa/dp-1d/03-house-robber.md`](dsa/dp-1d/03-house-robber.md) |
| 140 | House Robber Ii | — | ✅ [`dsa/dp-1d/04-house-robber-ii.md`](dsa/dp-1d/04-house-robber-ii.md) |
| 141 | Decode Ways | — | ✅ [`dsa/dp-1d/07-decode-ways.md`](dsa/dp-1d/07-decode-ways.md) |
| 142 | Unique Paths | — | ✅ [`dsa/dp-2d/01-unique-paths.md`](dsa/dp-2d/01-unique-paths.md) |
| 143 | Partition Equal Subset Sum | — | ✅ [`dsa/dp-1d/12-partition-equal-subset-sum.md`](dsa/dp-1d/12-partition-equal-subset-sum.md) |
| 144 | Edit Distance | — | ✅ [`dsa/dp-2d/11-edit-distance.md`](dsa/dp-2d/11-edit-distance.md) |
| 145 | Number Of Connected Components In An Undirected Graph | — | ✅ [`dsa/graphs/11-number-of-connected-components-in-an-undirected-graph.md`](dsa/graphs/11-number-of-connected-components-in-an-undirected-graph.md) |
| 146 | Graph Valid Tree | — | ✅ [`dsa/graphs/10-graph-valid-tree.md`](dsa/graphs/10-graph-valid-tree.md) |
| 147 | Min Cost To Connect All Points | — | ✅ [`dsa/advanced-graphs/04-min-cost-to-connect-all-points.md`](dsa/advanced-graphs/04-min-cost-to-connect-all-points.md) |
| 148 | Network Delay Time | — | ✅ [`dsa/advanced-graphs/01-network-delay-time.md`](dsa/advanced-graphs/01-network-delay-time.md) |
| 149 | Single Number | — | ✅ [`dsa/bit-manipulation/01-single-number.md`](dsa/bit-manipulation/01-single-number.md) |
| 150 | Sum Of Two Integers | — | ✅ [`dsa/bit-manipulation/06-sum-of-two-integers.md`](dsa/bit-manipulation/06-sum-of-two-integers.md) |

---

## Coverage summary
- **150** most-asked Salesforce DSA questions indexed.
- **~118** already covered in the existing `dsa/` (incl. `google/`, `faang/`) banks → linked above.
- **29** genuine gaps → new full solutions under [`gaps/`](gaps/).
- **3** "design a data structure" questions (Design HashMap, Design In-Memory File System, Design
  Search Autocomplete System) → built as complete LLD projects; see the **LLD tab**.

## Related sections
- **DSA** tab — the full FAANG question bank these link into.
- **Most Asked Google** / **Most Asked FAANG** tabs — company-focused sets.
- **Salesforce LLD** (below in this section) — the machine-coding / design half of the loop.
