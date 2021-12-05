package com.bobocode;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxyDataSource extends PGSimpleDataSource {
    private final int POOL_SIZE = 10;
    private final Queue<Connection> connectionQueue = new ConcurrentLinkedQueue<>();

    public ProxyDataSource(String url, String userName, String userPass) {
        super();
        setURL(url);
        setUser(userName);
        setPassword(userPass);
        for (int i = 0; i < POOL_SIZE; i++) {
            try (Connection connection = super.getConnection()) {
                ProxyConnection proxyConnection = new ProxyConnection(connection, connectionQueue);
                connectionQueue.add(proxyConnection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Connection getConnection() {
        return connectionQueue.poll();
    }
}
