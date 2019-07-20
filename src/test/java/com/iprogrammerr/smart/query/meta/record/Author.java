package com.iprogrammerr.smart.query.meta.record;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Author {

    public static final String TABLE = "AUTHOR";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String ALIAS = "alias";
    public static final String ALIVE = "alive";

    public final int id;
    public final String name;
    public final String surname;
    public final String alias;
    public final int alive;

    public Author(int id, String name, String surname, String alias, int alive) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.alias = alias;
        this.alive = alive;
    }

    public static Author fromResult(ResultSet result, String idLabel, String nameLabel, String surnameLabel,
        String aliasLabel, String aliveLabel) throws Exception {
        int id = result.getInt(idLabel);
        String name = result.getString(nameLabel);
        String surname = result.getString(surnameLabel);
        String alias = result.getString(aliasLabel);
        byte alive = result.getByte(aliveLabel);
        return new Author(id, name, surname, alias, alive);
    }

    public static Author fromResult(ResultSet result) throws Exception {
        return fromResult(result, ID, NAME, SURNAME, ALIAS, ALIVE);
    }

    public static List<Author> listFromResult(ResultSet result, String idLabel, String nameLabel, String surnameLabel,
        String aliasLabel, String aliveLabel) throws Exception {
        List<Author> list = new ArrayList<>();
        do {
            list.add(fromResult(result, idLabel, nameLabel, surnameLabel, aliasLabel, aliveLabel));
        } while (result.next());
        return list;
    }

    public static List<Author> listFromResult(ResultSet result) throws Exception {
        return listFromResult(result, ID, NAME, SURNAME, ALIAS, ALIVE);
    }

    @Override
    public String toString() {
        return "Author{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", surname='" + surname + '\'' +
            ", alias='" + alias + '\'' +
            ", alive=" + alive +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id == author.id &&
            alive == author.alive &&
            Objects.equals(name, author.name) &&
            Objects.equals(surname, author.surname) &&
            Objects.equals(alias, author.alias);
    }
}