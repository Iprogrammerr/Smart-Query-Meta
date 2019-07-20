package com.iprogrammerr.smart.query.meta;

public class IdInfo {

    public final String name;
    public final boolean autoIncrement;

    public IdInfo(String name, boolean autoIncrement) {
        this.name = name;
        this.autoIncrement = autoIncrement;
    }
}
