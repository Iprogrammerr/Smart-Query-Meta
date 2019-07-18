package com.iprogrammerr.smart.query.meta;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final String DATABASE_USER = "database.user";
    private static final String DATABASE_PASSWORD = "database.password";
    private static final String JDBC_URL = "jdbc.url";
    private static final String CLASSES_PACKAGE = "classes.package";
    private static final String CLASSES_PATH = "classes.path";
    public final String jdbcUrl;
    public final String databaseUser;
    public final String databasePassword;
    public final String classesPackage;
    public final String classesPath;

    public Configuration(String jdbcUrl, String databaseUser, String databasePassword, String classesPackage,
        String classesPath) {
        this.jdbcUrl = notNull(JDBC_URL, jdbcUrl);
        this.databaseUser = notNull(DATABASE_USER, databaseUser);
        this.databasePassword = notNull(DATABASE_PASSWORD, databasePassword);
        this.classesPackage = notNull(CLASSES_PACKAGE, classesPackage);
        this.classesPath = notNull(CLASSES_PATH, classesPath);
    }

    private static String notNull(String key, String value) {
        if (value == null) {
            throw new RuntimeException(String.format("%s property is required", key));
        }
        return value;
    }

    public static Configuration fromProperties(Properties properties) {
        return new Configuration(properties.getProperty(JDBC_URL), properties.getProperty(DATABASE_USER),
            properties.getProperty(DATABASE_PASSWORD), properties.getProperty(CLASSES_PACKAGE),
            properties.getProperty(CLASSES_PATH));
    }

    public static Configuration fromCmd(String... args) {
        try (InputStream is = args.length == 0 ? Configuration.class.getResourceAsStream("/application.properties")
            : new FileInputStream(args[0])) {
            Properties properties = new Properties();
            properties.load(is);
            return fromProperties(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Configuration{" +
            "jdbcUrl='" + jdbcUrl + '\'' +
            ", databaseUser='" + databaseUser + '\'' +
            ", databasePassword='" + databasePassword + '\'' +
            ", classesPackage='" + classesPackage + '\'' +
            ", classesPath='" + classesPath + '\'' +
            '}';
    }
}
