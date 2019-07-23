package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.meta.data.MetaTable;
import com.iprogrammerr.smart.query.meta.data.Tables;
import com.iprogrammerr.smart.query.meta.factory.TablesRepresentationsFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TablesRepresentationsFactoryTest {

    private Database database;
    private QueryFactory queryFactory;
    private TablesRepresentationsFactory factory;

    @Before
    public void setup() throws Exception {
        Configuration configuration = Configuration.fromCmd();
        database = new Database(configuration.jdbcUrl, configuration.databaseUser, configuration.databasePassword);
        queryFactory = new SmartQueryFactory(database::connection, false);
        factory = new TablesRepresentationsFactory(configuration.classesPackage);
        database.setup();
    }

    @Test
    public void generatesTables() throws Exception {
        List<String> tables = new Tables(database.connection()).all().stream()
            .map(t -> t.name).collect(Collectors.toList());
        generatesTable(tables.get(0), "Author");
        generatesTable(tables.get(1), "Book");
    }

    private void generatesTable(String table, String className) throws Exception {
        String expectedTable = new JavaFile(className).content();
        String actualTable = factory.newRepresentation(new MetaTable(queryFactory, table).data());
        MatcherAssert.assertThat(actualTable, Matchers.equalTo(expectedTable));
    }
}
