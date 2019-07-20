package com.iprogrammerr.smart.query.meta.factory;

import com.iprogrammerr.smart.query.meta.meta.MetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableRepresentationFactory {

    private static final int MAX_ARG_LINE_SIZE = 100;
    private static final List<String> IMPORTS = Arrays.asList("import java.sql.ResultSet;", "import java.util.List;",
        "import java.util.ArrayList;");
    private static final String BLOB_IMPORT = "import java.sql.Blob;";
    private static final String BLOB = "Blob";
    private static final String CONSTANTS_MODIFIED = "public static final";
    private static final String CLASS_PREFIX = "public class";
    private static final String FIELD_MODIFIER = "public final";
    private static final String TABLE = "TABLE";
    private static final String STRING = "String";
    private static final String SPACED_EQUAL = " = ";
    private static final String FACTORIES_MODIFIER = "public static";
    private static final String LIST_FACTORY_NAME = "listFromResult";
    private static final String RESULT_SET = "ResultSet";
    private static final String RESULT_SET_ARG_NAME = "result";
    private static final String THROWS_EXCEPTION = "throws Exception";
    private static final String FACTORY_ARG_SUFFIX = "Label";
    private static final String LIST_NAME = "list";
    private static final String INITIALIZED_LIST = "new ArrayList<>();";
    private final String packageName;

    public TableRepresentationFactory(String packageName) {
        this.packageName = packageName;
    }

    public String newRepresentation(MetaData data) {
        return new StringBuilder()
            .append(header(data.className, data.fieldsTypes.values().contains(BLOB)))
            .append(fields(data))
            .append(constructor(data.className, data.fieldsTypes))
            .append(factories(data))
            .append(Strings.NEW_LINE).append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String header(String className, boolean hasBlob) {
        StringBuilder builder = new StringBuilder()
            .append(Strings.PACKAGE_PREFIX).append(" ").append(packageName).append(Strings.SEMICOLON)
            .append(Strings.EMPTY_LINE);
        for (String i : IMPORTS) {
            builder.append(i).append(Strings.NEW_LINE);
        }
        if (hasBlob) {
            builder.append(BLOB_IMPORT).append(Strings.NEW_LINE);
        }
        return builder.append(Strings.NEW_LINE)
            .append(CLASS_PREFIX).append(" ").append(className).append(" ").append(Strings.START_CURLY_BRACKET)
            .append(Strings.EMPTY_LINE).toString();
    }

    private String fields(MetaData data) {
        StringBuilder builder = new StringBuilder();
        builder.append(constant(TABLE, data.tableName));
        for (String l : data.columnsLabels) {
            builder.append(constant(l.toUpperCase(), l.toLowerCase()));
        }
        builder.append(Strings.NEW_LINE);
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            builder.append(Strings.TAB).append(FIELD_MODIFIER).append(" ").append(e.getValue())
                .append(" ").append(e.getKey()).append(Strings.SEMICOLON).append(Strings.NEW_LINE);
        }
        builder.append(Strings.NEW_LINE);
        return builder.toString();
    }

    private String constant(String name, String value) {
        return new StringBuilder().append(Strings.TAB).append(CONSTANTS_MODIFIED)
            .append(" ").append(STRING).append(" ").append(name)
            .append(SPACED_EQUAL).append("\"").append(value).append("\"")
            .append(Strings.SEMICOLON).append(Strings.NEW_LINE)
            .toString();
    }

    private String constructor(String className, Map<String, String> fieldsTypes) {
        StringBuilder builder = new StringBuilder();
        builder.append(Strings.TAB).append(Strings.PUBLIC_MODIFIER).append(" ").append(className)
            .append(Strings.START_BRACKET);
        List<String> args = fieldsTypes.entrySet().stream().map(e -> e.getValue() + " " + e.getKey())
            .collect(Collectors.toList());
        builder.append(methodArgs(builder.length(), args)).append(Strings.END_BRACKET).append(" ")
            .append(Strings.START_CURLY_BRACKET);
        for (String f : fieldsTypes.keySet()) {
            builder.append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB)
                .append(Strings.THIS).append(Strings.DOT).append(f).append(SPACED_EQUAL).append(f).append(Strings.SEMICOLON);
        }
        return builder.append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.END_CURLY_BRACKET).toString();
    }

    private String methodArgs(int firstLineOffset, List<String> args) {
        StringBuilder builder = new StringBuilder();
        builder.append(args.get(0));
        int previousLineLength = 0;
        for (int i = 1; i < args.size(); i++) {
            builder.append(Strings.COMMA).append(" ");
            if (((firstLineOffset + builder.length()) - previousLineLength) > MAX_ARG_LINE_SIZE) {
                builder.append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB);
                previousLineLength = builder.length();
                firstLineOffset = 0;
            }
            builder.append(args.get(i));

        }
        return builder.toString();
    }

    private String factories(MetaData data) {
        return new StringBuilder()
            .append(Strings.EMPTY_LINE)
            .append(singleAliasedFactory(data))
            .append(Strings.EMPTY_LINE)
            .append(singleFactory(data))
            .append(Strings.EMPTY_LINE)
            .append(aliasedListFactory(data))
            .append(Strings.EMPTY_LINE)
            .append(listFactory(data))
            .toString();
    }

    private String singleAliasedFactory(MetaData data) {
        StringBuilder builder = new StringBuilder();
        builder.append(factoryPrefix(false, data.className))
            .append(aliasedFactoryArgs(builder.length(), data.fieldsTypes))
            .append(Strings.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(Strings.START_CURLY_BRACKET).append(Strings.NEW_LINE);
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            builder.append(fieldInitialization(e.getValue() + " " + e.getKey(),
                resultSetInvocation(e.getValue(), aliased(e.getKey()))))
                .append(Strings.NEW_LINE);
        }
        int offset = builder.length();
        return builder.append(Strings.DOUBLE_TAB).append("return new ").append(data.className)
            .append(Strings.START_BRACKET)
            .append(methodArgs(builder.length() - offset, new ArrayList<>(data.fieldsTypes.keySet())))
            .append(Strings.END_BRACKET).append(Strings.SEMICOLON)
            .append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String aliasedFactoryArgs(int firstLineOffset, Map<String, String> fieldsTypes) {
        List<String> args = new ArrayList<>();
        args.add(resultSetArg());
        args.addAll(fieldsTypes.keySet().stream().map(f -> STRING + " " + aliased(f))
            .collect(Collectors.toList()));
        return methodArgs(firstLineOffset, args);
    }

    private String resultSetArg() {
        return RESULT_SET + " " + RESULT_SET_ARG_NAME;
    }

    private List<String> aliasedArgsNames(Map<String, String> fieldsTypes) {
        return fieldsTypes.keySet().stream().map(this::aliased).collect(Collectors.toList());
    }

    private String factoryPrefix(boolean list, String className) {
        StringBuilder builder = new StringBuilder()
            .append(Strings.TAB).append(FACTORIES_MODIFIER).append(" ");
        if (list) {
            builder.append(list(className)).append(" ").append(LIST_FACTORY_NAME);
        } else {
            builder.append(className).append(" ").append(Strings.FACTORY_NAME);
        }
        return builder.append(Strings.START_BRACKET)
            .toString();
    }

    private String list(String className) {
        return String.format("List<%s>", className);
    }

    private String singleFactory(MetaData data) {
        return new StringBuilder()
            .append(factoryPrefix(false, data.className))
            .append(RESULT_SET).append(" ").append(RESULT_SET_ARG_NAME)
            .append(Strings.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(Strings.START_CURLY_BRACKET).append(Strings.NEW_LINE)
            .append(Strings.DOUBLE_TAB).append("return ")
            .append(constantsFactoryInvocation(data.columnsLabels))
            .append(Strings.SEMICOLON).append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String constantsFactoryInvocation(List<String> columnsLabels) {
        return factoryInvocation(false, RESULT_SET_ARG_NAME, columnsLabels.stream().map(String::toUpperCase)
            .collect(Collectors.toList()));
    }

    private String factoryInvocation(boolean list, String arg, List<String> args) {
        StringBuilder builder = new StringBuilder()
            .append(list ? LIST_FACTORY_NAME : Strings.FACTORY_NAME).append(Strings.START_BRACKET)
            .append(arg);
        for (String a : args) {
            builder.append(Strings.COMMA).append(" ").append(a);
        }
        return builder.append(Strings.END_BRACKET).toString();
    }

    private String aliased(String name) {
        return name + FACTORY_ARG_SUFFIX;
    }

    private String aliasedListFactory(MetaData data) {
        StringBuilder builder = new StringBuilder()
            .append(factoryPrefix(true, data.className));
        return builder.append(aliasedFactoryArgs(builder.length(), data.fieldsTypes))
            .append(Strings.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(Strings.START_CURLY_BRACKET).append(Strings.NEW_LINE)
            .append(listMapping(data))
            .append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String listFactory(MetaData data) {
        return new StringBuilder()
            .append(factoryPrefix(true, data.className))
            .append(RESULT_SET).append(" ").append(RESULT_SET_ARG_NAME)
            .append(Strings.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(Strings.START_CURLY_BRACKET).append(Strings.NEW_LINE)
            .append(Strings.DOUBLE_TAB).append("return ")
            .append(factoryInvocation(true, RESULT_SET_ARG_NAME, data.columnsLabels.stream()
                .map(String::toUpperCase).collect(Collectors.toList()))).append(";")
            .append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String listMapping(MetaData meta) {
        return new StringBuilder()
            .append(Strings.DOUBLE_TAB)
            .append(list(meta.className)).append(" ").append(LIST_NAME).append(SPACED_EQUAL).append(INITIALIZED_LIST)
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB)
            .append("do ").append(Strings.START_CURLY_BRACKET).append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB)
            .append(Strings.TAB)
            .append(LIST_NAME).append(".add(").append(factoryInvocation(false, RESULT_SET_ARG_NAME,
                aliasedArgsNames(meta.fieldsTypes))).append(");")
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB).append(Strings.END_CURLY_BRACKET)
            .append(" while (").append(RESULT_SET_ARG_NAME).append(Strings.DOT).append("next()").append(");")
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB).append("return ").append(LIST_NAME).append(Strings.SEMICOLON)
            .toString();
    }

    private String resultSetInvocation(String type, String key) {
        return new StringBuilder()
            .append(RESULT_SET_ARG_NAME).append(Strings.DOT).append("get")
            .append(capitalized(type))
            .append(Strings.START_BRACKET)
            .append(key)
            .append(Strings.END_BRACKET)
            .toString();
    }

    private String capitalized(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    private String fieldInitialization(String field, String value) {
        return new StringBuilder().append(Strings.DOUBLE_TAB)
            .append(field).append(SPACED_EQUAL).append(value).append(Strings.SEMICOLON)
            .toString();
    }
}
