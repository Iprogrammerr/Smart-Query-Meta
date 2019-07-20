package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.meta.meta.MetaTable;
import com.iprogrammerr.smart.query.meta.factory.TableRepresentationFactory;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TableRepresentationFactoryTest {

    private Database database;
    private QueryFactory queryFactory;
    private TableRepresentationFactory factory;

    @Before
    public void setup() throws Exception {
        Configuration configuration = Configuration.fromCmd();
        database = new Database(configuration.jdbcUrl, configuration.databaseUser, configuration.databasePassword);
        queryFactory = new SmartQueryFactory(database::connection, false);
        factory = new TableRepresentationFactory(configuration.classesPackage);
        database.setup();
    }

    @Test
    public void generatesTables() throws Exception {
        List<String> tables = new Tables(database.connection()).all().stream()
            .map(t -> t.name).collect(Collectors.toList());

        String expectedTable = tableFormula("Author");
        assertEquals(expectedTable, factory.newRepresentation(new MetaTable(queryFactory, tables.get(0)).data()));

        expectedTable = tableFormula("Book");
        assertEquals(expectedTable, factory.newRepresentation(new MetaTable(queryFactory, tables.get(1)).data()));
    }

    private String tableFormula(String table) throws Exception {
        return new String(Files.readAllBytes(Paths.get(getClass()
            .getResource(String.format("/%s.java", table)).toURI())));
    }
}
