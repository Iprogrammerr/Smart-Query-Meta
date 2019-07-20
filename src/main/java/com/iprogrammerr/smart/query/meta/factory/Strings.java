package com.iprogrammerr.smart.query.meta.factory;

public class Strings {

    public static final String TABLE = "TABLE";
    public static final String PACKAGE_PREFIX = "package";
    public static final String START_CURLY_BRACKET = "{";
    public static final String END_CURLY_BRACKET = "}";
    public static final String TAB = "\t";
    public static final String DOUBLE_TAB = TAB + TAB;
    public static final String NEW_LINE = "\n";
    public static final String EMPTY_LINE = NEW_LINE + NEW_LINE;
    public static final String START_BRACKET = "(";
    public static final String END_BRACKET = ")";
    public static final String SEMICOLON = ";";
    public static final String PUBLIC_MODIFIER = "public";
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String THIS = "this";
    public static final String FACTORY_NAME = "fromResult";
    public static final String CASED_SEPARATOR = "_";

    public static String capitalized(String string) {
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toUpperCase(string.charAt(0)));
        if (string.length() > 1) {
            builder.append(string.substring(1));
        }
        return builder.toString();
    }

    public static String toCamelCase(String string) {
        return toCase(string, CASED_SEPARATOR, false);
    }

    public static String toPascalCase(String string) {
        return toCase(string, CASED_SEPARATOR, true);
    }

    public static String toCase(String string, String separator, boolean pascalCase) {
        String[] parts = string.toLowerCase().split(separator);
        StringBuilder builder = new StringBuilder();
        String first = parts[0];
        if (pascalCase) {
            builder.append(capitalized(first));
        } else {
            builder.append(first);
        }
        for (int i = 1; i < parts.length; i++) {
            builder.append(Strings.capitalized(parts[i]));
        }
        return builder.toString();
    }
}
