package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.meta.data.MetaData;
import com.iprogrammerr.smart.query.meta.data.MetaTable;
import com.iprogrammerr.smart.query.meta.data.Table;
import com.iprogrammerr.smart.query.meta.data.Tables;
import com.iprogrammerr.smart.query.meta.factory.ActiveRecordsExtensionsFactory;
import com.iprogrammerr.smart.query.meta.factory.TablesRepresentationsFactory;

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

        QueryFactory queryFactory = new SmartQueryFactory(database::connection, false);
        TablesRepresentationsFactory tablesFactory = new TablesRepresentationsFactory(configuration.classesPackage);
        ActiveRecordsExtensionsFactory recordsFactory = new ActiveRecordsExtensionsFactory(
            configuration.classesPackage);
        List<Table> tables = new Tables(database.connection()).all();

        File classesFile = new File(configuration.classesPath);
        if (!(classesFile.exists() || classesFile.mkdirs())) {
            throw new RuntimeException(String.format("Can't create necessary %s directory",
                configuration.classesPath));
        }

        System.out.println(String.format("Generating %s db representation...",
            database.connection().getCatalog()));
        for (Table t : tables) {
            MetaData meta = new MetaTable(queryFactory, t.name).data();

            System.out.println(String.format("Table: %s", t.name));
            String representation = tablesFactory.newRepresentation(meta);
            saveClass(classesFile, meta.className, representation);

            if (configuration.generateActiveRecords) {
                System.out.println("...its ActiveRecord");
                String activeImpl = recordsFactory.newExtension(meta, t.metaId);
                saveClass(classesFile, recordsFactory.extensionName(meta.className), activeImpl);
            }
        }
    }

    private void saveClass(File root, String name, String classContent) throws Exception {
        Files.write(new File(root, name + ".java").toPath(),
            classContent.getBytes());
    }
}