package com.iprogrammerr.smart.query.meta.factory;

import java.util.List;

public class ClassElements {

    public static final int MAX_LINE_LENGTH = 120;
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
            builder.append(ClassElements.capitalized(parts[i]));
        }
        return builder.toString();
    }

    public static String classProlog(String packageName, List<String> imports) {
        StringBuilder builder = new StringBuilder()
            .append(PACKAGE_PREFIX).append(" ").append(packageName)
            .append(SEMICOLON);
        if (!imports.isEmpty()) {
            builder.append(NEW_LINE);
            for (String i : imports) {
                builder.append(NEW_LINE).append(i);
            }
        }
        return builder.toString();
    }

    public static String argsInLines(int firstLineOffset, List<String> args, int lineMaxLength) {
        StringBuilder builder = new StringBuilder();
        builder.append(args.get(0));
        int previousLineLength = 0;
        for (int i = 1; i < args.size(); i++) {
            builder.append(COMMA).append(" ");
            String arg = args.get(i);
            boolean newLine = (firstLineOffset + builder.length() + arg.length() - previousLineLength) > lineMaxLength;
            if (newLine) {
                previousLineLength = builder.length();
                firstLineOffset = 0;
                builder.append(NEW_LINE).append(DOUBLE_TAB);
            }
            builder.append(arg);

        }
        return builder.toString();
    }

    public static String argsInLines(int firstLineOffset, List<String> args) {
        return argsInLines(firstLineOffset, args, MAX_LINE_LENGTH);
    }

    public static String argsInLines(List<String> args) {
        return argsInLines(0, args);
    }
}
