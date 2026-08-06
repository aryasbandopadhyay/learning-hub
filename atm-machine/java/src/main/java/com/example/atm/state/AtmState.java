package com.example.atm.state;

import com.example.atm.exception.InvalidOperationException;
import com.example.atm.model.AtmStatus;
import com.example.atm.model.Card;
import com.example.atm.model.WithdrawalResult;
import com.example.atm.service.AtmMachine;

/**
 * State pattern interface. The ATM exposes stable operations, but delegates each operation to this
 * object so every state owns its valid actions and transitions.
 */
public interface AtmState {

    AtmStatus status();

    default void insertCard(AtmMachine atm, Card card) {
        throw invalid("insertCard");
    }

    default void enterPin(AtmMachine atm, String pin) {
        throw invalid("enterPin");
    }

    default long checkBalance(AtmMachine atm) {
        throw invalid("checkBalance");
    }

    default WithdrawalResult withdraw(AtmMachine atm, long amountCents) {
        throw invalid("withdraw");
    }

    default void deposit(AtmMachine atm, long amountCents) {
        throw invalid("deposit");
    }

    default void ejectCard(AtmMachine atm) {
        throw invalid("ejectCard");
    }

    private InvalidOperationException invalid(String operation) {
        return new InvalidOperationException(operation + " is not allowed while ATM is " + status());
    }
}
