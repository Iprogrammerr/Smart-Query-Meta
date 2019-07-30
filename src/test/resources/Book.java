package com.iprogrammerr.smart.query.meta.table;

import com.iprogrammerr.smart.query.mapping.clazz.Mapping;

import java.util.Objects;

public class Book {

    public static final String TABLE = "book";
    public static final String ID = "id";
    public static final String AUTHOR_ID = "author_id";
    public static final String TITLE = "title";
    public static final String PAGES = "pages";
    public static final String LANGUAGE = "language";
    public static final String YEAR_OF_PUBLICATION = "year_of_publication";

    public final int id;
    @Mapping(AUTHOR_ID)
    public final int authorId;
    public final String title;
    public final Integer pages;
    public final String language;
    @Mapping(YEAR_OF_PUBLICATION)
    public final int yearOfPublication;

    public Book(int id, int authorId, String title, Integer pages, String language, int yearOfPublication) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.pages = pages;
        this.language = language;
        this.yearOfPublication = yearOfPublication;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Book) {
            Book other = (Book) object;
            return id == other.id &&
                authorId == other.authorId &&
                Objects.equals(title, other.title) &&
                Objects.equals(pages, other.pages) &&
                Objects.equals(language, other.language) &&
                yearOfPublication == other.yearOfPublication;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorId, title, pages, language, yearOfPublication);
    }
}