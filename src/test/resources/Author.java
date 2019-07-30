package com.iprogrammerr.smart.query.meta.table;

import java.util.Objects;

public class Author {

    public static final String TABLE = "author";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String ALIAS = "alias";
    public static final String ALIVE = "alive";

    public final int id;
    public final String name;
    public final String surname;
    public final String alias;
    public final byte alive;

    public Author(int id, String name, String surname, String alias, byte alive) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.alias = alias;
        this.alive = alive;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Author) {
            Author other = (Author) object;
            return id == other.id &&
                Objects.equals(name, other.name) &&
                Objects.equals(surname, other.surname) &&
                Objects.equals(alias, other.alias) &&
                alive == other.alive;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, alias, alive);
    }
}