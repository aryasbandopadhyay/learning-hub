package com.example.vending.state;

import com.example.vending.exception.OutOfStockException;
import com.example.vending.model.MachineStateName;
import com.example.vending.model.RefundResult;
import com.example.vending.service.VendingMachine;

/** SOLD_OUT models the rejected branch when the selected product has no inventory. */
public final class SoldOutState extends AbstractState {
    public static final SoldOutState INSTANCE = new SoldOutState();

    private SoldOutState() {
    }

    @Override
    public MachineStateName name() {
        return MachineStateName.SOLD_OUT;
    }

    @Override
    public RefundResult cancel(VendingMachine machine) {
        return machine.refundAndReset("Sold-out selection cancelled");
    }

    public OutOfStockException soldOut(VendingMachine machine) {
        return new OutOfStockException(machine.getLastSoldOutCode() + " is sold out");
    }
}
