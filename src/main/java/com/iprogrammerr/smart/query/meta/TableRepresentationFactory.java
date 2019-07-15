package com.iprogrammerr.smart.query.meta;

import java.util.Map;

public class TableRepresentationFactory {

    private static final String CONSTANTS_MODIFIED = "public static final";
    private static final String PACKAGE_PREFIX = "package";
    private static final String CLASS_PREFIX = "public class";
    private static final String START_CURLY_BRACKET = "{";
    private static final String END_CURLY_BRACKET = "}";
    private static final String TAB = "\t";
    private static final String NEW_LINE = "\n";
    private static final String EMPTY_LINE = NEW_LINE + NEW_LINE;
    private static final String FIELD_MODIFIER = "public final";
    private static final String START_BRACKET = "(";
    private static final String END_BRACKET = ")";
    private static final String SEMICOLON = ";";
    private static final String PUBLIC_MODIFIER = "public";
    private static final String COMMA = ",";
    private static final String DOT = ".";
    private static final String EQUAL = " = ";
    private static final String THIS = "this";

    private final String packageName;

    public TableRepresentationFactory(String packageName) {
        this.packageName = packageName;
    }

    public String newRepresentation(MetaData data) {
        return header(data.className) + fields(data) + constructors(data.className, data.fieldsTypes);
    }

    private String header(String className) {
        return new StringBuilder()
            .append(PACKAGE_PREFIX).append(" ").append(packageName).append(SEMICOLON)
            .append(EMPTY_LINE)
            .append(CLASS_PREFIX).append(" ").append(className).append(" ").append(START_CURLY_BRACKET)
            .append(EMPTY_LINE).toString();
    }

    private String fields(MetaData data) {
        StringBuilder fields = new StringBuilder();
        for (String l : data.columnsLabels) {
            fields.append(TAB).append(CONSTANTS_MODIFIED)
                .append(" String ").append(l.toUpperCase())
                .append(" = ").append("\"").append(l.toLowerCase()).append("\"")
                .append(SEMICOLON).append(NEW_LINE);
        }
        fields.append(NEW_LINE);
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            fields.append(TAB).append(FIELD_MODIFIER).append(" ").append(e.getValue())
                .append(" ").append(e.getKey()).append(SEMICOLON).append(NEW_LINE);
        }
        fields.append(NEW_LINE);
        return fields.toString();
    }

    private String constructors(String className, Map<String, String> fieldsTypes) {
        StringBuilder constructors = new StringBuilder();
        constructors.append(TAB).append(PUBLIC_MODIFIER).append(" ").append(className).append(START_BRACKET);
        for (Map.Entry<String, String> e : fieldsTypes.entrySet()) {
            constructors.append(e.getValue()).append(" ").append(e.getKey()).append(COMMA).append(" ");
        }
        int length = constructors.length();
        constructors.replace(length - 2, length, END_BRACKET);
        constructors.append(" ").append(START_CURLY_BRACKET);
        for (String f : fieldsTypes.keySet()) {
            constructors.append(NEW_LINE).append(TAB).append(TAB)
                .append(THIS).append(DOT).append(f).append(EQUAL).append(f).append(SEMICOLON);
        }
        constructors.append(NEW_LINE).append(TAB).append(END_CURLY_BRACKET)
            .append(NEW_LINE).append(END_CURLY_BRACKET);
        return constructors.toString();
    }
}
