package com.example.atm.service;

import com.example.atm.model.Account;
import com.example.atm.model.AtmStatus;
import com.example.atm.model.Card;
import com.example.atm.model.WithdrawalResult;
import com.example.atm.state.AtmState;
import com.example.atm.state.IdleState;

import java.util.Map;

/**
 * ATM facade. Public methods are tiny because the State pattern is doing the real work.
 *
 * <p>Each method is synchronized so one physical ATM session cannot interleave operations from two
 * clients. The account itself is also synchronized, which is the important protection when many ATM
 * objects or channels hit the same account concurrently.
 */
public class AtmMachine {

    private final CashDispenser cashDispenser;
    private final int maxPinAttempts;

    private AtmState state = new IdleState();
    private Card currentCard;
    private int failedPinAttempts;

    public AtmMachine(CashDispenser cashDispenser) {
        this(cashDispenser, 3);
    }

    public AtmMachine(CashDispenser cashDispenser, int maxPinAttempts) {
        this.cashDispenser = cashDispenser;
        this.maxPinAttempts = maxPinAttempts;
    }

    public synchronized void insertCard(Card card) {
        state.insertCard(this, card);
    }

    public synchronized void enterPin(String pin) {
        state.enterPin(this, pin);
    }

    public synchronized long checkBalance() {
        return state.checkBalance(this);
    }

    public synchronized WithdrawalResult withdraw(long amountCents) {
        return state.withdraw(this, amountCents);
    }

    public synchronized void deposit(long amountCents) {
        state.deposit(this, amountCents);
    }

    public synchronized void ejectCard() {
        state.ejectCard(this);
    }

    public synchronized AtmStatus status() {
        return state.status();
    }

    /** Transaction helper used only by AuthenticatedState during the DISPENSING state. */
    public WithdrawalResult dispenseCash(long amountCents) {
        Account account = currentAccount();
        synchronized (cashDispenser) {
            Map<Integer, Integer> plan = cashDispenser.planBreakdown(amountCents);
            account.withdraw(amountCents);
            cashDispenser.dispense(amountCents);
            return new WithdrawalResult(amountCents, plan);
        }
    }

    public Map<Integer, Integer> cashInventory() {
        return cashDispenser.inventory();
    }

    public void transitionTo(AtmState nextState) {
        this.state = nextState;
    }

    public void attachCard(Card card) {
        this.currentCard = card;
    }

    public void clearCard() {
        this.currentCard = null;
        resetFailedPinAttempts();
    }

    public Card currentCard() {
        return currentCard;
    }

    public Account currentAccount() {
        return currentCard.getAccount();
    }

    public int incrementFailedPinAttempts() {
        return ++failedPinAttempts;
    }

    public void resetFailedPinAttempts() {
        failedPinAttempts = 0;
    }

    public int maxPinAttempts() {
        return maxPinAttempts;
    }
}
