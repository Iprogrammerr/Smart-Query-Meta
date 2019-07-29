package com.iprogrammerr.smart.query.meta.factory;

import com.iprogrammerr.smart.query.meta.data.MetaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TablesRepresentationsFactory {

    private static final String BLOB_IMPORT = "import java.sql.Blob;";
    private static final String MAPPING_IMPORT = "import com.iprogrammerr.smart.query.mapping.clazz.Mapping;";
    private static final String BLOB = "Blob";
    private static final String CONSTANTS_MODIFIED = "public static final";
    private static final String CLASS_PREFIX = "public class";
    private static final String FIELD_MODIFIER = "public final";
    private static final String STRING = "String";
    private static final String SPACED_EQUAL = " = ";

    private final String packageName;

    public TablesRepresentationsFactory(String packageName) {
        this.packageName = packageName;
    }

    public String newRepresentation(MetaData data) {
        return new StringBuilder()
            .append(header(data.className, data.fieldsTypes.values().contains(BLOB), needsMapping(data)))
            .append(ClassElements.EMPTY_LINE)
            .append(fields(data))
            .append(ClassElements.EMPTY_LINE)
            .append(constructor(data.className, data.fieldsTypes))
            .append(ClassElements.NEW_LINE).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private boolean needsMapping(MetaData data) {
        boolean needsMapping = false;
        int i = 0;
        for (String f : data.fieldsTypes.keySet()) {
            if (!isFieldEqualToColumnLabel(f, data.columnsLabels.get(i))) {
                needsMapping = true;
                break;
            }
            i++;
        }
        return needsMapping;
    }

    private boolean isFieldEqualToColumnLabel(String field, String label) {
        return field.equalsIgnoreCase(label);
    }

    private String header(String className, boolean hasBlob, boolean needsMapping) {
        List<List<String>> importsGroups = new ArrayList<>();
        if (needsMapping) {
            importsGroups.add(Collections.singletonList(MAPPING_IMPORT));
        }
        if (hasBlob) {
            importsGroups.add(Collections.singletonList(BLOB_IMPORT));
        }
        return new StringBuilder()
            .append(ClassElements.prolog(packageName, importsGroups))
            .append(ClassElements.EMPTY_LINE)
            .append(CLASS_PREFIX).append(" ")
            .append(className).append(" ").append(ClassElements.START_CURLY_BRACKET)
            .toString();
    }

    private String fields(MetaData data) {
        StringBuilder builder = new StringBuilder();
        builder.append(constant(ClassElements.TABLE, data.tableName.toLowerCase()));
        for (String l : data.columnsLabels) {
            builder.append(ClassElements.NEW_LINE).append(constant(l.toUpperCase(), l.toLowerCase()));
        }
        int i = 0;
        builder.append(ClassElements.NEW_LINE);
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            String l = data.columnsLabels.get(i);
            if (!isFieldEqualToColumnLabel(e.getKey(), l)) {
                builder.append(ClassElements.NEW_LINE).append(ClassElements.TAB)
                    .append(mappingAnnotation(l));
            }
            i++;
            builder.append(ClassElements.NEW_LINE).append(ClassElements.TAB)
                .append(field(e.getValue(), e.getKey()));
        }
        return builder.toString();
    }

    private String mappingAnnotation(String label) {
        return String.format("@Mapping(%s)", label);
    }

    private String constant(String name, String value) {
        return new StringBuilder().append(ClassElements.TAB).append(CONSTANTS_MODIFIED)
            .append(" ").append(STRING).append(" ").append(name)
            .append(SPACED_EQUAL).append("\"").append(value).append("\"")
            .append(ClassElements.SEMICOLON)
            .toString();
    }

    private String field(String type, String name) {
        return new StringBuilder()
            .append(FIELD_MODIFIER).append(" ").append(type).append(" ").append(name)
            .append(ClassElements.SEMICOLON)
            .toString();
    }

    private String constructor(String className, Map<String, String> fieldsTypes) {
        StringBuilder builder = new StringBuilder()
            .append(ClassElements.TAB).append(ClassElements.PUBLIC_MODIFIER).append(" ").append(className)
            .append(ClassElements.START_BRACKET);

        List<String> args = fieldsTypes.entrySet().stream().map(e -> e.getValue() + " " + e.getKey())
            .collect(Collectors.toList());
        builder.append(ClassElements.argsInLines(builder.length(), args)).append(ClassElements.END_BRACKET).append(" ")
            .append(ClassElements.START_CURLY_BRACKET);

        for (String f : fieldsTypes.keySet()) {
            builder.append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB)
                .append(ClassElements.THIS).append(ClassElements.DOT).append(f).append(SPACED_EQUAL).append(f)
                .append(ClassElements.SEMICOLON);
        }
        return builder.append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }
}
