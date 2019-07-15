package com.iprogrammerr.smart.query.meta;

import org.h2.tools.RunScript;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestDatabaseSetup {

    private final String jdbcUrl;
    private final String user;
    private final String password;
    private Connection connection;

    public TestDatabaseSetup(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public TestDatabaseSetup() {
        this("jdbc:h2:mem:test", "test", "test");
    }

    public void setup() {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
            TestDatabaseSetup.class.getResourceAsStream("/schema.sql")))) {
            RunScript.execute(connection(), r);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Connection connection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(jdbcUrl, user, password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}
