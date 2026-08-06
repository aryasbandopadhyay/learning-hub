package com.example.vending.state;

import com.example.vending.exception.InsufficientFundsException;
import com.example.vending.exception.OutOfStockException;
import com.example.vending.model.InventoryItem;
import com.example.vending.model.MachineStateName;
import com.example.vending.model.MoneyResult;
import com.example.vending.model.Product;
import com.example.vending.model.PurchaseResult;
import com.example.vending.model.RefundResult;
import com.example.vending.service.VendingMachine;

/** HAS_MONEY owns the middle of the transaction: accept more coins, select, or cancel/refund. */
public final class HasMoneyState extends AbstractState {
    public static final HasMoneyState INSTANCE = new HasMoneyState();

    private HasMoneyState() {
    }

    @Override
    public MachineStateName name() {
        return MachineStateName.HAS_MONEY;
    }

    @Override
    public MoneyResult insertMoney(VendingMachine machine, int amount) {
        machine.acceptCoin(amount);
        return new MoneyResult(machine.getBalance(), "Accepted " + amount + ", balance = " + machine.getBalance());
    }

    @Override
    public PurchaseResult selectProduct(VendingMachine machine, String productCode) {
        InventoryItem item = machine.requireItem(productCode);
        Product product = item.getProduct();
        if (!item.isAvailable()) {
            machine.setLastSoldOutCode(productCode);
            machine.setState(SoldOutState.INSTANCE);
            machine.setState(HasMoneyState.INSTANCE); // sold-out is a handled rejection, not a lost payment
            throw new OutOfStockException(productCode + " is sold out");
        }
        if (machine.getBalance() < product.getPrice()) {
            throw new InsufficientFundsException(
                    "Need " + product.getPrice() + ", but balance is " + machine.getBalance());
        }
        machine.setSelectedProduct(product);
        machine.setState(DispensingState.INSTANCE);
        return machine.dispense();
    }

    @Override
    public RefundResult cancel(VendingMachine machine) {
        return machine.refundAndReset("Transaction cancelled");
    }
}
