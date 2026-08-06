package com.example.atm.state;

import com.example.atm.model.AtmStatus;
import com.example.atm.model.Card;
import com.example.atm.service.AtmMachine;

/** IDLE accepts only card insertion; all money operations remain guarded by the interface defaults. */
public class IdleState implements AtmState {

    @Override
    public AtmStatus status() {
        return AtmStatus.IDLE;
    }

    @Override
    public void insertCard(AtmMachine atm, Card card) {
        atm.attachCard(card);
        atm.resetFailedPinAttempts();
        atm.transitionTo(new CardInsertedState());
    }
}
