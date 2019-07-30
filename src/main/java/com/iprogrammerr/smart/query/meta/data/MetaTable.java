package com.iprogrammerr.smart.query.meta.data;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.meta.factory.ClassElements;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MetaTable {

    private static final Map<String, String> TYPES_MAPPING = new HashMap<>();
    private static final String CLASS_PARTS_SEPARATOR = ".";

    static {
        TYPES_MAPPING.put("BigInteger", "Long");
    }

    private final QueryFactory factory;
    private final String table;

    public MetaTable(QueryFactory factory, String table) {
        this.factory = factory;
        this.table = table;
    }

    public MetaData data() {
        return factory.newQuery().dsl()
            .selectAll().from(table).limit(1)
            .query()
            .fetch(this::dataFromResult);
    }

    private MetaData dataFromResult(ResultSet result) throws Exception {
        List<String> columnLabels = new ArrayList<>();
        Map<String, String> fieldsTypes = new LinkedHashMap<>();
        ResultSetMetaData meta = result.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String label = meta.getColumnLabel(i);
            columnLabels.add(label);
            String field = ClassElements.toCamelCase(label);
            String type = typeName(meta.getColumnClassName(i));
            if (meta.isNullable(i) == ResultSetMetaData.columnNoNulls) {
                type = nonNullableType(type);
            }
            fieldsTypes.put(field, type);
        }
        return new MetaData(table, ClassElements.toPascalCase(table), columnLabels, fieldsTypes);
    }

    private String nonNullableType(String type) {
        Primitive primitive = Primitive.translate(type);
        String converted;
        if (primitive == Primitive.UNKNOWN) {
            converted = type;
        } else {
            converted = primitive.name().toLowerCase();
        }
        return converted;
    }

    private String typeName(String className) {
        int idx = className.lastIndexOf(CLASS_PARTS_SEPARATOR);
        String type;
        if (idx >= 0) {
            type = className.substring(idx + 1);
        } else {
            type = className;
        }
        return TYPES_MAPPING.getOrDefault(type, type);
    }
}
