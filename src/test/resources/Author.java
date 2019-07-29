package com.iprogrammerr.smart.query.meta.table;

public class Author {

    public static final String TABLE = "author";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String ALIAS = "alias";
    public static final String ALIVE = "alive";

    public final Integer id;
    public final String name;
    public final String surname;
    public final String alias;
    public final Byte alive;

    public Author(Integer id, String name, String surname, String alias, Byte alive) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.alias = alias;
        this.alive = alive;
    }
}