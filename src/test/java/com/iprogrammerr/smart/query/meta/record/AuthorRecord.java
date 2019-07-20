package com.iprogrammerr.smart.query.meta.record;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.meta.active.ActiveRecord;
import com.iprogrammerr.smart.query.meta.active.UpdateableColumn;

public class AuthorRecord extends ActiveRecord< Author> {

    public AuthorRecord(QueryFactory factory, Integer id) {
        super(factory, Author.TABLE, new UpdateableColumn<>(Author.ID, id), new UpdateableColumn<>(Author.NAME),
            new UpdateableColumn<>(Author.SURNAME), new UpdateableColumn<>(Author.ALIAS),
            new UpdateableColumn<>(Author.ALIVE));
    }

    public AuthorRecord(QueryFactory factory) {
        this(factory, null);
    }

    @Override
    public Author fetch() {
        return fetchQuery().fetch(r -> {
            r.next();
            return Author.fromResult(r);
        });
    }

    public AuthorRecord setName(String name) {
        set(Author.NAME, name);
        return this;
    }

    public AuthorRecord setSurname(String surname) {
        set(Author.SURNAME, surname);
        return this;
    }

    public AuthorRecord setAlias(String alias) {
        set(Author.ALIAS, alias);
        return this;
    }

    public AuthorRecord setAlive(int alive) {
        set(Author.ALIVE, alive);
        return this;
    }
}
