[![Build Status](https://travis-ci.com/Iprogrammerr/Smart-Query-Meta.svg?branch=master)](https://travis-ci.com/Iprogrammerr/Smart-Query-Meta)
[![Test Coverage](https://img.shields.io/codecov/c/github/iprogrammerr/smart-query-meta/master.svg)](https://codecov.io/gh/Iprogrammerr/Smart-Query-Meta/branch/master)
# Smart Query Meta
SQL tables representation and ActiveRecord extensions generator based on database connection. Designed to be used with [Smart Query.](https://github.com/Iprogrammerr/Smart-Query)
## Usage
### Jar
```
mvn clean install
java -jar target/smart-query-meta-jar-with-dependecies.jar <path to application.properties>
```
Application.properties sample:
```
databaseUser=test
databasePassword=test
jdbcUrl=jdbc:h2:mem:test
classesPackage=com.iprogrammerr.smart.query.meta.table
#location of generated classes
classesPath=/home/user/projects/project/target/generated/sources
#whether to generate child classes of com.iprogrammerr.smart.query.active.ActiveRecord
generateActiveRecords=true
```
### Plugin
```xml
<plugin>
    <groupId>com.iprogrammerr</groupId>
    <artifactId>smart-query-meta</artifactId>
    <version>1.1.0</version>
    <configuration>
        <jdbcUrl>jdbc:mysql://localhost:3306/database</jdbcUrl>
        <databaseUser>root</databaseUser>
        <databasePassword>abc</databasePassword>
        <classesPackage>com.iprogrammerr.db</classesPackage>
        <classesPath>/home/user/projects/project/target/generated/sources</classesPath>
        <generateActiveRecords>true</generateActiveRecords>
    </configuration>
    <executions>
        <execution>
            <phase>compile</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
Calling directly:
```
mvn meta:generate
```
## Example
Schema:
```
DROP TABLE IF EXISTS author;
CREATE TABLE author (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    surname VARCHAR(50) NOT NULL UNIQUE,
    alias VARCHAR(50) NOT NULL UNIQUE,
    alive TINYINT(1) NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS book;
CREATE TABLE book (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    author_id INT UNSIGNED NOT NULL,
    title VARCHAR(100) NOT NULL UNIQUE,
    pages INT UNSIGNED,
    language VARCHAR(50) NOT NULL,
    year_of_publication INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);
```
Tables:
```java
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
```
ActiveRecords:
```java
package com.iprogrammerr.smart.query.meta.table;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;
import com.iprogrammerr.smart.query.mapping.Mappings;

public class AuthorRecord extends ActiveRecord<Integer, Author> {

    public AuthorRecord(QueryFactory factory, Integer id) {
        super(factory, Author.TABLE, new UpdateableColumn<>(Author.ID, id), Integer.class, true,
            new UpdateableColumn<>(Author.NAME), new UpdateableColumn<>(Author.SURNAME),
            new UpdateableColumn<>(Author.ALIAS), new UpdateableColumn<>(Author.ALIVE));
    }

    public AuthorRecord(QueryFactory factory) {
        this(factory, null);
    }

    @Override
    public Author fetch() {
        return fetchQuery().fetch(Mappings.ofClass(Author.class));
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

    public AuthorRecord setAlive(byte alive) {
        set(Author.ALIVE, alive);
        return this;
    }
}

package com.iprogrammerr.smart.query.meta.table;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;
import com.iprogrammerr.smart.query.mapping.Mappings;

public class BookRecord extends ActiveRecord<Integer, Book> {

    public BookRecord(QueryFactory factory, Integer id) {
        super(factory, Book.TABLE, new UpdateableColumn<>(Book.ID, id), Integer.class, true,
            new UpdateableColumn<>(Book.AUTHOR_ID), new UpdateableColumn<>(Book.TITLE),
            new UpdateableColumn<>(Book.PAGES), new UpdateableColumn<>(Book.LANGUAGE),
            new UpdateableColumn<>(Book.YEAR_OF_PUBLICATION));
    }

    public BookRecord(QueryFactory factory) {
        this(factory, null);
    }

    @Override
    public Book fetch() {
        return fetchQuery().fetch(Mappings.ofClass(Book.class));
    }

    public BookRecord setAuthorId(int authorId) {
        set(Book.AUTHOR_ID, authorId);
        return this;
    }

    public BookRecord setTitle(String title) {
        set(Book.TITLE, title);
        return this;
    }

    public BookRecord setPages(Integer pages) {
        set(Book.PAGES, pages);
        return this;
    }

    public BookRecord setLanguage(String language) {
        set(Book.LANGUAGE, language);
        return this;
    }

    public BookRecord setYearOfPublication(int yearOfPublication) {
        set(Book.YEAR_OF_PUBLICATION, yearOfPublication);
        return this;
    }
}
```
Note that only nullable fields are generated as non-primitives. ActiveRecord extensions also don't allow to set id of autoincrement type. Using [Smart Query:](https://github.com/Iprogrammerr/Smart-Query)
```java
long id = queryFactory.newQuery().dsl()
    .insertInto(Author.TABLE)
    .columns(Author.NAME, Author.SURNAME, Author.ALIAS, Author.ALIVE)
    .values("Friedrich", "Nietzsche", "Dynamite", 0)
    .query()
    .executeReturningId();
        
Author author = queryFactory.newQuery().dsl()
    .selectAll().from(Author.TABLE).where(Author.ID).equal().value(id)
    .query()
    .fetch(Mappings.ofClass(Author.class));
        
List<Book> books = queryFactory.newQuery().dsl()
    .selectAll().from(Book.TABLE).where(Book.AUTHOR_ID).equal().value(id)
    .query()
    .fetch(Mappings.listOfClass(Book.class));

AuthorRecord record = new AuthorRecord(queryFactory)
    .setName("Lem")
    .setSurname("Stanisław")
    .setAlias("Visionary")
    .setAlive((byte) 0);
record.insert();

author = record.fetch();        

record.setAlias("LS");
record.update();
```
## Supported databases
* MySQL
* MariaDB 
* PostgreSQL
* SQLite
* HSQLDB 
* H2
* Firebird
* Derby