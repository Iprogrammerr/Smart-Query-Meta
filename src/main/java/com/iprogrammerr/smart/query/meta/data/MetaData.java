package com.iprogrammerr.smart.query.meta.data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MetaData {

    public final String tableName;
    public final String className;
    public final List<String> columnsLabels;
    public final Map<String, String> fieldsTypes;

    public MetaData(String tableName, String className, List<String> columnsLabels, Map<String, String> fieldsTypes) {
        this.tableName = tableName;
        this.className = className;
        this.columnsLabels = columnsLabels;
        this.fieldsTypes = fieldsTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaData metaData = (MetaData) o;
        return Objects.equals(tableName, metaData.tableName) &&
            Objects.equals(className, metaData.className) &&
            Objects.equals(columnsLabels, metaData.columnsLabels) &&
            Objects.equals(fieldsTypes, metaData.fieldsTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, className, columnsLabels, fieldsTypes);
    }
}
