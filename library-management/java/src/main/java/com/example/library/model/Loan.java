package com.example.library.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Join entity connecting Member <-> BookItem for one borrowing period.
 *
 * <p>Relationship: Member 1..* Loan and BookItem 1..* historical Loan (only one active at a time).
 * Return time is nullable until the copy comes back.
 */
public class Loan {

    private final String id;
    private final Member member;
    private final BookItem item;
    private final Instant checkoutTime;
    private final Instant dueTime;
    private Instant returnTime;

    public Loan(Member member, BookItem item, Instant checkoutTime, Instant dueTime) {
        this.id = UUID.randomUUID().toString();
        this.member = member;
        this.item = item;
        this.checkoutTime = checkoutTime;
        this.dueTime = dueTime;
    }

    public void close(Instant returnTime) {
        this.returnTime = returnTime;
    }

    public boolean isOpen() {
        return returnTime == null;
    }

    public String getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public BookItem getItem() {
        return item;
    }

    public Instant getCheckoutTime() {
        return checkoutTime;
    }

    public Instant getDueTime() {
        return dueTime;
    }

    public Instant getReturnTime() {
        return returnTime;
    }
}
