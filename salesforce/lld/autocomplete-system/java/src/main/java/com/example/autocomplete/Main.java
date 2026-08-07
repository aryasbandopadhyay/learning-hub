package com.example.autocomplete;

import com.example.autocomplete.service.AutocompleteSystem;

import java.util.List;

/**
 * Runnable deterministic demo: ingest a small corpus, query prefixes, and commit one interactive
 * sentence via the LC642-style input API.
 */
public class Main {

    public static void main(String[] args) {
        AutocompleteSystem autocomplete = new AutocompleteSystem(
                List.of("i love you", "island", "i love leetcode", "ironman"),
                List.of(5, 3, 2, 2));

        System.out.println("Suggestions for 'i': " + autocomplete.suggest("i", 3));
        System.out.println("Suggestions for 'i ': " + autocomplete.suggest("i ", 3));

        System.out.println("Interactive input 'i': " + autocomplete.input('i'));
        System.out.println("Interactive input ' ': " + autocomplete.input(' '));
        System.out.println("Interactive input 'a': " + autocomplete.input('a'));
        System.out.println("Interactive input '#': " + autocomplete.input('#'));

        System.out.println("Suggestions for 'i a': " + autocomplete.suggest("i a", 3));
    }
}
