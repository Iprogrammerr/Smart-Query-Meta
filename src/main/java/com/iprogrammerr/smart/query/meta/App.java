package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.meta.data.MetaData;
import com.iprogrammerr.smart.query.meta.data.MetaTable;
import com.iprogrammerr.smart.query.meta.data.Table;
import com.iprogrammerr.smart.query.meta.data.Tables;
import com.iprogrammerr.smart.query.meta.factory.ActiveRecordImplFactory;
import com.iprogrammerr.smart.query.meta.factory.TableRepresentationFactory;

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
        TableRepresentationFactory tablesFactory = new TableRepresentationFactory(configuration.classesPackage);
        ActiveRecordImplFactory implFactory = new ActiveRecordImplFactory(configuration.classesPackage);
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

            MetaData meta = new MetaTable(queryFactory, t.name).data();

            String representation = tablesFactory.newRepresentation(meta);
            String activeImpl = implFactory.newImplementation(meta, t.idInfo);

            saveClass(classesFile, meta.className, representation);
            saveClass(classesFile, implFactory.implName(meta.className), activeImpl);
        }
    }

    private void saveClass(File root, String name, String classContent) throws Exception {
        Files.write(new File(root, name + ".java").toPath(),
            classContent.getBytes());
    }
}