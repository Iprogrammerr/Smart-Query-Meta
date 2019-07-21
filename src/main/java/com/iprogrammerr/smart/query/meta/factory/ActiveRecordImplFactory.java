package com.iprogrammerr.smart.query.meta.factory;

import com.iprogrammerr.smart.query.meta.data.IdInfo;
import com.iprogrammerr.smart.query.meta.data.MetaData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveRecordImplFactory {

    private static final int MAX_LINE_LENGTH = 80;
    private static final List<String> IMPORTS = Arrays.asList("import com.iprogrammerr.smart.query.QueryFactory;",
        "import com.iprogrammerr.smart.query.active.ActiveRecord;",
        "import com.iprogrammerr.smart.query.active.UpdateableColumn;");
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

    public String newImplementation(MetaData meta, IdInfo idInfo) {
        String idType = idType(meta, idInfo.name);
        return new StringBuilder()
            .append(prolog())
            .append(Strings.NEW_LINE)
            .append(header(meta, idType))
            .append(Strings.EMPTY_LINE)
            .append(constructors(meta, idInfo, idType))
            .append(Strings.EMPTY_LINE).append(Strings.TAB)
            .append(OVERRIDE)
            .append(Strings.NEW_LINE).append(Strings.TAB)
            .append(fetchImplementation(meta))
            .append(setters(meta, idInfo))
            .append(Strings.NEW_LINE)
            .append(Strings.END_CURLY_BRACKET)
            .toString();
    }

    private String idType(MetaData meta, String idName) {
        Optional<String> idType = meta.fieldsTypes.entrySet().stream()
            .filter(e -> e.getKey().equalsIgnoreCase(idName)).map(Map.Entry::getValue).findFirst();
        if (!idType.isPresent()) {
            throw new RuntimeException(String.format("Can't find id %s in %s", idName, meta.fieldsTypes));
        }
        return ID_TYPE_TRANSLATION.getOrDefault(idType.get(), idType.get());
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

    private String header(MetaData meta, String idType) {
        return new StringBuilder()
            .append("public class ").append(implName(meta.className)).append(" extends ActiveRecord<")
            .append(idType).append(", ").append(meta.className).append("> ").append(Strings.START_CURLY_BRACKET)
            .toString();
    }

    public String implName(String className) {
        return className + "Record";
    }

    private String constant(String className, String key) {
        return className + "." + key;
    }

    private String constructors(MetaData meta, IdInfo idInfo, String idType) {
        String idArg = Strings.toCamelCase(idInfo.name);
        return new StringBuilder()
            .append(constructorPrefix(meta.className)).append(", ").append(idType)
            .append(" ").append(idArg).append(Strings.END_BRACKET).append(" ").append(Strings.START_CURLY_BRACKET)
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB)
            .append(superCall(meta, idInfo, idType, idArg))
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

    private String superCall(MetaData meta, IdInfo idInfo, String idType, String idArg) {
        StringBuilder builder = new StringBuilder()
            .append("super").append(Strings.START_BRACKET).append(QUERY_FACTORY_ARG).append(", ")
            .append(constant(meta.className, Strings.TABLE)).append(", ")
            .append(newUpdateableColumn(constant(meta.className, idInfo.name) + ", " + idArg))
            .append(", ").append(idType).append(".class").append(", ").append(idInfo.autoIncrement);
        int previousLength = 0;
        for (String c : meta.columnsLabels) {
            if (c.equals(idInfo.name)) {
                continue;
            }
            builder.append(", ");
            String arg = newUpdateableColumn(constant(meta.className, c));
            boolean newLine = (builder.length() - previousLength + arg.length()) > MAX_LINE_LENGTH;
            if (newLine) {
                previousLength = builder.length();
                builder.append(Strings.NEW_LINE).append(Strings.TAB).append(Strings.DOUBLE_TAB);
            }
            builder.append(arg);
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

    private String setters(MetaData data, IdInfo idInfo) {
        StringBuilder builder = new StringBuilder();
        String className = implName(data.className);
        int i = 0;
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            if (idInfo.autoIncrement && e.getKey().equalsIgnoreCase(idInfo.name)) {
                continue;
            }
            builder.append(setter(className, e.getKey(), e.getValue(),
                constant(data.className, data.columnsLabels.get(i++))));
        }
        return builder.toString();
    }

    private String setter(String className, String field, String type, String column) {
        return new StringBuilder().append(Strings.EMPTY_LINE).append(Strings.TAB)
            .append(Strings.PUBLIC_MODIFIER).append(" ").append(className)
            .append(" set").append(Strings.capitalized(field))
            .append(Strings.START_BRACKET)
            .append(type).append(" ").append(field)
            .append(Strings.END_BRACKET)
            .append(" ").append(Strings.START_CURLY_BRACKET)
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB)
            .append("set(").append(column)
            .append(Strings.COMMA).append(" ").append(field).append(Strings.END_BRACKET)
            .append(Strings.SEMICOLON)
            .append(Strings.NEW_LINE).append(Strings.DOUBLE_TAB).append("return this;")
            .append(Strings.NEW_LINE).append(Strings.TAB)
            .append(Strings.END_CURLY_BRACKET)
            .toString();
    }
}
