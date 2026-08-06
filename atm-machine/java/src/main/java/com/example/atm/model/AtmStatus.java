package com.example.atm.model;

/** Public snapshot of the State object currently serving the ATM. */
public enum AtmStatus {
    IDLE,
    CARD_INSERTED,
    AUTHENTICATED,
    DISPENSING
}
