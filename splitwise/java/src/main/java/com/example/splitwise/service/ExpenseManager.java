package com.example.splitwise.service;

import com.example.splitwise.model.BalanceSummary;
import com.example.splitwise.model.Expense;
import com.example.splitwise.model.Split;
import com.example.splitwise.model.User;
import com.example.splitwise.strategy.SplitStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * The application service / aggregate root.
 *
 * <p>It records immutable expenses and maintains a normalized balance sheet where
 * balances[debtor][creditor] = cents the debtor owes the creditor. addExpense is synchronized
 * because it mutates two shared structures (expenses + balances) and must be atomic under
 * concurrent calls.
 */
public class ExpenseManager {

    private final List<Expense> expenses = new ArrayList<>();
    private final Map<User, Map<User, Long>> balances = new LinkedHashMap<>();

    public synchronized Expense addExpense(User payer,
                                           long totalCents,
                                           List<User> participants,
                                           SplitStrategy strategy,
                                           Map<User, Long> splitValues) {
        List<Split> splits = strategy.split(totalCents, participants,
                splitValues == null ? Map.of() : splitValues);
        Expense expense = new Expense(payer, totalCents, splits);
        expenses.add(expense);

        // Every non-payer consumed value that the payer covered, so that user owes the payer.
        for (Split split : splits) {
            if (!split.user().equals(payer) && split.amountCents() > 0) {
                addDebt(split.user(), payer, split.amountCents());
            }
        }
        return expense;
    }

    public Expense addExpense(User payer, long totalCents, List<User> participants, SplitStrategy strategy) {
        return addExpense(payer, totalCents, participants, strategy, Map.of());
    }

    /** Net a new debtor->creditor amount against current same-direction and opposite debt. */
    private void addDebt(User debtor, User creditor, long amountCents) {
        long current = balances.getOrDefault(debtor, Map.of()).getOrDefault(creditor, 0L);
        long opposite = balances.getOrDefault(creditor, Map.of()).getOrDefault(debtor, 0L);
        long newDebt = current + amountCents;
        if (opposite >= newDebt) {
            setDebt(debtor, creditor, 0);
            setDebt(creditor, debtor, opposite - newDebt);
        } else {
            setDebt(creditor, debtor, 0);
            setDebt(debtor, creditor, newDebt - opposite);
        }
    }

    private void setDebt(User debtor, User creditor, long amountCents) {
        Map<User, Long> row = balances.computeIfAbsent(debtor, ignored -> new LinkedHashMap<>());
        if (amountCents == 0) {
            row.remove(creditor);
            if (row.isEmpty()) {
                balances.remove(debtor);
            }
        } else {
            row.put(creditor, amountCents);
        }
    }

    public synchronized BalanceSummary getBalances(User user) {
        Map<User, Long> owes = new LinkedHashMap<>(balances.getOrDefault(user, Map.of()));
        Map<User, Long> owedBy = new LinkedHashMap<>();
        for (var entry : balances.entrySet()) {
            Long amount = entry.getValue().get(user);
            if (amount != null && amount > 0) {
                owedBy.put(entry.getKey(), amount);
            }
        }
        return new BalanceSummary(user, owes, owedBy);
    }

    public synchronized String showBalances() {
        if (balances.isEmpty()) {
            return "No balances";
        }
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (var debtorEntry : balances.entrySet()) {
            for (var creditorEntry : debtorEntry.getValue().entrySet()) {
                joiner.add(debtorEntry.getKey().getName() + " owes "
                        + creditorEntry.getKey().getName() + " " + formatCents(creditorEntry.getValue()));
            }
        }
        return joiner.toString();
    }

    public synchronized List<Expense> getExpenses() {
        return List.copyOf(expenses);
    }

    public static String formatCents(long cents) {
        return String.format("$%.2f", cents / 100.0);
    }
}
