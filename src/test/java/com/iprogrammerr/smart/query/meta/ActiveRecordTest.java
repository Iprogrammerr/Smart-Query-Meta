package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.meta.record.Author;
import com.iprogrammerr.smart.query.meta.record.AuthorRecord;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ActiveRecordTest {

    private Database database;
    private QueryFactory factory;

    @Before
    public void setup() throws Exception {
        Configuration configuration = Configuration.fromCmd();
        database = new Database(configuration.jdbcUrl, configuration.databaseUser, configuration.databasePassword);
        factory = new SmartQueryFactory(database::connection, false);
        database.setup();
    }

    @Test
    public void insertsAndFetches() {
        String name = "Adam";
        String surname = "Mickiewicz";
        String alias = "Wieszcz";
        int alive = 0;

        AuthorRecord ar = new AuthorRecord(factory)
            .setName(name).setSurname(surname)
            .setAlias(alias).setAlive(alive);

        int id = (int) ar.insertReturningId();

        Author a = ar.fetch();

        MatcherAssert.assertThat(a, Matchers.equalTo(new Author(id, name, surname, alias, alive)));
    }

}
