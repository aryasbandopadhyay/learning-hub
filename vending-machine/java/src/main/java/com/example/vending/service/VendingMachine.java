package com.example.vending.service;

import com.example.vending.exception.InvalidDenominationException;
import com.example.vending.exception.InvalidStateException;
import com.example.vending.exception.UnknownProductException;
import com.example.vending.model.InventoryItem;
import com.example.vending.model.MachineStateName;
import com.example.vending.model.MoneyResult;
import com.example.vending.model.Product;
import com.example.vending.model.PurchaseResult;
import com.example.vending.model.RefundResult;
import com.example.vending.state.HasMoneyState;
import com.example.vending.state.IdleState;
import com.example.vending.state.State;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Aggregate root and concurrency boundary for one physical vending machine.
 *
 * <p>Every public operation is {@code synchronized}. That means state, balance, selected product,
 * and inventory mutate under the same monitor, so multiple threads cannot interleave a vend and
 * oversell the last unit. The optional {@link #purchase(String, List)} helper holds the monitor for
 * a full transaction, matching the "one customer at a time" MVP assumption.
 */
public class VendingMachine {

    private final Map<String, InventoryItem> inventory = new LinkedHashMap<>();
    private final Set<Integer> acceptedDenominations;
    private final List<Integer> changeDenominationsDescending;

    private State state = IdleState.INSTANCE;
    private int balance;
    private Product selectedProduct;
    private String lastSoldOutCode;

    public VendingMachine(List<InventoryItem> items, Set<Integer> acceptedDenominations) {
        for (InventoryItem item : items) {
            inventory.put(item.getProduct().getCode(), item);
        }
        this.acceptedDenominations = new TreeSet<>(acceptedDenominations);
        this.changeDenominationsDescending = this.acceptedDenominations.stream()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    public static VendingMachine demoMachine() {
        return new VendingMachine(
                List.of(
                        new InventoryItem(new Product("WATER", "Water Bottle", 25), 2),
                        new InventoryItem(new Product("CHIPS", "Potato Chips", 15), 3),
                        new InventoryItem(new Product("SODA", "Soda Can", 35), 1)),
                Set.of(1, 5, 10, 25));
    }

    public synchronized MoneyResult insertMoney(int amount) {
        return state.insertMoney(this, amount);
    }

    public synchronized PurchaseResult selectProduct(String productCode) {
        return state.selectProduct(this, productCode);
    }

    public synchronized PurchaseResult dispense() {
        return state.dispense(this);
    }

    public synchronized RefundResult cancel() {
        return state.cancel(this);
    }

    /** Atomic convenience for concurrent tests and APIs that model one complete customer action. */
    public synchronized PurchaseResult purchase(String productCode, List<Integer> coins) {
        if (state.name() != MachineStateName.IDLE) {
            throw new InvalidStateException("purchase requires a fresh IDLE machine");
        }
        try {
            for (int coin : coins) {
                insertMoney(coin);
            }
            return selectProduct(productCode);
        } catch (RuntimeException e) {
            resetTransaction(); // whole-transaction helper must not leave another caller's money
            throw e;
        }
    }

    public InventoryItem requireItem(String productCode) {
        InventoryItem item = inventory.get(productCode);
        if (item == null) {
            throw new UnknownProductException("Unknown product: " + productCode);
        }
        return item;
    }

    public void acceptCoin(int amount) {
        if (!acceptedDenominations.contains(amount)) {
            throw new InvalidDenominationException("Unsupported denomination: " + amount);
        }
        balance += amount;
    }

    public PurchaseResult completeDispense() {
        if (selectedProduct == null) {
            throw new InvalidStateException("No product selected for dispensing");
        }
        InventoryItem item = requireItem(selectedProduct.getCode());
        item.decrement();
        List<Integer> change = makeChange(balance - selectedProduct.getPrice());
        Product dispensed = selectedProduct;
        resetTransaction();
        return new PurchaseResult(dispensed, change,
                "Dispensed " + dispensed.getCode() + ", change = " + change);
    }

    public RefundResult refundAndReset(String message) {
        List<Integer> coins = makeChange(balance);
        int amount = balance;
        resetTransaction();
        return new RefundResult(coins, amount, message + ", refund = " + coins);
    }

    private List<Integer> makeChange(int amount) {
        List<Integer> coins = new ArrayList<>();
        int remaining = amount;
        for (int denom : changeDenominationsDescending) {
            while (remaining >= denom) {
                coins.add(denom);
                remaining -= denom;
            }
        }
        if (remaining != 0) {
            throw new IllegalStateException("Cannot make exact greedy change for " + amount);
        }
        return coins;
    }

    private void resetTransaction() {
        balance = 0;
        selectedProduct = null;
        state = IdleState.INSTANCE;
    }

    public synchronized int stockOf(String productCode) {
        return requireItem(productCode).getStock();
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized MachineStateName currentState() {
        return state.name();
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public void setLastSoldOutCode(String lastSoldOutCode) {
        this.lastSoldOutCode = lastSoldOutCode;
    }

    public String getLastSoldOutCode() {
        return lastSoldOutCode;
    }

    public boolean hasMoney() {
        return state == HasMoneyState.INSTANCE;
    }

    public synchronized Map<String, Integer> stockSnapshot() {
        Map<String, Integer> snapshot = new LinkedHashMap<>();
        inventory.forEach((code, item) -> snapshot.put(code, item.getStock()));
        return snapshot;
    }
}
