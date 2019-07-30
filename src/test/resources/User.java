package com.iprogrammerr.smart.query.meta.table;

import com.iprogrammerr.smart.query.mapping.clazz.Mapping;

import java.util.Objects;

public class User {

    public static final String TABLE = "user";
    public static final String ID = "id";
    public static final String FULL_NAME = "full_name";

    public final long id;
    @Mapping(FULL_NAME)
    public final String fullName;

    public User(long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof User) {
            User other = (User) object;
            return id == other.id &&
                Objects.equals(fullName, other.fullName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName);
    }
}