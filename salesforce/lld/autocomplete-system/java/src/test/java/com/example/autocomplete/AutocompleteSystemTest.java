package com.example.autocomplete;

import com.example.autocomplete.service.AutocompleteSystem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutocompleteSystemTest {

    private AutocompleteSystem sampleSystem() {
        return new AutocompleteSystem(
                List.of("i love you", "island", "i love leetcode", "ironman"),
                List.of(5, 3, 2, 2));
    }

    @Test
    void suggestionsRankByFrequencyThenLexicographicOrder() {
        AutocompleteSystem system = new AutocompleteSystem(
                List.of("app", "apple", "ape", "apricot"),
                List.of(4, 4, 2, 4));

        assertEquals(List.of("app", "apple", "apricot", "ape"), system.suggest("ap", 10));
    }

    @Test
    void kLimitIsRespected() {
        AutocompleteSystem system = sampleSystem();

        assertEquals(List.of("i love you", "island"), system.suggest("i", 2));
    }

    @Test
    void prefixWithNoMatchesReturnsEmptyList() {
        AutocompleteSystem system = sampleSystem();

        assertTrue(system.suggest("z", 3).isEmpty());
    }

    @Test
    void updatingFrequencyChangesRanking() {
        AutocompleteSystem system = new AutocompleteSystem(
                List.of("hello", "helium"),
                List.of(2, 5));

        assertEquals(List.of("helium", "hello"), system.suggest("hel", 2));
        system.addTerm("hello", 4); // hello now has 6 total occurrences.
        assertEquals(List.of("hello", "helium"), system.suggest("hel", 2));
    }

    @Test
    void longerCorpusAndInteractiveCommitWorkTogether() {
        AutocompleteSystem system = new AutocompleteSystem(
                List.of(
                        "salesforce crm", "salesforce cloud", "salesforce careers",
                        "search autocomplete", "search analytics", "service cloud",
                        "slack integration", "sandbox refresh", "schema builder"),
                List.of(8, 7, 3, 6, 4, 5, 2, 2, 1));

        assertEquals(List.of("salesforce crm", "salesforce cloud", "salesforce careers"),
                system.suggest("sales", 3));
        assertEquals(List.of("search autocomplete", "search analytics"),
                system.suggest("search", 5));

        assertEquals(List.of("salesforce crm", "salesforce cloud", "search autocomplete"),
                system.input('s'));
        assertEquals(List.of("salesforce crm", "salesforce cloud", "salesforce careers"),
                system.input('a'));
        assertEquals(List.of(), system.input('a'));
        assertEquals(List.of(), system.input('#'));
        assertEquals(List.of("saa"), system.suggest("saa", 3));
    }
}
