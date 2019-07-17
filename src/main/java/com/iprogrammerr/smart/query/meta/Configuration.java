package com.iprogrammerr.smart.query.meta;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    public static final String DATABASE_USER = "database.user";
    public static final String DATABASE_PASSWORD = "database.password";
    public static final String JDBC_URL = "jdbc.url";
    public static final String CLASSES_PACKAGE = "classes.package";
    public static final String CLASSES_PATH = "classes.path";
    private final Properties properties;

    public Configuration(Properties properties) {
        this.properties = properties;
    }

    public static Configuration fromCmd(String... args) {
        try (InputStream is = args.length == 0 ? Configuration.class.getResourceAsStream("/application.properties")
            : new FileInputStream(args[0])) {
            Properties properties = new Properties();
            properties.load(is);
            return new Configuration(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String databaseUser() {
        return notNull(DATABASE_USER);
    }

    public String databasePassword() {
        return notNull(DATABASE_PASSWORD);
    }

    public String jdbcUrl() {
        return notNull(JDBC_URL);
    }

    public String classesPackage() {
        return notNull(CLASSES_PACKAGE);
    }

    public String classesPath() {
        return notNull(CLASSES_PATH);
    }

    private String notNull(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(String.format("There is no property associated with %s value", key));
        }
        return value;
    }
}
