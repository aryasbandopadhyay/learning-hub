package com.example.splitwise.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Snapshot returned by getBalances(user): outgoing debts and incoming debts for that user. */
public record BalanceSummary(User user, Map<User, Long> owes, Map<User, Long> owedBy) {

    public BalanceSummary {
        owes = Collections.unmodifiableMap(new LinkedHashMap<>(owes));
        owedBy = Collections.unmodifiableMap(new LinkedHashMap<>(owedBy));
    }
}
