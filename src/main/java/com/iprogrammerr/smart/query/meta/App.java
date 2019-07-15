package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;

import java.util.List;

public class App {

    public static void main(String... args) {
        TestDatabaseSetup setup = new TestDatabaseSetup();
        setup.setup();

        QueryFactory queryFactory = new SmartQueryFactory(setup::connection, false);
        String classesPath = App.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        TableRepresentationFactory tablesFactory = new TableRepresentationFactory(App.class.getPackage().getName());
        List<String> tables = new Tables(setup.connection()).all();

        for (String t : tables) {
            System.out.println("TABLE: " + t);
            MetaTable meta = new MetaTable(queryFactory, t);
            System.out.println(tablesFactory.newRepresentation(meta.data()));
            System.out.println();
        }
    }
}
