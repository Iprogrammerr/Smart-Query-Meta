package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.meta.data.MetaTable;
import com.iprogrammerr.smart.query.meta.data.Table;
import com.iprogrammerr.smart.query.meta.data.Tables;
import com.iprogrammerr.smart.query.meta.factory.ActiveRecordsExtensionsFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ActiveRecordsExtensionsTest {

    private Database database;
    private QueryFactory queryFactory;
    private ActiveRecordsExtensionsFactory factory;

    @Before
    public void setup() throws Exception {
        Configuration configuration = Configuration.fromCmd();
        database = new Database(configuration.jdbcUrl, configuration.databaseUser, configuration.databasePassword);
        queryFactory = new SmartQueryFactory(database::connection, false);
        factory = new ActiveRecordsExtensionsFactory(configuration.classesPackage);
        database.setup();
    }

    @Test
    public void generatesExtensions() throws Exception {
        List<Table> tables = new Tables(database.connection()).all();
        generatesExtension(tables.get(0), "AuthorRecord");
        generatesExtension(tables.get(1), "BookRecord");
    }

    private void generatesExtension(Table table, String className) throws Exception {
        String expectedExtension = new JavaFile(className).content();
        String actualExtension = factory.newExtension(new MetaTable(queryFactory, table.name).data(), table.metaId);
        MatcherAssert.assertThat(expectedExtension, Matchers.equalTo(actualExtension));
    }
}
