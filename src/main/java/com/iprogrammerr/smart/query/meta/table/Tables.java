package com.iprogrammerr.smart.query.meta.table;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Tables {

    private static final String TYPE = "TABLE";
    private static final String TABLE_KEY = "TABLE_NAME";
    private static final String ID_KEY = "COLUMN_NAME";
    private static final String ALL_PATTERN = "%";
    private final Connection connection;

    public Tables(Connection connection) {
        this.connection = connection;
    }

    public List<Table> all() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(connection.getCatalog(), null, ALL_PATTERN, new String[]{TYPE});
            List<Table> tables = new ArrayList<>();
            while (rs.next()) {
                String table = rs.getString(TABLE_KEY);
                ResultSet idRs = metaData.getPrimaryKeys(null, null, table);
                idRs.next();
                tables.add(new Table(table, idRs.getString(ID_KEY)));
            }
            return tables;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
