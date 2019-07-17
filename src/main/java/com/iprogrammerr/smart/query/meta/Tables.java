package com.iprogrammerr.smart.query.meta;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Tables {

    private static final String TYPE = "TABLE";
    private static final String TABLE_KEY = "TABLE_NAME";
    private final Connection connection;

    public Tables(Connection connection) {
        this.connection = connection;
    }

    public List<String> all() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{TYPE});
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(rs.getString(TABLE_KEY));
            }
            return tables;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
