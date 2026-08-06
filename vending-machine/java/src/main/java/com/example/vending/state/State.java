package com.example.vending.state;

import com.example.vending.model.MachineStateName;
import com.example.vending.model.MoneyResult;
import com.example.vending.model.PurchaseResult;
import com.example.vending.model.RefundResult;
import com.example.vending.service.VendingMachine;

/**
 * State pattern contract. The service delegates every user operation to the current implementation;
 * each State decides which operations are legal and which next State should be installed.
 */
public interface State {
    MachineStateName name();

    MoneyResult insertMoney(VendingMachine machine, int amount);

    PurchaseResult selectProduct(VendingMachine machine, String productCode);

    PurchaseResult dispense(VendingMachine machine);

    RefundResult cancel(VendingMachine machine);
}
