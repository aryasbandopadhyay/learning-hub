package com.example.atm.state;

import com.example.atm.exception.AuthenticationException;
import com.example.atm.model.AtmStatus;
import com.example.atm.service.AtmMachine;

/** Card is present but not trusted yet. Only PIN entry or eject are valid. */
public class CardInsertedState implements AtmState {

    @Override
    public AtmStatus status() {
        return AtmStatus.CARD_INSERTED;
    }

    @Override
    public void enterPin(AtmMachine atm, String pin) {
        if (atm.currentCard().matchesPin(pin)) {
            atm.resetFailedPinAttempts();
            atm.transitionTo(new AuthenticatedState());
            return;
        }
        int failures = atm.incrementFailedPinAttempts();
        if (failures >= atm.maxPinAttempts()) {
            atm.clearCard();
            atm.transitionTo(new IdleState());
            throw new AuthenticationException("Too many wrong PIN attempts; card ejected");
        }
        throw new AuthenticationException("Incorrect PIN");
    }

    @Override
    public void ejectCard(AtmMachine atm) {
        atm.clearCard();
        atm.transitionTo(new IdleState());
    }
}
