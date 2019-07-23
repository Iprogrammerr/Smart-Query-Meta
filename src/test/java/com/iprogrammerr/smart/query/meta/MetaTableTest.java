package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.meta.data.MetaData;
import com.iprogrammerr.smart.query.meta.data.MetaTable;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MetaTableTest {

    private QueryFactory factory;

    @Before
    public void setup() throws Exception {
        Configuration configuration = Configuration.fromCmd();
        Database database = new Database(configuration.jdbcUrl, configuration.databaseUser,
            configuration.databasePassword);
        database.setup();
        factory = new SmartQueryFactory(database::connection, false);
    }

    @Test
    public void readsMetaData() {
        MetaTable table = new MetaTable(factory, "AUTHOR");
        MatcherAssert.assertThat(table.data(), Matchers.equalTo(authorMetaData()));

        table = new MetaTable(factory, "ORGANISM");
        MatcherAssert.assertThat(table.data(), Matchers.equalTo(organismMetaData()));
    }

    private MetaData authorMetaData() {
        Map<String, String> fieldsTypes = new HashMap<>();
        fieldsTypes.put("id", "Integer");
        fieldsTypes.put("name", "String");
        fieldsTypes.put("surname", "String");
        fieldsTypes.put("alias", "String");
        fieldsTypes.put("alive", "Byte");
        return new MetaData("AUTHOR", "Author", Arrays.asList("ID", "NAME",
            "SURNAME", "ALIAS", "ALIVE"), fieldsTypes, new HashSet<>());
    }

    private MetaData organismMetaData() {
        Map<String, String> fieldsTypes = new HashMap<>();
        fieldsTypes.put("dna", "String");
        fieldsTypes.put("name", "String");
        return new MetaData("ORGANISM", "Organism", Arrays.asList("DNA", "NAME"), fieldsTypes,
            Collections.singleton("name"));
    }

    @Test
    public void throwsExceptionForWrongTable() {
        boolean thrown = false;
        try {
            new MetaTable(factory, "abd").data();
        } catch (Exception e) {
            thrown = true;
        }
        MatcherAssert.assertThat(thrown, Matchers.equalTo(true));
    }
}
