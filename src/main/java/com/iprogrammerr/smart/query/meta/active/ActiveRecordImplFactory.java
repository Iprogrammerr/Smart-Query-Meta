package com.iprogrammerr.smart.query.meta.active;

import com.iprogrammerr.smart.query.meta.meta.MetaData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveRecordImplFactory {

    private static final int MAX_LINE_LENGTH = 80;
    private static final String SEMICOLON = ";";
    private static final List<String> IMPORTS = Arrays.asList("import com.iprogrammerr.smart.query.QueryFactory;",
        "import com.iprogrammerr.smart.query.meta.active.ActiveRecord;",
        "import com.iprogrammerr.smart.query.meta.active.UpdateableColumn;");
    private static final String START_CURLY_BRACKET = "{";
    private static final String END_CURLY_BRACKET = "}";
    private static final String TAB = "\t";
    private static final String DOUBLE_TAB = TAB + TAB;
    private static final String NEW_LINE = "\n";
    private static final String EMPTY_LINE = NEW_LINE + NEW_LINE;
    private static final String START_BRACKET = "(";
    private static final String END_BRACKET = ")";
    private static final String QUERY_FACTORY = "QueryFactory";
    private static final String QUERY_FACTORY_ARG = "factory";
    private static final String PUBLIC = "public";
    private static final String ID = "id";
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
            .append(NEW_LINE)
            .append(header(meta))
            .append(EMPTY_LINE)
            .append(constructors(meta, idName))
            .append(NEW_LINE)
            .append(END_CURLY_BRACKET)
            .toString();
    }

    private String prolog() {
        StringBuilder builder = new StringBuilder(packageName).append(SEMICOLON)
            .append(EMPTY_LINE);
        for (String i : IMPORTS) {
            builder.append(i).append(NEW_LINE);
        }
        return builder.toString();
    }

    private String header(MetaData meta) {
        return new StringBuilder()
            .append("public class ").append(implName(meta.className)).append(" extends ActiveRecord<")
            .append(meta.className).append("> ").append(START_CURLY_BRACKET)
            .toString();
    }

    private String implName(String className) {
        return className + "Record";
    }

    private String constant(MetaData meta, String key) {
        return meta.className + "." + key;
    }

    //TODO unhardcode id type
    private String constructors(MetaData meta, String idName) {
        Optional<String> idType = meta.fieldsTypes.entrySet().stream()
            .filter(e -> e.getKey().equalsIgnoreCase(idName)).map(Map.Entry::getValue).findFirst();
        if (!idType.isPresent()) {
            throw new RuntimeException(String.format("Can't find id %s in %s", idName, meta.fieldsTypes));
        }
        return new StringBuilder()
            .append(constructorPrefix(meta.className)).append(", ").append(ID_TYPE_TRANSLATION.get(idType.get()))
            .append(" ").append(ID).append(END_BRACKET).append(" ").append(START_CURLY_BRACKET)
            .append(NEW_LINE).append(DOUBLE_TAB)
            .append(superCall(meta, idName))
            .append(NEW_LINE).append(TAB).append(END_CURLY_BRACKET)
            .append(EMPTY_LINE)
            .append(constructorPrefix(meta.className)).append(END_BRACKET)
            .append(" ").append(START_CURLY_BRACKET).append(NEW_LINE).append(DOUBLE_TAB)
            .append("this(").append(QUERY_FACTORY_ARG).append(", null").append(END_BRACKET).append(SEMICOLON)
            .append(NEW_LINE).append(TAB).append(END_CURLY_BRACKET)
            .toString();
    }

    private String constructorPrefix(String className) {
        return new StringBuilder()
            .append(TAB).append(PUBLIC).append(" ").append(implName(className)).append(START_BRACKET)
            .append(QUERY_FACTORY).append(" ").append(QUERY_FACTORY_ARG)
            .toString();
    }

    private String superCall(MetaData meta, String idName) {
        StringBuilder builder = new StringBuilder()
            .append("super").append(START_BRACKET).append(QUERY_FACTORY_ARG)
            .append(", ").append(newUpdateableColumn(constant(meta, idName) + ", " + ID));
        int previousLength = 0;
        for (String c : meta.columnsLabels) {
            if (c.equals(idName)) {
                continue;
            }
            builder.append(", ");
            if (builder.length() - previousLength > MAX_LINE_LENGTH) {
                previousLength = builder.length();
                builder.append(NEW_LINE).append(TAB).append(DOUBLE_TAB);
            }
            builder.append(newUpdateableColumn(constant(meta, c)));
        }
        return builder.append(END_BRACKET).append(SEMICOLON).toString();
    }

    private String newUpdateableColumn(String inner) {
        return new StringBuilder("new UpdateableColumn<>(").append(inner).append(")").toString();
    }
}
