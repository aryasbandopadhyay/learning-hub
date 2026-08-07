package com.example.autocomplete.model;

/** Immutable candidate used while ranking top-k suggestions. */
public record RankedTerm(String term, int weight) {
}
