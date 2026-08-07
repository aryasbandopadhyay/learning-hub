package com.example.pool.factory;

import com.example.pool.model.Connection;

/** Factory abstraction: the pool creates resources without depending on a concrete constructor. */
public interface ConnectionFactory {
    Connection create(String id);
}
