package com.iprogrammerr.smart.query.meta.table;

import com.iprogrammerr.smart.query.mapping.clazz.Mapping;

public class Book {

    public static final String TABLE = "book";
    public static final String ID = "id";
    public static final String AUTHOR_ID = "author_id";
    public static final String TITLE = "title";
    public static final String YEAR_OF_PUBLICATION = "year_of_publication";

    public final Integer id;
    @Mapping(AUTHOR_ID)
    public final Integer authorId;
    public final String title;
    @Mapping(YEAR_OF_PUBLICATION)
    public final Integer yearOfPublication;

    public Book(Integer id, Integer authorId, String title, Integer yearOfPublication) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.yearOfPublication = yearOfPublication;
    }
}