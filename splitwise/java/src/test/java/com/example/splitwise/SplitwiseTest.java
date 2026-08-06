package com.example.splitwise;

import com.example.splitwise.exception.InvalidSplitException;
import com.example.splitwise.model.BalanceSummary;
import com.example.splitwise.model.User;
import com.example.splitwise.service.ExpenseManager;
import com.example.splitwise.strategy.EqualSplitStrategy;
import com.example.splitwise.strategy.ExactSplitStrategy;
import com.example.splitwise.strategy.PercentSplitStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SplitwiseTest {

    private final User alice = new User("u1", "Alice");
    private final User bob = new User("u2", "Bob");
    private final User charlie = new User("u3", "Charlie");

    @Test
    void equalSplitCreatesDebtsToPayer() {
        ExpenseManager manager = new ExpenseManager();
        manager.addExpense(alice, 300, List.of(alice, bob, charlie), new EqualSplitStrategy());

        BalanceSummary aliceBalances = manager.getBalances(alice);
        assertEquals(100L, aliceBalances.owedBy().get(bob));
        assertEquals(100L, aliceBalances.owedBy().get(charlie));
        assertTrue(aliceBalances.owes().isEmpty());
    }

    @Test
    void exactSplitRejectsBadSumAndAcceptsValidShares() {
        ExpenseManager manager = new ExpenseManager();
        ExactSplitStrategy strategy = new ExactSplitStrategy();

        assertThrows(InvalidSplitException.class, () -> manager.addExpense(alice, 600,
                List.of(alice, bob, charlie), strategy, Map.of(alice, 300L, bob, 100L, charlie, 100L)));

        manager.addExpense(alice, 600, List.of(alice, bob, charlie), strategy,
                Map.of(alice, 300L, bob, 100L, charlie, 200L));
        assertEquals(100L, manager.getBalances(bob).owes().get(alice));
        assertEquals(200L, manager.getBalances(charlie).owes().get(alice));
    }

    @Test
    void percentSplitRejectsBadSumAndUpdatesBalances() {
        ExpenseManager manager = new ExpenseManager();
        PercentSplitStrategy strategy = new PercentSplitStrategy();

        assertThrows(InvalidSplitException.class, () -> manager.addExpense(bob, 10000,
                List.of(alice, bob), strategy, Map.of(alice, 30L, bob, 30L)));

        manager.addExpense(bob, 10000, List.of(alice, bob), strategy, Map.of(alice, 25L, bob, 75L));
        assertEquals(2500L, manager.getBalances(alice).owes().get(bob));
    }

    @Test
    void balancesNetOutAcrossMultipleExpenses() {
        ExpenseManager manager = new ExpenseManager();
        manager.addExpense(alice, 300, List.of(alice, bob, charlie), new EqualSplitStrategy());
        manager.addExpense(bob, 150, List.of(alice, bob), new EqualSplitStrategy());

        assertEquals(25L, manager.getBalances(bob).owes().get(alice));
        assertEquals(100L, manager.getBalances(charlie).owes().get(alice));
        assertTrue(manager.getBalances(alice).owes().isEmpty());
    }

    @Test
    void concurrentEqualExpensesHaveNoLostUpdates() throws InterruptedException {
        int threads = 80;
        ExpenseManager manager = new ExpenseManager();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    manager.addExpense(alice, 2, List.of(alice, bob), new EqualSplitStrategy());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(threads, manager.getBalances(bob).owes().get(alice));
        assertEquals(threads, manager.getExpenses().size());
    }
}
