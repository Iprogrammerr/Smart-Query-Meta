package com.iprogrammerr.smart.query.meta.factory;

import com.iprogrammerr.smart.query.meta.data.MetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TableRepresentationFactory {

    private static final int MAX_ARG_LINE_SIZE = 100;
    private static final List<String> IMPORTS = Arrays.asList("import java.sql.ResultSet;",
        "import java.util.ArrayList;", "import java.util.List;");
    private static final String BLOB_IMPORT = "import java.sql.Blob;";
    private static final String BLOB = "Blob";
    private static final String CONSTANTS_MODIFIED = "public static final";
    private static final String CLASS_PREFIX = "public class";
    private static final String FIELD_MODIFIER = "public final";
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
    private static final Map<String, String> TYPE_RESULT_SET_TYPE = new HashMap<>();
    private static final Set<String> WAS_NULL_TYPES = new HashSet<>();

    static {
        TYPE_RESULT_SET_TYPE.put("Integer", "int");
        WAS_NULL_TYPES.add("Double");
        WAS_NULL_TYPES.add("Float");
        WAS_NULL_TYPES.add("Long");
        WAS_NULL_TYPES.add("Integer");
        WAS_NULL_TYPES.add("Short");
        WAS_NULL_TYPES.add("Character");
        WAS_NULL_TYPES.add("Byte");
        WAS_NULL_TYPES.add("Boolean");
    }


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
            .append(TextElements.NEW_LINE).append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String header(String className, boolean hasBlob) {
        List<String> imports;
        if (hasBlob) {
            imports = new ArrayList<>(IMPORTS);
            imports.add(BLOB_IMPORT);
        } else {
            imports = IMPORTS;
        }
        return new StringBuilder()
            .append(TextElements.classProlog(packageName, imports))
            .append(TextElements.NEW_LINE).append(CLASS_PREFIX).append(" ")
            .append(className).append(" ").append(TextElements.START_CURLY_BRACKET)
            .append(TextElements.EMPTY_LINE)
            .toString();
    }

    private String fields(MetaData data) {
        StringBuilder builder = new StringBuilder();
        builder.append(constant(TextElements.TABLE, data.tableName));
        for (String l : data.columnsLabels) {
            builder.append(constant(l.toUpperCase(), l.toLowerCase()));
        }
        builder.append(TextElements.NEW_LINE);
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            builder.append(TextElements.TAB).append(FIELD_MODIFIER).append(" ").append(e.getValue())
                .append(" ").append(e.getKey()).append(TextElements.SEMICOLON).append(TextElements.NEW_LINE);
        }
        builder.append(TextElements.NEW_LINE);
        return builder.toString();
    }

    private String constant(String name, String value) {
        return new StringBuilder().append(TextElements.TAB).append(CONSTANTS_MODIFIED)
            .append(" ").append(STRING).append(" ").append(name)
            .append(SPACED_EQUAL).append("\"").append(value).append("\"")
            .append(TextElements.SEMICOLON).append(TextElements.NEW_LINE)
            .toString();
    }

    private String constructor(String className, Map<String, String> fieldsTypes) {
        StringBuilder builder = new StringBuilder();
        builder.append(TextElements.TAB).append(TextElements.PUBLIC_MODIFIER).append(" ").append(className)
            .append(TextElements.START_BRACKET);
        List<String> args = fieldsTypes.entrySet().stream().map(e -> e.getValue() + " " + e.getKey())
            .collect(Collectors.toList());
        builder.append(methodArgs(builder.length(), args)).append(TextElements.END_BRACKET).append(" ")
            .append(TextElements.START_CURLY_BRACKET);
        for (String f : fieldsTypes.keySet()) {
            builder.append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB)
                .append(TextElements.THIS).append(TextElements.DOT).append(f).append(SPACED_EQUAL).append(f)
                .append(TextElements.SEMICOLON);
        }
        return builder.append(TextElements.NEW_LINE).append(TextElements.TAB).append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String methodArgs(int firstLineOffset, List<String> args) {
        StringBuilder builder = new StringBuilder();
        builder.append(args.get(0));
        int previousLineLength = 0;
        for (int i = 1; i < args.size(); i++) {
            builder.append(TextElements.COMMA).append(" ");
            if (((firstLineOffset + builder.length()) - previousLineLength) > MAX_ARG_LINE_SIZE) {
                builder.append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB);
                previousLineLength = builder.length();
                firstLineOffset = 0;
            }
            builder.append(args.get(i));

        }
        return builder.toString();
    }

    private String factories(MetaData data) {
        return new StringBuilder()
            .append(TextElements.EMPTY_LINE)
            .append(singleAliasedFactory(data))
            .append(TextElements.EMPTY_LINE)
            .append(singleFactory(data))
            .append(TextElements.EMPTY_LINE)
            .append(aliasedListFactory(data))
            .append(TextElements.EMPTY_LINE)
            .append(listFactory(data))
            .toString();
    }

    private String singleAliasedFactory(MetaData data) {
        StringBuilder builder = new StringBuilder();
        builder.append(factoryPrefix(false, data.className))
            .append(aliasedFactoryArgs(builder.length(), data.fieldsTypes))
            .append(TextElements.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(TextElements.START_CURLY_BRACKET).append(TextElements.NEW_LINE);

        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            String field = e.getKey();
            String type = e.getValue();
            builder.append(fieldInitialization(type + " " + field,
                resultSetInvocation(e.getValue(), aliased(field))))
                .append(TextElements.NEW_LINE);
            if (WAS_NULL_TYPES.contains(type) && data.nullableFields.contains(field)) {
                builder.append(wasNull(field))
                    .append(TextElements.NEW_LINE);
            }

        }

        int offset = builder.length();
        return builder.append(TextElements.DOUBLE_TAB).append("return new ").append(data.className)
            .append(TextElements.START_BRACKET)
            .append(methodArgs(builder.length() - offset, new ArrayList<>(data.fieldsTypes.keySet())))
            .append(TextElements.END_BRACKET).append(TextElements.SEMICOLON)
            .append(TextElements.NEW_LINE).append(TextElements.TAB).append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String aliasedFactoryArgs(int firstLineOffset, Map<String, String> fieldsTypes) {
        List<String> args = new ArrayList<>();
        args.add(resultSetArg());
        args.addAll(fieldsTypes.keySet().stream().map(f -> STRING + " " + aliased(f))
            .collect(Collectors.toList()));
        return methodArgs(firstLineOffset, args);
    }

    private String wasNull(String field) {
        return new StringBuilder()
            .append(TextElements.DOUBLE_TAB).append("if(").append(RESULT_SET_ARG_NAME).append(".wasNull()) ")
            .append(TextElements.START_CURLY_BRACKET).append(TextElements.NEW_LINE)
            .append(TextElements.DOUBLE_TAB).append(TextElements.TAB)
            .append(field).append(SPACED_EQUAL).append("null;").append(TextElements.NEW_LINE)
            .append(TextElements.DOUBLE_TAB).append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String resultSetArg() {
        return RESULT_SET + " " + RESULT_SET_ARG_NAME;
    }

    private List<String> aliasedArgsNames(Map<String, String> fieldsTypes) {
        return fieldsTypes.keySet().stream().map(this::aliased).collect(Collectors.toList());
    }

    private String factoryPrefix(boolean list, String className) {
        StringBuilder builder = new StringBuilder()
            .append(TextElements.TAB).append(FACTORIES_MODIFIER).append(" ");
        if (list) {
            builder.append(list(className)).append(" ").append(LIST_FACTORY_NAME);
        } else {
            builder.append(className).append(" ").append(TextElements.FACTORY_NAME);
        }
        return builder.append(TextElements.START_BRACKET)
            .toString();
    }

    private String list(String className) {
        return String.format("List<%s>", className);
    }

    private String singleFactory(MetaData data) {
        return new StringBuilder()
            .append(factoryPrefix(false, data.className))
            .append(RESULT_SET).append(" ").append(RESULT_SET_ARG_NAME)
            .append(TextElements.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(TextElements.START_CURLY_BRACKET).append(TextElements.NEW_LINE)
            .append(TextElements.DOUBLE_TAB).append("return ")
            .append(constantsFactoryInvocation(data.columnsLabels))
            .append(TextElements.SEMICOLON).append(TextElements.NEW_LINE).append(TextElements.TAB)
            .append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String constantsFactoryInvocation(List<String> columnsLabels) {
        return factoryInvocation(false, RESULT_SET_ARG_NAME, columnsLabels.stream().map(String::toUpperCase)
            .collect(Collectors.toList()));
    }

    private String factoryInvocation(boolean list, String arg, List<String> args) {
        StringBuilder builder = new StringBuilder()
            .append(list ? LIST_FACTORY_NAME : TextElements.FACTORY_NAME).append(TextElements.START_BRACKET)
            .append(arg);
        for (String a : args) {
            builder.append(TextElements.COMMA).append(" ").append(a);
        }
        return builder.append(TextElements.END_BRACKET).toString();
    }

    private String aliased(String name) {
        return name + FACTORY_ARG_SUFFIX;
    }

    private String aliasedListFactory(MetaData data) {
        StringBuilder builder = new StringBuilder()
            .append(factoryPrefix(true, data.className));
        return builder.append(aliasedFactoryArgs(builder.length(), data.fieldsTypes))
            .append(TextElements.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(TextElements.START_CURLY_BRACKET).append(TextElements.NEW_LINE)
            .append(listMapping(data))
            .append(TextElements.NEW_LINE).append(TextElements.TAB).append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String listFactory(MetaData data) {
        return new StringBuilder()
            .append(factoryPrefix(true, data.className))
            .append(RESULT_SET).append(" ").append(RESULT_SET_ARG_NAME)
            .append(TextElements.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(TextElements.START_CURLY_BRACKET).append(TextElements.NEW_LINE)
            .append(TextElements.DOUBLE_TAB).append("return ")
            .append(factoryInvocation(true, RESULT_SET_ARG_NAME, data.columnsLabels.stream()
                .map(String::toUpperCase).collect(Collectors.toList()))).append(";")
            .append(TextElements.NEW_LINE).append(TextElements.TAB).append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String listMapping(MetaData meta) {
        return new StringBuilder()
            .append(TextElements.DOUBLE_TAB)
            .append(list(meta.className)).append(" ").append(LIST_NAME).append(SPACED_EQUAL).append(INITIALIZED_LIST)
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB)
            .append("do ").append(TextElements.START_CURLY_BRACKET).append(TextElements.NEW_LINE)
            .append(TextElements.DOUBLE_TAB)
            .append(TextElements.TAB)
            .append(LIST_NAME).append(".add(").append(factoryInvocation(false, RESULT_SET_ARG_NAME,
                aliasedArgsNames(meta.fieldsTypes))).append(");")
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB).append(TextElements.END_CURLY_BRACKET)
            .append(" while (").append(RESULT_SET_ARG_NAME).append(TextElements.DOT).append("next()").append(");")
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB).append("return ").append(LIST_NAME)
            .append(TextElements.SEMICOLON)
            .toString();
    }

    private String resultSetInvocation(String type, String key) {
        return new StringBuilder()
            .append(RESULT_SET_ARG_NAME).append(TextElements.DOT).append("get")
            .append(TextElements.capitalized(TYPE_RESULT_SET_TYPE.getOrDefault(type, type)))
            .append(TextElements.START_BRACKET)
            .append(key)
            .append(TextElements.END_BRACKET)
            .toString();
    }

    private String fieldInitialization(String field, String value) {
        return new StringBuilder().append(TextElements.DOUBLE_TAB)
            .append(field).append(SPACED_EQUAL).append(value).append(TextElements.SEMICOLON)
            .toString();
    }
}
