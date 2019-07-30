package com.iprogrammerr.smart.query.meta.factory;

import com.iprogrammerr.smart.query.meta.data.MetaData;
import com.iprogrammerr.smart.query.meta.data.MetaId;
import com.iprogrammerr.smart.query.meta.data.Primitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveRecordsExtensionsFactory {

    private static final int SUPER_CALL_LINE_MAX_LENGTH = 110;
    private static final int NEXT_LINE_SUPER_CALL_TABS = 3;
    private static final List<String> IMPORTS = Arrays.asList("import com.iprogrammerr.smart.query.QueryFactory;",
        "import com.iprogrammerr.smart.query.active.ActiveRecord;",
        "import com.iprogrammerr.smart.query.active.UpdateableColumn;",
        "import com.iprogrammerr.smart.query.mapping.Mappings;");
    private static final String OVERRIDE = "@Override";
    private static final String QUERY_FACTORY = "QueryFactory";
    private static final String QUERY_FACTORY_ARG = "factory";
    private static final String PARENT_FETCH = "fetchQuery()";
    private static final Map<String, String> PRIMITIVES_TRANSLATIONS = new HashMap<>();

    static {
        for (Primitive p : Primitive.values()) {
            String n = p.name().toLowerCase();
            if (p == Primitive.INT) {
                PRIMITIVES_TRANSLATIONS.put(n, Primitive.INTEGER);
            } else {
                PRIMITIVES_TRANSLATIONS.put(n, ClassElements.capitalized(n));
            }
        }
    }

    private final String packageName;

    public ActiveRecordsExtensionsFactory(String packageName) {
        this.packageName = packageName;
    }

    public String newExtension(MetaData meta, MetaId metaId) {
        String idType = idType(meta, metaId.name);
        String idClassType = PRIMITIVES_TRANSLATIONS.getOrDefault(idType, idType);
        return new StringBuilder()
            .append(ClassElements.prolog(packageName, Collections.singletonList(IMPORTS)))
            .append(ClassElements.EMPTY_LINE)
            .append(header(meta, idClassType))
            .append(ClassElements.EMPTY_LINE)
            .append(constructors(meta, metaId, idClassType))
            .append(ClassElements.EMPTY_LINE)
            .append(ClassElements.TAB).append(OVERRIDE)
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB)
            .append(fetchImplementation(meta))
            .append(setters(meta, metaId))
            .append(ClassElements.NEW_LINE)
            .append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String idType(MetaData meta, String idName) {
        String idField = ClassElements.toCamelCase(idName);
        Optional<String> idType = meta.fieldsTypes.entrySet().stream()
            .filter(e -> e.getKey().equalsIgnoreCase(idField)).map(Map.Entry::getValue).findFirst();
        if (!idType.isPresent()) {
            throw new RuntimeException(String.format("Can't find id %s in %s", idField, meta.fieldsTypes));
        }
        return idType.get();
    }

    private String header(MetaData meta, String idType) {
        return new StringBuilder()
            .append("public class ").append(extensionName(meta.className)).append(" extends ActiveRecord<")
            .append(idType).append(", ").append(meta.className).append("> ").append(ClassElements.START_CURLY_BRACKET)
            .toString();
    }

    public String extensionName(String className) {
        return className + "Record";
    }

    private String constant(String className, String key) {
        return className + "." + key.toUpperCase();
    }

    private String constructors(MetaData meta, MetaId metaId, String idType) {
        String idArg = ClassElements.toCamelCase(metaId.name);
        return new StringBuilder()
            .append(constructorPrefix(meta.className)).append(", ").append(idType)
            .append(" ").append(idArg).append(ClassElements.END_BRACKET).append(" ")
            .append(ClassElements.START_CURLY_BRACKET)
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB)
            .append(superCall(meta, metaId, idType, idArg))
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .append(ClassElements.EMPTY_LINE)
            .append(constructorPrefix(meta.className)).append(ClassElements.END_BRACKET)
            .append(" ").append(ClassElements.START_CURLY_BRACKET).append(ClassElements.NEW_LINE)
            .append(ClassElements.DOUBLE_TAB)
            .append("this(").append(QUERY_FACTORY_ARG).append(", null").append(ClassElements.END_BRACKET)
            .append(ClassElements.SEMICOLON)
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String constructorPrefix(String className) {
        return new StringBuilder()
            .append(ClassElements.TAB).append(ClassElements.PUBLIC_MODIFIER).append(" ")
            .append(extensionName(className))
            .append(ClassElements.START_BRACKET)
            .append(QUERY_FACTORY).append(" ").append(QUERY_FACTORY_ARG)
            .toString();
    }

    private String superCall(MetaData meta, MetaId metaId, String idType, String idArg) {
        StringBuilder builder = new StringBuilder()
            .append("super").append(ClassElements.START_BRACKET);

        List<String> args = new ArrayList<>();
        args.add(QUERY_FACTORY_ARG);
        args.add(constant(meta.className, ClassElements.TABLE));
        args.add(newUpdateableColumn(constant(meta.className, metaId.name) + ", " + idArg));
        args.add(idType + ".class");
        args.add(Boolean.toString(metaId.autoIncrement));
        meta.columnsLabels.forEach(c -> {
            if (!c.equalsIgnoreCase(metaId.name)) {
                args.add(newUpdateableColumn(constant(meta.className, c.toUpperCase())));
            }
        });

        return builder.append(ClassElements.argsInLines(builder.length(),
            args, SUPER_CALL_LINE_MAX_LENGTH, NEXT_LINE_SUPER_CALL_TABS))
            .append(ClassElements.END_BRACKET).append(ClassElements.SEMICOLON)
            .toString();
    }

    private String newUpdateableColumn(String inner) {
        return new StringBuilder("new UpdateableColumn<>(").append(inner).append(")").toString();
    }

    private String fetchImplementation(MetaData meta) {
        return new StringBuilder()
            .append(ClassElements.PUBLIC_MODIFIER).append(" ").append(meta.className).append(" fetch() ")
            .append(ClassElements.START_CURLY_BRACKET)
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB).append("return ").append(PARENT_FETCH)
            .append(".fetch(")
            .append(mappingsOf(meta.className))
            .append(ClassElements.END_BRACKET).append(ClassElements.SEMICOLON)
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB).append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }

    private String mappingsOf(String clazz) {
        return String.format("Mappings.ofClass(%s.class)", clazz);
    }

    private String setters(MetaData data, MetaId metaId) {
        StringBuilder builder = new StringBuilder();
        String className = extensionName(data.className);
        int i = 0;
        for (Map.Entry<String, String> e : data.fieldsTypes.entrySet()) {
            String cl = data.columnsLabels.get(i++);
            if (metaId.autoIncrement && e.getKey().equalsIgnoreCase(metaId.name)) {
                continue;
            }
            builder.append(ClassElements.EMPTY_LINE)
                .append(setter(className, e.getKey(), e.getValue(), constant(data.className, cl)));
        }
        return builder.toString();
    }

    private String setter(String className, String field, String type, String column) {
        return new StringBuilder()
            .append(ClassElements.TAB).append(ClassElements.PUBLIC_MODIFIER).append(" ").append(className)
            .append(" set").append(ClassElements.capitalized(field))
            .append(ClassElements.START_BRACKET)
            .append(type).append(" ").append(field)
            .append(ClassElements.END_BRACKET)
            .append(" ").append(ClassElements.START_CURLY_BRACKET)
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB)
            .append("set(").append(column)
            .append(ClassElements.COMMA).append(" ").append(field)
            .append(ClassElements.END_BRACKET).append(ClassElements.SEMICOLON)
            .append(ClassElements.NEW_LINE).append(ClassElements.DOUBLE_TAB).append("return this;")
            .append(ClassElements.NEW_LINE).append(ClassElements.TAB)
            .append(ClassElements.END_CURLY_BRACKET)
            .toString();
    }
}
