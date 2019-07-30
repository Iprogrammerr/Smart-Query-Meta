package com.iprogrammerr.smart.query.meta.factory;

import java.util.List;

public class ClassElements {

    public static final int MAX_LINE_LENGTH = 120;
    public static final String TABLE = "TABLE";
    public static final String PACKAGE_PREFIX = "package";
    public static final String START_CURLY_BRACKET = "{";
    public static final String END_CURLY_BRACKET = "}";
    public static final String SPACE = " ";
    public static final String TAB = SPACE + SPACE + SPACE + SPACE;
    public static final String DOUBLE_TAB = TAB + TAB;
    public static final String TRI_TAB = DOUBLE_TAB + TAB;
    public static final String NEW_LINE = "\n";
    public static final String EMPTY_LINE = NEW_LINE + NEW_LINE;
    public static final String START_BRACKET = "(";
    public static final String END_BRACKET = ")";
    public static final String SEMICOLON = ";";
    public static final String PUBLIC_MODIFIER = "public";
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String THIS = "this";
    public static final String CASED_SEPARATOR = "_";

    public static String capitalized(String element) {
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toUpperCase(element.charAt(0)));
        if (element.length() > 1) {
            builder.append(element.substring(1));
        }
        return builder.toString();
    }

    public static String toCamelCase(String element) {
        return toCase(element, CASED_SEPARATOR, false);
    }

    public static String toPascalCase(String element) {
        return toCase(element, CASED_SEPARATOR, true);
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
            builder.append(ClassElements.capitalized(parts[i]));
        }
        return builder.toString();
    }

    public static String prolog(String packageName, List<List<String>> importsGroups) {
        StringBuilder builder = new StringBuilder()
            .append(PACKAGE_PREFIX).append(" ").append(packageName)
            .append(SEMICOLON);
        if (!importsGroups.isEmpty()) {
            for (List<String> g : importsGroups) {
                builder.append(NEW_LINE);
                for (String i : g) {
                    builder.append(NEW_LINE).append(i);
                }
            }
        }
        return builder.toString();
    }

    public static String argsInLines(int firstLineOffset, List<String> args, int lineMaxLength, int lineTabs) {
        StringBuilder builder = new StringBuilder();
        builder.append(args.get(0));
        int previousLineLength = 0;
        for (int i = 1; i < args.size(); i++) {
            builder.append(COMMA);
            String arg = args.get(i);
            boolean newLine = (firstLineOffset + builder.length() + arg
                .length() - previousLineLength) > lineMaxLength;
            if (newLine) {
                previousLineLength = builder.length();
                firstLineOffset = 0;
                builder.append(NEW_LINE);
                for (int j = 0; j < lineTabs; j++) {
                    builder.append(TAB);
                }
            } else {
                builder.append(" ");
            }
            builder.append(arg);

        }
        return builder.toString();
    }

    public static String argsInLines(int firstLineOffset, List<String> args) {
        return argsInLines(firstLineOffset, args, MAX_LINE_LENGTH, 2);
    }
}
