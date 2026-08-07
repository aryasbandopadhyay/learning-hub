package com.example.autocomplete.service;

import com.example.autocomplete.model.RankedTerm;
import com.example.autocomplete.model.TrieNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Application service for Trie-backed autocomplete.
 *
 * <p>The MVP is intentionally single-threaded, matching the common LeetCode 642 object model where
 * {@link #input(char)} owns mutable in-progress sentence state. A production typeahead service would
 * wrap Trie updates/lookups in a read-write lock and keep input buffers per user/session.
 */
public class AutocompleteSystem {

    private static final int DEFAULT_INTERACTIVE_LIMIT = 3;

    // Public ranking: higher frequency is better; for ties, lexicographically smaller is better.
    private static final Comparator<RankedTerm> BEST_FIRST = Comparator
            .comparingInt(RankedTerm::weight).reversed()
            .thenComparing(RankedTerm::term);

    // Heap ranking: the worst candidate sits at the root so overflow can evict it in O(log k).
    private static final Comparator<RankedTerm> WORST_FIRST = Comparator
            .comparingInt(RankedTerm::weight)
            .thenComparing(RankedTerm::term, Comparator.reverseOrder());

    private final TrieNode root = new TrieNode();
    private final StringBuilder currentInput = new StringBuilder();

    public AutocompleteSystem() {
    }

    public AutocompleteSystem(List<String> terms, List<Integer> weights) {
        if (terms.size() != weights.size()) {
            throw new IllegalArgumentException("terms and weights must have the same size");
        }
        for (int i = 0; i < terms.size(); i++) {
            addTerm(terms.get(i), weights.get(i));
        }
    }

    /** Insert a new term or increment the frequency of an existing one. */
    public void addTerm(String term, int weight) {
        if (term == null || term.isEmpty()) {
            throw new IllegalArgumentException("term must not be empty");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("weight must be positive");
        }

        TrieNode node = root;
        for (char ch : term.toCharArray()) {
            node = node.child(ch); // create one Trie edge per character.
        }
        node.addWeight(term, weight); // terminal node owns the accumulated frequency.
    }

    /** Return top-k terms sharing the prefix, ranked by frequency desc then lexicographic asc. */
    public List<String> suggest(String prefix, int k) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }
        if (k <= 0) {
            return List.of();
        }

        TrieNode node = walkPrefix(prefix);
        if (node == null) {
            return List.of();
        }

        PriorityQueue<RankedTerm> topK = new PriorityQueue<>(WORST_FIRST);
        collect(node, topK, k);

        List<RankedTerm> ranked = new ArrayList<>(topK);
        ranked.sort(BEST_FIRST); // PriorityQueue iteration order is not sorted, so sort once.
        return ranked.stream().map(RankedTerm::term).toList();
    }

    /** LC642-style streaming API: '#' commits the buffered sentence; other chars return suggestions. */
    public List<String> input(char ch) {
        if (ch == '#') {
            if (!currentInput.isEmpty()) {
                addTerm(currentInput.toString(), 1);
                currentInput.setLength(0);
            }
            return List.of();
        }
        currentInput.append(ch);
        return suggest(currentInput.toString(), DEFAULT_INTERACTIVE_LIMIT);
    }

    /** Walk the Trie from root to the node representing prefix; return null on the first miss. */
    private TrieNode walkPrefix(String prefix) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            node = node.getChild(ch);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /** DFS over the matching subtree, maintaining a bounded heap of the best k terminal nodes. */
    private void collect(TrieNode node, PriorityQueue<RankedTerm> topK, int k) {
        if (node.isTerminal()) {
            topK.offer(new RankedTerm(node.getTerm(), node.getWeight()));
            if (topK.size() > k) {
                topK.poll(); // evict the lowest-frequency / lexicographically largest candidate.
            }
        }
        for (TrieNode child : node.children()) {
            collect(child, topK, k);
        }
    }
}
