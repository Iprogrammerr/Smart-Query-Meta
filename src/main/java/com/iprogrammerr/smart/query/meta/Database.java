package com.iprogrammerr.smart.query.meta;

import org.h2.tools.RunScript;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private final String jdbcUrl;
    private final String databaseUser;
    private final String databasePassword;
    private Connection connection;

    public Database(String jdbcUrl, String databaseUser, String databasePassword) {
        this.jdbcUrl = jdbcUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
    }

    public void setup() throws Exception {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
            getClass().getResourceAsStream("/schema.sql")))) {
            RunScript.execute(connection(), r);
        }
    }

    public Connection connection() throws Exception {
        if (connection == null) {
            connection = DriverManager.getConnection(jdbcUrl, databaseUser,
                databasePassword);
        }
        return connection;
    }
}
