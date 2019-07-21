package com.iprogrammerr.smart.query.meta.data;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.meta.factory.Strings;

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
        TYPES_MAPPING.put("Boolean", "boolean");
        TYPES_MAPPING.put("Byte", "int");
        TYPES_MAPPING.put("Short", "int");
        TYPES_MAPPING.put("Integer", "int");
        TYPES_MAPPING.put("Long", "long");
        TYPES_MAPPING.put("BigInteger", "long");
        TYPES_MAPPING.put("Float", "float");
        TYPES_MAPPING.put("Double", "double");
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
            .fetch(r -> {
                List<String> columnLabels = new ArrayList<>();
                Map<String, String> ft = new LinkedHashMap<>();
                ResultSetMetaData meta = r.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String label = meta.getColumnLabel(i);
                    columnLabels.add(label);
                    String field = Strings.toCamelCase(label);
                    String type = typeName(meta.getColumnClassName(i));
                    ft.put(field, type);
                }
                return new MetaData(table, Strings.toPascalCase(table), columnLabels, ft);
            });
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
