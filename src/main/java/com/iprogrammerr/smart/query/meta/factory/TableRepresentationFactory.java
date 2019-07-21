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
    private static final String RESULT_SET_ARG = RESULT_SET + " " + RESULT_SET_ARG_NAME;
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
            .append(ClassElements.EMPTY_LINE)
            .append(fields(data))
            .append(ClassElements.EMPTY_LINE)
            .append(constructor(data.className, data.fieldsTypes))
            .append(ClassElements.EMPTY_LINE)
            .append(factories(data))
            .append(ClassElements.NEW_LINE).append(ClassElements.END_CURLY_BRACKET)
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
            .append(ClassElements.classProlog(packageName, imports))
            .append(ClassElements.EMPTY_LINE)
            .append(CLASS_PREFIX).append(" ")
            .append(className).append(" ").append(ClassElements.START_CURLY_BRACKET)
            .toString();
    }

    private String fields(MetaData data) {
        StringBuilder builder = new StringBuilder();
        builder.append(constant(ClassElements.TABLE, data.tableName));
        for (String l : data.columnsLabels) {
            builder.append(ClassElements.NEW_LINE).append(constant(l.toUpperCase(), l.toLowerCase()));
        }
        builder.append(ClassElements.NEW_LINE);
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            builder.append(ClassElements.NEW_LINE).append(ClassElements.TAB)
                .append(field(e.getValue(), e.getKey()));
        }
        return builder.toString();
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

    private String factories(MetaData data) {
        return new StringBuilder()
            .append(singleAliasedFactory(data))
            .append(ClassElements.EMPTY_LINE)
            .append(singleFactory(data))
            .append(ClassElements.EMPTY_LINE)
            .append(aliasedListFactory(data))
            .append(ClassElements.EMPTY_LINE)
            .append(listFactory(data))
            .toString();
    }

    private String singleAliasedFactory(MetaData data) {
        StringBuilder builder = new StringBuilder();
        builder.append(factoryPrefix(false, data.className))
            .append(aliasedFactoryArgs(builder.length(), data.fieldsTypes))
            .append(ClassElements.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(ClassElements.START_CURLY_BRACKET)
            .append(ClassElements.NEW_LINE)
            .append(aliasedFactoryBody(data));
        int offset = builder.length();
        List<String> fields = new ArrayList<>(data.fieldsTypes.keySet());
        return builder.append(ClassElements.DOUBLE_TAB).append("return new ").append(data.className)
            .append(ClassElements.START_BRACKET)
            .append(ClassElements.argsInLines(builder.length() - offset, fields))
            .append(ClassElements.END_BRACKET).append(ClassElements.SEMICOLON)
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String aliasedFactoryBody(MetaData data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            String field = e.getKey();
            String type = e.getValue();
            builder.append(fieldInitialization(type + " " + field,
                resultSetInvocation(e.getValue(), aliased(field))))
                .append(ClassElements.NEW_LINE);
            if (WAS_NULL_TYPES.contains(type) && data.nullableFields.contains(field)) {
                builder.append(wasNull(field))
                    .append(ClassElements.NEW_LINE);
            }
        }
        return builder.toString();
    }

    private String aliasedFactoryArgs(int firstLineOffset, Map<String, String> fieldsTypes) {
        List<String> args = new ArrayList<>();
        args.add(RESULT_SET_ARG);
        List<String> aliasedArgs = fieldsTypes.keySet().stream().map(f -> STRING + " " + aliased(f))
            .collect(Collectors.toList());
        args.addAll(aliasedArgs);
        return ClassElements.argsInLines(firstLineOffset, args);
    }

    private String wasNull(String field) {
        return new StringBuilder()
            .append(ClassElements.DOUBLE_TAB).append("if(").append(RESULT_SET_ARG_NAME).append(".wasNull()) ")
            .append(ClassElements.START_CURLY_BRACKET).append(ClassElements.NEW_LINE)
            .append(ClassElements.DOUBLE_TAB).append(ClassElements.TAB)
            .append(field).append(SPACED_EQUAL).append("null;")
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB)
            .append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private List<String> aliasedArgsNames(Map<String, String> fieldsTypes) {
        return fieldsTypes.keySet().stream().map(this::aliased).collect(Collectors.toList());
    }

    private String factoryPrefix(boolean list, String className) {
        StringBuilder builder = new StringBuilder().append(ClassElements.TAB)
            .append(FACTORIES_MODIFIER).append(" ");
        if (list) {
            builder.append(list(className)).append(" ").append(LIST_FACTORY_NAME);
        } else {
            builder.append(className).append(" ").append(ClassElements.FACTORY_NAME);
        }
        return builder.append(ClassElements.START_BRACKET).toString();
    }

    private String list(String className) {
        return String.format("List<%s>", className);
    }

    private String singleFactory(MetaData data) {
        return new StringBuilder()
            .append(factoryPrefix(false, data.className))
            .append(RESULT_SET_ARG).append(ClassElements.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(ClassElements.START_CURLY_BRACKET).append(ClassElements.NEW_LINE)
            .append(ClassElements.DOUBLE_TAB).append("return ")
            .append(constantsFactoryInvocation(data.columnsLabels))
            .append(ClassElements.SEMICOLON).append(ClassElements.NEW_LINE).append(ClassElements.TAB)
            .append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String constantsFactoryInvocation(List<String> columnsLabels) {
        return factoryInvocation(false, RESULT_SET_ARG_NAME, ClassElements.constants(columnsLabels));
    }


    private String factoryInvocation(boolean list, String arg, List<String> args) {
        StringBuilder builder = new StringBuilder()
            .append(list ? LIST_FACTORY_NAME : ClassElements.FACTORY_NAME).append(ClassElements.START_BRACKET)
            .append(arg);
        for (String a : args) {
            builder.append(ClassElements.COMMA).append(" ").append(a);
        }
        return builder.append(ClassElements.END_BRACKET).toString();
    }

    private String aliased(String name) {
        return name + FACTORY_ARG_SUFFIX;
    }

    private String aliasedListFactory(MetaData data) {
        StringBuilder builder = new StringBuilder()
            .append(factoryPrefix(true, data.className));
        return builder.append(aliasedFactoryArgs(builder.length(), data.fieldsTypes))
            .append(ClassElements.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(ClassElements.START_CURLY_BRACKET).append(ClassElements.NEW_LINE)
            .append(listMapping(data))
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String listFactory(MetaData data) {
        return new StringBuilder()
            .append(factoryPrefix(true, data.className))
            .append(RESULT_SET).append(" ").append(RESULT_SET_ARG_NAME)
            .append(ClassElements.END_BRACKET).append(" ").append(THROWS_EXCEPTION)
            .append(" ").append(ClassElements.START_CURLY_BRACKET).append(ClassElements.NEW_LINE)
            .append(ClassElements.DOUBLE_TAB).append("return ")
            .append(factoryInvocation(true, RESULT_SET_ARG_NAME, ClassElements.constants(data.columnsLabels)))
            .append(ClassElements.SEMICOLON)
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String listMapping(MetaData meta) {
        return new StringBuilder()
            .append(ClassElements.DOUBLE_TAB)
            .append(list(meta.className)).append(" ").append(LIST_NAME).append(SPACED_EQUAL).append(INITIALIZED_LIST)
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB)
            .append("do ").append(ClassElements.START_CURLY_BRACKET).append(ClassElements.NEW_LINE)
            .append(ClassElements.DOUBLE_TAB).append(ClassElements.TAB)
            .append(LIST_NAME).append(".add(")
            .append(factoryInvocation(false, RESULT_SET_ARG_NAME, aliasedArgsNames(meta.fieldsTypes)))
            .append(");")
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB).append(ClassElements.END_CURLY_BRACKET)
            .append(" while (").append(RESULT_SET_ARG_NAME).append(ClassElements.DOT).append("next()").append(");")
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB).append("return ").append(LIST_NAME)
            .append(ClassElements.SEMICOLON)
            .toString();
    }

    private String resultSetInvocation(String type, String key) {
        return new StringBuilder()
            .append(RESULT_SET_ARG_NAME).append(ClassElements.DOT).append("get")
            .append(ClassElements.capitalized(TYPE_RESULT_SET_TYPE.getOrDefault(type, type)))
            .append(ClassElements.START_BRACKET)
            .append(key)
            .append(ClassElements.END_BRACKET)
            .toString();
    }

    private String fieldInitialization(String field, String value) {
        return new StringBuilder().append(ClassElements.DOUBLE_TAB)
            .append(field).append(SPACED_EQUAL).append(value).append(ClassElements.SEMICOLON)
            .toString();
    }
}
