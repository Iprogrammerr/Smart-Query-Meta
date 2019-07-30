package com.iprogrammerr.smart.query.meta.factory;

import com.iprogrammerr.smart.query.meta.data.MetaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TablesRepresentationsFactory {

    private static final String BLOB_IMPORT = "import java.sql.Blob;";
    private static final String MAPPING_IMPORT = "import com.iprogrammerr.smart.query.mapping.clazz.Mapping;";
    private static final String OBJECTS_IMPORT = "import java.util.Objects;";
    private static final String BLOB = "Blob";
    private static final String CONSTANTS_MODIFIED = "public static final";
    private static final String CLASS_PREFIX = "public class";
    private static final String FIELD_MODIFIER = "public final";
    private static final String STRING = "String";
    private static final String SPACED_ASSIGNMENT = " = ";
    private static final String SPACED_EQUAL = " == ";
    private static final String OVERRIDE_ANNOTATION = "@Override";
    private static final String EQUALS_ARG = "object";
    private static final String EQUALS_CASTED_ARG = "other";
    private static final String AND = "&&";
    private static final Set<String> PRIMITIVES = new HashSet<>();

    static {
        PRIMITIVES.add("double");
        PRIMITIVES.add("float");
        PRIMITIVES.add("long");
        PRIMITIVES.add("int");
        PRIMITIVES.add("short");
        PRIMITIVES.add("char");
        PRIMITIVES.add("byte");
        PRIMITIVES.add("boolean");
    }

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
            .append(ClassElements.EMPTY_LINE)
            .append(equalsImplementation(data))
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
        importsGroups.add(Collections.singletonList(OBJECTS_IMPORT));
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
            .append(SPACED_ASSIGNMENT).append("\"").append(value).append("\"")
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
                .append(ClassElements.THIS).append(ClassElements.DOT).append(f).append(SPACED_ASSIGNMENT).append(f)
                .append(ClassElements.SEMICOLON);
        }
        return builder.append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String equalsImplementation(MetaData data) {
        return new StringBuilder()
            .append(ClassElements.TAB).append(OVERRIDE_ANNOTATION).append(ClassElements.NEW_LINE)
            .append(ClassElements.TAB).append(ClassElements.PUBLIC_MODIFIER)
            .append(String.format(" boolean equals(Object %s) ", EQUALS_ARG)).append(ClassElements.START_CURLY_BRACKET)
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB)
            .append("if (this == ").append(EQUALS_ARG).append(ClassElements.END_BRACKET)
            .append(ClassElements.SPACE).append(ClassElements.START_CURLY_BRACKET)
            .append(ClassElements.NEW_LINE).append(ClassElements.TRI_TAB).append(callReturn(Boolean.toString(true)))
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB).append(ClassElements.END_CURLY_BRACKET)
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB)
            .append("if (").append(EQUALS_ARG).append(" instanceof ").append(data.className).append(") ")
            .append(ClassElements.START_CURLY_BRACKET).append(ClassElements.NEW_LINE)
            .append(objectsComparison(data))
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB).append(ClassElements.END_CURLY_BRACKET)
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB).append(callReturn(Boolean.toString(false)))
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String objectsComparison(MetaData data) {
        StringBuilder builder = new StringBuilder()
            .append(ClassElements.TRI_TAB)
            .append(data.className).append(ClassElements.SPACE).append(EQUALS_CASTED_ARG).append(SPACED_ASSIGNMENT)
            .append(ClassElements.START_BRACKET).append(data.className).append(ClassElements.END_BRACKET)
            .append(ClassElements.SPACE).append(EQUALS_ARG).append(ClassElements.SEMICOLON)
            .append(ClassElements.NEW_LINE).append(ClassElements.TRI_TAB)
            .append(callReturn(fieldsComparison(data)));
        return builder.toString();
    }


    private String fieldsComparison(MetaData data) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            String f = e.getKey();
            String t = e.getValue();
            if (PRIMITIVES.contains(t)) {
                builder.append(f).append(SPACED_EQUAL).append(otherField(f));
            } else {
                builder.append(objectsEquals(f, otherField(f)));
            }
            if (i == (data.fieldsTypes.size() - 1)) {
                continue;
            }
            i++;
            builder.append(ClassElements.SPACE).append(AND).append(ClassElements.NEW_LINE)
                .append(ClassElements.TRI_TAB).append(ClassElements.TAB);

        }
        return builder.toString();
    }

    private String otherField(String field) {
        return String.format("%s.%s", EQUALS_CASTED_ARG, field);
    }

    private String objectsEquals(String first, String second) {
        return String.format("Objects.equals(%s, %s)", first, second);
    }

    private String callReturn(String arg) {
        return String.format("return %s;", arg);
    }
}
