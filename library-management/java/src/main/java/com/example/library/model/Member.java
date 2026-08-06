package com.example.library.model;

/**
 * Borrower entity. The max-loans rule belongs here as member policy, while active-loan counting is
 * kept in LibraryService because it owns the currently-open Loan records.
 */
public class Member {

    private final String id;
    private final String name;
    private final int maxConcurrentLoans;

    public Member(String id, String name, int maxConcurrentLoans) {
        this.id = id;
        this.name = name;
        this.maxConcurrentLoans = maxConcurrentLoans;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxConcurrentLoans() {
        return maxConcurrentLoans;
    }
}
