package com.iprogrammerr.smart.query.meta;

import java.util.List;
import java.util.Map;

public class MetaData {

    public final String className;
    public final List<String> columnsLabels;
    public final Map<String, String> fieldsTypes;

    public MetaData(String className, List<String> columnsLabels,
        Map<String, String> fieldsTypes) {
        this.className = className;
        this.columnsLabels = columnsLabels;
        this.fieldsTypes = fieldsTypes;
    }
}
