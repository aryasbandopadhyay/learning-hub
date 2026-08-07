package com.example.pool.strategy;

import com.example.pool.model.Connection;

/** Strategy hook for validation-on-borrow. */
public interface ValidationStrategy {
    boolean isValid(Connection connection);
}
