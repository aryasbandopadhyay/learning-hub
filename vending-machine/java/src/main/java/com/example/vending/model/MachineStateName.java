package com.example.vending.model;

/** Names of the concrete State objects; useful for tests, logs, and diagrams. */
public enum MachineStateName {
    IDLE,
    HAS_MONEY,
    DISPENSING,
    SOLD_OUT
}
