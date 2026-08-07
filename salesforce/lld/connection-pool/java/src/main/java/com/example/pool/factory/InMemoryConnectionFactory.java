package com.example.pool.factory;

import com.example.pool.model.Connection;

/** Default fake factory for demos/tests; in real code this could open DB sockets instead. */
public class InMemoryConnectionFactory implements ConnectionFactory {
    @Override
    public Connection create(String id) {
        return new Connection(id);
    }
}
