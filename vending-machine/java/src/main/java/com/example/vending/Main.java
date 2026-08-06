package com.example.vending;

import com.example.vending.model.PurchaseResult;
import com.example.vending.model.RefundResult;
import com.example.vending.service.VendingMachine;

/** Runnable demo showing insert → select → dispense/change → cancel/refund. */
public class Main {

    public static void main(String[] args) {
        VendingMachine machine = VendingMachine.demoMachine();

        System.out.println("Stock at open: WATER=" + machine.stockOf("WATER")
                + ", CHIPS=" + machine.stockOf("CHIPS")
                + ", SODA=" + machine.stockOf("SODA"));

        machine.insertMoney(25);
        PurchaseResult water = machine.selectProduct("WATER");
        System.out.println(water.message());
        System.out.println("WATER stock now: " + machine.stockOf("WATER"));

        machine.insertMoney(25);
        PurchaseResult chips = machine.selectProduct("CHIPS");
        System.out.println(chips.message());

        machine.insertMoney(10);
        machine.insertMoney(5);
        RefundResult refund = machine.cancel();
        System.out.println(refund.message());
        System.out.println("State now: " + machine.currentState());
    }
}
