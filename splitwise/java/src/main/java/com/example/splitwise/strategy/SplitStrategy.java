package com.example.splitwise.strategy;

import com.example.splitwise.model.Split;
import com.example.splitwise.model.User;

import java.util.List;
import java.util.Map;

/**
 * Strategy pattern centerpiece: every split algorithm has the same contract. The ExpenseManager
 * depends on this abstraction, so EQUAL/EXACT/PERCENT (or future weighted shares) can be swapped
 * without changing balance-sheet code.
 */
public interface SplitStrategy {

    /**
     * @param totalCents  total paid, represented as integer cents for exact arithmetic
     * @param participants users who consumed the expense, usually including the payer
     * @param values optional strategy-specific input: exact cents or integer percentages
     */
    List<Split> split(long totalCents, List<User> participants, Map<User, Long> values);
}
