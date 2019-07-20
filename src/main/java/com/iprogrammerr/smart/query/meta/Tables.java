package com.iprogrammerr.smart.query.meta;

import com.iprogrammerr.smart.query.SmartQuery;

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
                tables.add(tableFromResult(metaData, rs));
            }
            return tables;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Table tableFromResult(DatabaseMetaData metaData, ResultSet result) throws Exception {
        String table = result.getString(TABLE_KEY);
        ResultSet idRs = metaData.getPrimaryKeys(null, null, table);
        idRs.next();
        String idColumn = idRs.getString(ID_KEY);
        boolean autoIncrement = hasAutoIncrementId(table, idColumn);
        return new Table(table, new IdInfo(idColumn, autoIncrement));
    }

    private boolean hasAutoIncrementId(String table, String idColumn) {
        return new SmartQuery(connection, false).dsl()
            .select(idColumn).from(table)
            .query()
            .fetch(r -> r.getMetaData().isAutoIncrement(1));
    }
}
