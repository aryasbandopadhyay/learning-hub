package com.example.atm;

import com.example.atm.exception.AuthenticationException;
import com.example.atm.exception.CashDispenseException;
import com.example.atm.exception.InsufficientFundsException;
import com.example.atm.exception.InvalidOperationException;
import com.example.atm.model.Account;
import com.example.atm.model.AtmStatus;
import com.example.atm.model.Card;
import com.example.atm.model.WithdrawalResult;
import com.example.atm.service.AtmMachine;
import com.example.atm.service.CashDispenser;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtmMachineTest {

    private AtmMachine newAtm(Account account) {
        AtmMachine atm = new AtmMachine(
                new CashDispenser(Map.of(200000, 5, 50000, 10, 20000, 10, 10000, 20)));
        atm.insertCard(new Card("CARD-1", "1234", account));
        atm.enterPin("1234");
        return atm;
    }

    @Test
    void insertCardAndCorrectPinAuthenticates() {
        Account account = new Account("A1", 100000);
        AtmMachine atm = new AtmMachine(CashDispenser.demoDispenser());
        atm.insertCard(new Card("C1", "1234", account));
        assertEquals(AtmStatus.CARD_INSERTED, atm.status());
        atm.enterPin("1234");
        assertEquals(AtmStatus.AUTHENTICATED, atm.status());
    }

    @Test
    void wrongPinBeyondLimitEjectsCard() {
        Account account = new Account("A1", 100000);
        AtmMachine atm = new AtmMachine(CashDispenser.demoDispenser());
        atm.insertCard(new Card("C1", "1234", account));
        assertThrows(AuthenticationException.class, () -> atm.enterPin("0000"));
        assertThrows(AuthenticationException.class, () -> atm.enterPin("1111"));
        assertThrows(AuthenticationException.class, () -> atm.enterPin("2222"));
        assertEquals(AtmStatus.IDLE, atm.status());
    }

    @Test
    void withdrawWithSufficientFundsDispensesNotesAndUpdatesInventory() {
        Account account = new Account("A1", 1000000);
        AtmMachine atm = newAtm(account);
        WithdrawalResult result = atm.withdraw(300000);
        assertEquals(700000, account.getBalanceCents());
        assertEquals(Map.of(200000, 1, 50000, 2), result.notes());
        assertEquals(4, atm.cashInventory().get(200000));
        assertEquals(8, atm.cashInventory().get(50000));
        assertEquals(AtmStatus.AUTHENTICATED, atm.status());
    }

    @Test
    void withdrawMoreThanBalanceIsRejected() {
        Account account = new Account("A1", 100000);
        AtmMachine atm = newAtm(account);
        assertThrows(InsufficientFundsException.class, () -> atm.withdraw(200000));
        assertEquals(100000, account.getBalanceCents());
    }

    @Test
    void nonDispensableAmountIsRejectedBeforeDebit() {
        Account account = new Account("A1", 1000000);
        AtmMachine atm = newAtm(account);
        assertThrows(CashDispenseException.class, () -> atm.withdraw(12500));
        assertEquals(1000000, account.getBalanceCents());
    }

    @Test
    void operationBeforeAuthenticationIsRejectedByStateGuards() {
        Account account = new Account("A1", 1000000);
        AtmMachine idle = new AtmMachine(CashDispenser.demoDispenser());
        assertThrows(InvalidOperationException.class, () -> idle.withdraw(10000));

        AtmMachine cardInserted = new AtmMachine(CashDispenser.demoDispenser());
        cardInserted.insertCard(new Card("C1", "1234", account));
        assertThrows(InvalidOperationException.class, () -> cardInserted.withdraw(10000));
    }

    @Test
    void depositIncreasesBalance() {
        Account account = new Account("A1", 100000);
        AtmMachine atm = newAtm(account);
        atm.deposit(50000);
        assertEquals(150000, atm.checkBalance());
    }

    /**
     * Concurrency test: many sessions share one Account with only enough funds for some winners.
     * Account.withdraw is synchronized, so total dispensed never exceeds the opening balance.
     */
    @Test
    void concurrentWithdrawalsNeverOverdrawAccount() throws InterruptedException {
        int threads = 50;
        long startBalance = 500000;
        long amount = 100000;
        Account shared = new Account("A1", startBalance);
        Card sharedCard = new Card("C1", "1234", shared);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    AtmMachine atm = new AtmMachine(CashDispenser.demoDispenser());
                    atm.insertCard(sharedCard);
                    atm.enterPin("1234");
                    start.await();
                    atm.withdraw(amount);
                    successes.incrementAndGet();
                } catch (InsufficientFundsException ignored) {
                    // expected once the limited shared balance is exhausted
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(startBalance - successes.get() * amount, shared.getBalanceCents());
        assertTrue(shared.getBalanceCents() >= 0);
        assertEquals(5, successes.get());
    }
}
