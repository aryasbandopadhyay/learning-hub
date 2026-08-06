package com.example.locker.model;

/** State pattern in the small: a locker is either available or holding exactly one package. */
public enum LockerState {
    FREE,
    OCCUPIED
}
