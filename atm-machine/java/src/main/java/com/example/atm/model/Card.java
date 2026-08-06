package com.example.atm.model;

/**
 * Simplified card model: the card directly references an Account and stores a demo PIN.
 *
 * <p>Real systems would call a bank network and would never store a raw PIN. That is deliberately
 * out of scope so the State pattern remains the focus of this LLD exercise.
 */
public class Card {

    private final String cardNumber;
    private final String pin;
    private final Account account;

    public Card(String cardNumber, String pin, Account account) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.account = account;
    }

    public boolean matchesPin(String candidate) {
        return pin.equals(candidate);
    }

    public Account getAccount() {
        return account;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
