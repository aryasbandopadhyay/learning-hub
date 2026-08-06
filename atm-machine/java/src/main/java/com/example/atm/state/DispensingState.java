package com.example.atm.state;

import com.example.atm.model.AtmStatus;

/**
 * Short-lived transaction state entered during withdraw. It rejects all external operations while
 * the ATM is planning notes, debiting the account, and updating dispenser inventory.
 */
public class DispensingState implements AtmState {

    @Override
    public AtmStatus status() {
        return AtmStatus.DISPENSING;
    }
}
