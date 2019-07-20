package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class App {

    public static void main(String... args) throws Exception {
        new App().execute(Configuration.fromCmd(args));
    }

    public void execute(Configuration configuration) throws Exception {
        Database database = new Database(configuration.jdbcUrl, configuration.databaseUser,
            configuration.databasePassword);
        //database.setup();

        QueryFactory queryFactory = new SmartQueryFactory(database::connection, false);
        TableRepresentationFactory tablesFactory = new TableRepresentationFactory(configuration.classesPackage);
        List<Table> tables = new Tables(database.connection()).all();

        File classesFile = new File(configuration.classesPath);
        if (!(classesFile.exists() || classesFile.mkdirs())) {
            throw new RuntimeException(String.format("Can't create necessary %s directory",
                configuration.classesPath));
        }

        System.out.println(String.format("Generating %s db tables representations:",
            database.connection().getCatalog()));
        for (Table t : tables) {
            System.out.println(t.name);
            System.out.println(t.idName);
            MetaData meta = new MetaTable(queryFactory, t.name).data();
            String representation = tablesFactory.newRepresentation(meta);
            Files.write(new File(classesFile, meta.className + ".java").toPath(),
                representation.getBytes());
        }
    }
}