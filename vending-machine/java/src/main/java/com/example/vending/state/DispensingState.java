package com.example.vending.state;

import com.example.vending.model.MachineStateName;
import com.example.vending.model.PurchaseResult;
import com.example.vending.service.VendingMachine;

/** DISPENSING is deliberately short-lived: decrement stock, make change, and return to IDLE. */
public final class DispensingState extends AbstractState {
    public static final DispensingState INSTANCE = new DispensingState();

    private DispensingState() {
    }

    @Override
    public MachineStateName name() {
        return MachineStateName.DISPENSING;
    }

    @Override
    public PurchaseResult dispense(VendingMachine machine) {
        return machine.completeDispense();
    }
}
