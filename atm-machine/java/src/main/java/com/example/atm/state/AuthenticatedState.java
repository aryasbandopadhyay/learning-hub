package com.example.atm.state;

import com.example.atm.model.AtmStatus;
import com.example.atm.model.WithdrawalResult;
import com.example.atm.service.AtmMachine;

/** Authenticated users may check balance, deposit, withdraw, or end the session. */
public class AuthenticatedState implements AtmState {

    @Override
    public AtmStatus status() {
        return AtmStatus.AUTHENTICATED;
    }

    @Override
    public long checkBalance(AtmMachine atm) {
        return atm.currentAccount().getBalanceCents();
    }

    @Override
    public WithdrawalResult withdraw(AtmMachine atm, long amountCents) {
        atm.transitionTo(new DispensingState());
        try {
            return atm.dispenseCash(amountCents);
        } finally {
            atm.transitionTo(this);
        }
    }

    @Override
    public void deposit(AtmMachine atm, long amountCents) {
        atm.currentAccount().deposit(amountCents);
    }

    @Override
    public void ejectCard(AtmMachine atm) {
        atm.clearCard();
        atm.transitionTo(new IdleState());
    }
}
