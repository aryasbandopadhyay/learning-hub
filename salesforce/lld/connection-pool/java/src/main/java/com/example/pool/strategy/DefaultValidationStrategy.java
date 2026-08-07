package com.example.pool.strategy;

import com.example.pool.model.Connection;

/** Default validation: a fake connection is usable while it remains open. */
public class DefaultValidationStrategy implements ValidationStrategy {
    @Override
    public boolean isValid(Connection connection) {
        return connection.isValid();
    }
}
