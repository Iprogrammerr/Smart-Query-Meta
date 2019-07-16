package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.meta.table.Author;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class App {

    public static void main(String... args) throws Exception {
        Configuration configuration = Configuration.fromCmd(args);

        TestDatabaseSetup setup = new TestDatabaseSetup(configuration.jdbcUrl(), configuration.databaseUser(),
            configuration.databasePassword());
        setup.setup();

        QueryFactory queryFactory = new SmartQueryFactory(setup::connection, false);
        //String classesPath = App.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        TableRepresentationFactory tablesFactory = new TableRepresentationFactory(configuration.classesPackage());
        List<String> tables = new Tables(setup.connection()).all();

        File classesFile = new File(configuration.classesPath());
        if (!(classesFile.exists() || classesFile.mkdirs())) {
            throw new RuntimeException(String.format("Can't create necessary %s directory",
                configuration.classesPath()));
        }

        for (String t : tables) {
            System.out.println("TABLE: " + t);
            MetaData meta = new MetaTable(queryFactory, t).data();
            String representation = tablesFactory.newRepresentation(meta);
            Files.write(new File(classesFile, meta.className + ".java").toPath(),
                representation.getBytes());
            System.out.println(representation);
            System.out.println();
        }

        queryFactory.newQuery().dsl()
            .insertInto(Author.TABLE)
            .columns(Author.NAME, Author.SURNAME, Author.ALIAS, Author.ALIVE)
            .values("Adam", "Mickiewicz", "Wieszcz", 0)
            .query()
            .execute();

        List<Author> authors = queryFactory.newQuery().dsl()
            .selectAll().from(Author.TABLE)
            .query()
            .fetch(Author::fromListResult);

        System.out.println(authors);
    }
}
