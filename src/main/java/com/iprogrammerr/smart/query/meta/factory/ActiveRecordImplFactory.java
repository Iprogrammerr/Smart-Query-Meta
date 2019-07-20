package com.iprogrammerr.smart.query.meta.factory;

import com.iprogrammerr.smart.query.meta.meta.MetaData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveRecordImplFactory {

    private static final int MAX_LINE_LENGTH = 80;
    private static final List<String> IMPORTS = Arrays.asList("import com.iprogrammerr.smart.query.QueryFactory;",
        "import com.iprogrammerr.smart.query.meta.active.ActiveRecord;",
        "import com.iprogrammerr.smart.query.meta.active.UpdateableColumn;");
    private static final String OVERRIDE = "@Override";
    private static final String QUERY_FACTORY = "QueryFactory";
    private static final String QUERY_FACTORY_ARG = "factory";
    private static final String PUBLIC = "public";
    private static final String ID = "id";
    private static final String PARENT_FETCH = "fetchQuery()";
    private static final Map<String, String> ID_TYPE_TRANSLATION = new HashMap<>();

    static {
        ID_TYPE_TRANSLATION.put("byte", "Integer");
        ID_TYPE_TRANSLATION.put("short", "Integer");
        ID_TYPE_TRANSLATION.put("int", "Integer");
        ID_TYPE_TRANSLATION.put("long", "Long");
    }

    private final String packageName;

    public ActiveRecordImplFactory(String packageName) {
        this.packageName = packageName;
    }

    public String newImplementation(MetaData meta, String idName) {
        return new StringBuilder()
            .append(prolog())
            .append(Strings.NEW_LINE)
            .append(header(meta))
            .append(Strings.EMPTY_LINE)
            .append(constructors(meta, idName))
            .append(Strings.EMPTY_LINE).append(Strings.TAB)
            .append(OVERRIDE)
            .append(Strings.NEW_LINE).append(Strings.TAB)
            .append(fetchImplementation(meta))
            .append(setters(meta))
            .append(Strings.NEW_LINE)
            .append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String prolog() {
        StringBuilder builder = new StringBuilder()
            .append(Strings.PACKAGE_PREFIX).append(" ").append(packageName)
            .append(Strings.SEMICOLON).append(Strings.EMPTY_LINE);
        for (String i : IMPORTS) {
            builder.append(i).append(Strings.NEW_LINE);
        }
        return builder.toString();
    }

    private String header(MetaData meta) {
        return new StringBuilder()
            .append("public class ").append(implName(meta.className)).append(" extends ActiveRecord<")
            .append(meta.className).append("> ").append(Strings.START_CURLY_BRACKET)
            .toString();
    }

    public String implName(String className) {
        return className + "Record";
    }

    private String constant(MetaData meta, String key) {
        return meta.className + "." + key;
    }

    private String constructors(MetaData meta, String idName) {
        Optional<String> idType = meta.fieldsTypes.entrySet().stream()
            .filter(e -> e.getKey().equalsIgnoreCase(idName)).map(Map.Entry::getValue).findFirst();
        if (!idType.isPresent()) {
            throw new RuntimeException(String.format("Can't find id %s in %s", idName, meta.fieldsTypes));
        }
        return new StringBuilder()
            .append(constructorPrefix(meta.className)).append(", ").append(ID_TYPE_TRANSLATION.get(idType.get()))
            .append(" ").append(ID).append(Strings.END_BRACKET).append(" ").append(Strings.START_CURLY_BRACKET)
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB)
            .append(superCall(meta, idName))
            .append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.END_CURLY_BRACKET)
            .append(Strings.EMPTY_LINE)
            .append(constructorPrefix(meta.className)).append(Strings.END_BRACKET)
            .append(" ").append(Strings.START_CURLY_BRACKET).append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB)
            .append("this(").append(QUERY_FACTORY_ARG).append(", null").append(Strings.END_BRACKET)
            .append(Strings.SEMICOLON)
            .append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String constructorPrefix(String className) {
        return new StringBuilder()
            .append(Strings.TAB).append(PUBLIC).append(" ").append(implName(className)).append(Strings.START_BRACKET)
            .append(QUERY_FACTORY).append(" ").append(QUERY_FACTORY_ARG)
            .toString();
    }

    private String superCall(MetaData meta, String idName) {
        StringBuilder builder = new StringBuilder()
            .append("super").append(Strings.START_BRACKET).append(QUERY_FACTORY_ARG).append(", ")
            .append(constant(meta, Strings.TABLE)).append(", ")
            .append(newUpdateableColumn(constant(meta, idName) + ", " + ID));
        int previousLength = 0;
        for (String c : meta.columnsLabels) {
            if (c.equals(idName)) {
                continue;
            }
            builder.append(", ");
            if (builder.length() - previousLength > MAX_LINE_LENGTH) {
                previousLength = builder.length();
                builder.append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.DOUBLE_TAB);
            }
            builder.append(newUpdateableColumn(constant(meta, c)));
        }
        return builder.append(Strings.END_BRACKET).append(Strings.SEMICOLON).toString();
    }

    private String newUpdateableColumn(String inner) {
        return new StringBuilder("new UpdateableColumn<>(").append(inner).append(")").toString();
    }

    private String fetchImplementation(MetaData meta) {
        return new StringBuilder()
            .append(PUBLIC).append(" ").append(meta.className).append(" fetch() ").append(Strings.START_CURLY_BRACKET)
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB).append("return ").append(PARENT_FETCH)
            .append(".fetch(r -> ")
            .append(Strings.START_CURLY_BRACKET).append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB).append(Strings.TAB)
            .append("r.next();")
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB).append(Strings.TAB).append("return ")
            .append(meta.className).append(Strings.DOT).append(Strings.FACTORY_NAME).append("(r);")
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB).append(Strings.END_CURLY_BRACKET)
            .append(Strings.END_BRACKET)
            .append(Strings.SEMICOLON)
            .append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String setters(MetaData data) {
        StringBuilder builder = new StringBuilder();
        String className = implName(data.className);
        int i = 0;
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            builder.append(Strings.EMPTY_LINE).append(Strings.TAB)
                .append(Strings.PUBLIC_MODIFIER).append(" ").append(className)
                .append(" set").append(Strings.capitalized(e.getKey()))
                .append(Strings.START_BRACKET)
                .append(e.getValue()).append(" ").append(e.getKey())
                .append(Strings.END_BRACKET)
                .append(" ").append(Strings.START_CURLY_BRACKET)
                .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB)
                .append("set(").append(constant(data, data.columnsLabels.get(i++)))
                .append(Strings.COMMA).append(" ").append(e.getKey()).append(Strings.END_BRACKET)
                .append(Strings.SEMICOLON)
                .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB).append("return this;")
                .append(Strings.NEW_LINE).append(Strings.TAB)
                .append(Strings.END_CURLY_BRACKET);
        }
        return builder.toString();
    }
}
