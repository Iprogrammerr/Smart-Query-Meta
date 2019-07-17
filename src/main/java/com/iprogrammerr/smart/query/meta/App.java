package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import org.h2.tools.RunScript;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class App {

    public static void main(String... args) throws Exception {
        Configuration configuration = Configuration.fromCmd(args);

        Connection connection = DriverManager.getConnection(configuration.jdbcUrl(), configuration.databaseUser(),
            configuration.databasePassword());

        QueryFactory queryFactory = new SmartQueryFactory(() -> connection, false);
        TableRepresentationFactory tablesFactory = new TableRepresentationFactory(configuration.classesPackage());
        List<String> tables = new Tables(connection).all();

        File classesFile = new File(configuration.classesPath());
        if (!(classesFile.exists() || classesFile.mkdirs())) {
            throw new RuntimeException(String.format("Can't create necessary %s directory",
                configuration.classesPath()));
        }

        System.out.println(String.format("Generating %s db tables representations:", connection.getCatalog()));
        for (String t : tables) {
            System.out.println(t);
            MetaData meta = new MetaTable(queryFactory, t).data();
            String representation = tablesFactory.newRepresentation(meta);
            Files.write(new File(classesFile, meta.className + ".java").toPath(),
                representation.getBytes());
        }
    }

    private static void setupDb(Connection connection) throws Exception {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
            App.class.getResourceAsStream("/schema.sql")))) {
            RunScript.execute(connection, r);
        }
    }
}
