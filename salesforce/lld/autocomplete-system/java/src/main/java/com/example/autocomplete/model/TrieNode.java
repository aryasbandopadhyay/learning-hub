package com.example.autocomplete.model;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * One node in the Trie.
 *
 * <p>Every edge is a character. A node becomes terminal when the path from the root forms a stored
 * term/sentence. Terminal nodes keep the full term and its accumulated frequency so collection does
 * not need to rebuild strings during DFS.
 */
public class TrieNode {

    // TreeMap gives deterministic traversal. Ranking is handled separately by the heap.
    private final Map<Character, TrieNode> children = new TreeMap<>();

    private boolean terminal;
    private String term;
    private int weight;

    /** Return the child for this character, creating it during insertion if absent. */
    public TrieNode child(char ch) {
        return children.computeIfAbsent(ch, ignored -> new TrieNode());
    }

    /** Return the child for this character during prefix walking, or null if the prefix breaks. */
    public TrieNode getChild(char ch) {
        return children.get(ch);
    }

    /** Child nodes used by DFS collection after the prefix node is found. */
    public Collection<TrieNode> children() {
        return children.values();
    }

    /** Mark this node as a complete stored term and increment its historical frequency. */
    public void addWeight(String term, int delta) {
        this.terminal = true;
        this.term = term;
        this.weight += delta;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public String getTerm() {
        return term;
    }

    public int getWeight() {
        return weight;
    }
}
