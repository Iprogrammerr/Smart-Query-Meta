package com.iprogrammerr.smart.query.meta.data;

public enum Primitive {
    DOUBLE, FLOAT, LONG, INT, SHORT, CHARACTER, BYTE, BOOLEAN, UNKNOWN;

    public static final String INTEGER = "Integer";

    public static Primitive translate(String type) {
        if (type.equals(INTEGER)) {
            return INT;
        }
        for (Primitive p : values()) {
            if (p.name().equals(type.toUpperCase())) {
                return p;
            }
        }
        return UNKNOWN;
    }
}
