package com.iprogrammerr.smart.query.meta.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetaData {

    public final String tableName;
    public final String className;
    public final List<String> columnsLabels;
    public final Map<String, String> fieldsTypes;
    public final Set<String> nullableFields;

    public MetaData(String tableName, String className, List<String> columnsLabels,
        Map<String, String> fieldsTypes, Set<String> nullableFields) {
        this.tableName = tableName;
        this.className = className;
        this.columnsLabels = columnsLabels;
        this.fieldsTypes = fieldsTypes;
        this.nullableFields = nullableFields;
    }
}
