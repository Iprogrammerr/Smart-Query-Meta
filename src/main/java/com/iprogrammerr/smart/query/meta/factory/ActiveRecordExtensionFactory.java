package com.iprogrammerr.smart.query.meta.factory;

import com.iprogrammerr.smart.query.meta.data.MetaData;
import com.iprogrammerr.smart.query.meta.data.MetaId;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveRecordExtensionFactory {

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
    private final String packageName;

    public ActiveRecordExtensionFactory(String packageName) {
        this.packageName = packageName;
    }

    public String newExtension(MetaData meta, MetaId metaId) {
        String idType = idType(meta, metaId.name);
        return new StringBuilder()
            .append(TextElements.classProlog(packageName, IMPORTS))
            .append(TextElements.EMPTY_LINE)
            .append(header(meta, idType))
            .append(TextElements.EMPTY_LINE)
            .append(constructors(meta, metaId, idType))
            .append(TextElements.EMPTY_LINE).append(TextElements.TAB)
            .append(OVERRIDE)
            .append(TextElements.NEW_LINE).append(TextElements.TAB)
            .append(fetchImplementation(meta))
            .append(setters(meta, metaId))
            .append(TextElements.NEW_LINE)
            .append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String idType(MetaData meta, String idName) {
        Optional<String> idType = meta.fieldsTypes.entrySet().stream()
            .filter(e -> e.getKey().equalsIgnoreCase(idName)).map(Map.Entry::getValue).findFirst();
        if (!idType.isPresent()) {
            throw new RuntimeException(String.format("Can't find id %s in %s", idName, meta.fieldsTypes));
        }
        return idType.get();
    }

    private String header(MetaData meta, String idType) {
        return new StringBuilder()
            .append("public class ").append(implName(meta.className)).append(" extends ActiveRecord<")
            .append(idType).append(", ").append(meta.className).append("> ").append(TextElements.START_CURLY_BRACKET)
            .toString();
    }

    public String implName(String className) {
        return className + "Record";
    }

    private String constant(String className, String key) {
        return className + "." + key;
    }

    private String constructors(MetaData meta, MetaId metaId, String idType) {
        String idArg = TextElements.toCamelCase(metaId.name);
        return new StringBuilder()
            .append(constructorPrefix(meta.className)).append(", ").append(idType)
            .append(" ").append(idArg).append(TextElements.END_BRACKET).append(" ").append(TextElements.START_CURLY_BRACKET)
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB)
            .append(superCall(meta, metaId, idType, idArg))
            .append(TextElements.NEW_LINE).append(TextElements.TAB).append(TextElements.END_CURLY_BRACKET)
            .append(TextElements.EMPTY_LINE)
            .append(constructorPrefix(meta.className)).append(TextElements.END_BRACKET)
            .append(" ").append(TextElements.START_CURLY_BRACKET).append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB)
            .append("this(").append(QUERY_FACTORY_ARG).append(", null").append(TextElements.END_BRACKET)
            .append(TextElements.SEMICOLON)
            .append(TextElements.NEW_LINE).append(TextElements.TAB).append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String constructorPrefix(String className) {
        return new StringBuilder()
            .append(TextElements.TAB).append(PUBLIC).append(" ").append(implName(className)).append(TextElements.START_BRACKET)
            .append(QUERY_FACTORY).append(" ").append(QUERY_FACTORY_ARG)
            .toString();
    }

    private String superCall(MetaData meta, MetaId metaId, String idType, String idArg) {
        StringBuilder builder = new StringBuilder()
            .append("super").append(TextElements.START_BRACKET).append(QUERY_FACTORY_ARG).append(", ")
            .append(constant(meta.className, TextElements.TABLE)).append(", ")
            .append(newUpdateableColumn(constant(meta.className, metaId.name) + ", " + idArg))
            .append(", ").append(idType).append(".class").append(", ").append(metaId.autoIncrement);
        int previousLength = 0;
        for (String c : meta.columnsLabels) {
            if (c.equals(metaId.name)) {
                continue;
            }
            builder.append(", ");
            String arg = newUpdateableColumn(constant(meta.className, c));
            boolean newLine = (builder.length() - previousLength + arg.length()) > MAX_LINE_LENGTH;
            if (newLine) {
                previousLength = builder.length();
                builder.append(TextElements.NEW_LINE).append(TextElements.TAB).append(TextElements.DOUBLE_TAB);
            }
            builder.append(arg);
        }
        return builder.append(TextElements.END_BRACKET).append(TextElements.SEMICOLON).toString();
    }

    private String newUpdateableColumn(String inner) {
        return new StringBuilder("new UpdateableColumn<>(").append(inner).append(")").toString();
    }

    private String fetchImplementation(MetaData meta) {
        return new StringBuilder()
            .append(PUBLIC).append(" ").append(meta.className).append(" fetch() ").append(TextElements.START_CURLY_BRACKET)
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB).append("return ").append(PARENT_FETCH)
            .append(".fetch(r -> ")
            .append(TextElements.START_CURLY_BRACKET).append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB).append(
                TextElements.TAB)
            .append("r.next();")
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB).append(TextElements.TAB).append("return ")
            .append(meta.className).append(TextElements.DOT).append(TextElements.FACTORY_NAME).append("(r);")
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB).append(TextElements.END_CURLY_BRACKET)
            .append(TextElements.END_BRACKET)
            .append(TextElements.SEMICOLON)
            .append(TextElements.NEW_LINE).append(TextElements.TAB).append(TextElements.END_CURLY_BRACKET)
            .toString();
    }

    private String setters(MetaData data, MetaId metaId) {
        StringBuilder builder = new StringBuilder();
        String className = implName(data.className);
        int i = 0;
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            String cl = data.columnsLabels.get(i++);
            if (metaId.autoIncrement && e.getKey().equalsIgnoreCase(metaId.name)) {
                continue;
            }
            builder.append(setter(className, e.getKey(), e.getValue(), constant(data.className, cl)));
        }
        return builder.toString();
    }

    private String setter(String className, String field, String type, String column) {
        return new StringBuilder().append(TextElements.EMPTY_LINE).append(TextElements.TAB)
            .append(TextElements.PUBLIC_MODIFIER).append(" ").append(className)
            .append(" set").append(TextElements.capitalized(field))
            .append(TextElements.START_BRACKET)
            .append(type).append(" ").append(field)
            .append(TextElements.END_BRACKET)
            .append(" ").append(TextElements.START_CURLY_BRACKET)
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB)
            .append("set(").append(column)
            .append(TextElements.COMMA).append(" ").append(field).append(TextElements.END_BRACKET)
            .append(TextElements.SEMICOLON)
            .append(TextElements.NEW_LINE).append(TextElements.DOUBLE_TAB).append("return this;")
            .append(TextElements.NEW_LINE).append(TextElements.TAB)
            .append(TextElements.END_CURLY_BRACKET)
            .toString();
    }
}
