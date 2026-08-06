package com.example.vending.state;

import com.example.vending.model.MachineStateName;
import com.example.vending.model.MoneyResult;
import com.example.vending.service.VendingMachine;

/** IDLE means no transaction exists yet. Only inserting money can start the flow. */
public final class IdleState extends AbstractState {
    public static final IdleState INSTANCE = new IdleState();

    private IdleState() {
    }

    @Override
    public MachineStateName name() {
        return MachineStateName.IDLE;
    }

    @Override
    public MoneyResult insertMoney(VendingMachine machine, int amount) {
        machine.acceptCoin(amount);
        machine.setState(HasMoneyState.INSTANCE);
        return new MoneyResult(machine.getBalance(), "Accepted " + amount + ", balance = " + machine.getBalance());
    }
}
