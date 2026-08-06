package com.example.vending.state;

import com.example.vending.exception.InvalidStateException;
import com.example.vending.model.MachineStateName;
import com.example.vending.model.MoneyResult;
import com.example.vending.model.PurchaseResult;
import com.example.vending.model.RefundResult;
import com.example.vending.service.VendingMachine;

/** Default rejections keep concrete states focused on the operations they actually allow. */
abstract class AbstractState implements State {

    protected InvalidStateException invalid(String operation) {
        return new InvalidStateException(operation + " is not allowed while machine is " + name());
    }

    @Override
    public MoneyResult insertMoney(VendingMachine machine, int amount) {
        throw invalid("insertMoney");
    }

    @Override
    public PurchaseResult selectProduct(VendingMachine machine, String productCode) {
        throw invalid("selectProduct");
    }

    @Override
    public PurchaseResult dispense(VendingMachine machine) {
        throw invalid("dispense");
    }

    @Override
    public RefundResult cancel(VendingMachine machine) {
        throw invalid("cancel");
    }

    @Override
    public abstract MachineStateName name();
}
