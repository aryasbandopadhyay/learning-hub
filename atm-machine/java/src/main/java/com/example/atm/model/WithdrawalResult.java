package com.example.atm.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable value returned after a successful withdrawal. */
public record WithdrawalResult(long amountCents, Map<Integer, Integer> notes) {
    public WithdrawalResult {
        notes = Collections.unmodifiableMap(new LinkedHashMap<>(notes));
    }
}
