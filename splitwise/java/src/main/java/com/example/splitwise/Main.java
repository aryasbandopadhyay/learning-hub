package com.example.splitwise;

import com.example.splitwise.model.User;
import com.example.splitwise.service.ExpenseManager;
import com.example.splitwise.strategy.EqualSplitStrategy;
import com.example.splitwise.strategy.ExactSplitStrategy;
import com.example.splitwise.strategy.PercentSplitStrategy;

import java.util.List;
import java.util.Map;

/** Runnable demo showing equal, exact, and percent strategies producing one net balance sheet. */
public class Main {

    public static void main(String[] args) {
        User alice = new User("u1", "Alice");
        User bob = new User("u2", "Bob");
        User charlie = new User("u3", "Charlie");
        ExpenseManager manager = new ExpenseManager();

        manager.addExpense(alice, 30000, List.of(alice, bob, charlie), new EqualSplitStrategy());
        manager.addExpense(bob, 12000, List.of(alice, bob), new ExactSplitStrategy(),
                Map.of(alice, 5000L, bob, 7000L));
        manager.addExpense(charlie, 10000, List.of(alice, charlie), new PercentSplitStrategy(),
                Map.of(alice, 25L, charlie, 75L));

        System.out.println("Recorded expenses: " + manager.getExpenses().size());
        System.out.println("Balances:");
        System.out.println(manager.showBalances());
    }
}
