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

    private QueryFactory factory;

    @Before
    public void setup() throws Exception {
        Configuration configuration = Configuration.fromCmd();
        Database database = new Database(configuration.jdbcUrl, configuration.databaseUser,
            configuration.databasePassword);
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

        MatcherAssert.assertThat(ar.fetch(), Matchers.equalTo(new Author(id, name, surname, alias, alive)));
    }


    @Test
    public void updates() {
        String name = "Stanisław";
        String surname = "Lem";
        String alias = "Futurysta";
        int alive = 0;

        AuthorRecord ar = new AuthorRecord(factory)
            .setName(name).setSurname(surname)
            .setAlias(alias).setAlive(alive);

        int id = (int) ar.insertReturningId();
        String newName = "Stasiek";
        int newAlive = 1;
        ar.setName(newName);
        ar.setAlive(newAlive);

        ar.update();

        MatcherAssert.assertThat(ar.fetch(), Matchers.equalTo(new Author(id, newName, surname, alias, newAlive)));
    }

    @Test
    public void deletes() {
        AuthorRecord ar = new AuthorRecord(factory)
            .setName("a").setSurname("b")
            .setAlias("c").setAlive(1);

        long id = ar.insertReturningId();
        ar.delete();

        boolean noRecords = factory.newQuery()
            .sql("SELECT id FROM author WHERE id = ?")
            .set(id)
            .fetch(r -> !r.next());

        MatcherAssert.assertThat(noRecords, Matchers.equalTo(true));
    }

}
