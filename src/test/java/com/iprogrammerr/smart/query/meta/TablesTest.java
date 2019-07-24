package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.meta.data.MetaId;
import com.iprogrammerr.smart.query.meta.data.Table;
import com.iprogrammerr.smart.query.meta.data.Tables;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TablesTest {

    private Database database;

    @Before
    public void setup() throws Exception {
        Configuration configuration = Configuration.fromCmd();
        database = new Database(configuration.jdbcUrl, configuration.databaseUser, configuration.databasePassword);
        database.setup();
    }

    @Test
    public void readsTables() throws Exception {
        Tables tables = new Tables(database.connection());
        Table[] expected = {
            new Table("AUTHOR", new MetaId("ID", true, false)),
            new Table("BOOK", new MetaId("ID", true, false)),
            new Table("ORGANISM", new MetaId("DNA", false, false))
        };
        List<Table> actual = tables.all();
        MatcherAssert.assertThat(actual, Matchers.contains(expected));
    }

}
