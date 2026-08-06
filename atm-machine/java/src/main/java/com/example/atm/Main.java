package com.example.atm;

import com.example.atm.model.Account;
import com.example.atm.model.Card;
import com.example.atm.model.WithdrawalResult;
import com.example.atm.service.AtmMachine;
import com.example.atm.service.CashDispenser;

/** Runnable demo showing the normal ATM session: card, PIN, balance, withdrawal, eject. */
public class Main {

    public static void main(String[] args) {
        Account account = new Account("ACC-1", 1000000); // ₹10,000.00 in cents/paise
        Card card = new Card("CARD-1", "1234", account);
        AtmMachine atm = new AtmMachine(CashDispenser.demoDispenser());

        System.out.println("ATM state at open: " + atm.status());
        atm.insertCard(card);
        System.out.println("After card insert: " + atm.status());
        atm.enterPin("1234");
        System.out.println("After PIN: " + atm.status());
        System.out.println("Balance before withdrawal: " + money(atm.checkBalance()));

        WithdrawalResult result = atm.withdraw(300000); // ₹3,000.00
        System.out.println("Dispensed: " + money(result.amountCents()) + " as " + result.notes());
        System.out.println("Balance after withdrawal: " + money(atm.checkBalance()));
        atm.ejectCard();
        System.out.println("After eject: " + atm.status());
    }

    private static String money(long cents) {
        return String.format("INR %,.2f", cents / 100.0);
    }
}
