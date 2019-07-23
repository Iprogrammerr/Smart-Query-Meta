[![Build Status](https://travis-ci.com/Iprogrammerr/Smart-Query-Meta.svg?branch=master)](https://travis-ci.com/Iprogrammerr/Smart-Query-Meta)
[![Test Coverage](https://img.shields.io/codecov/c/github/iprogrammerr/smart-query-meta/master.svg)](https://codecov.io/gh/Iprogrammerr/Smart-Query-Meta/branch/master)
# Smart Query Meta
SQL tables representation and ActiveRecord extensions generator based on database connection.
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
```
<plugin>
    <groupId>com.iprogrammerr</groupId>
    <artifactId>smart-query-meta</artifactId>
    <version>1.0.0</version>
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
    year_of_publication INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);
```
Tables:
```java
package com.iprogrammerr.smart.query.meta.table;

import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class Author { 
    
    public static final String TABLE = "AUTHOR";
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

    public Author(int id, String name, String surname, String alias, Byte alive) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.alias = alias;
        this.alive = alive;
    }

    public static Author fromResult(ResultSet result, String idLabel, String nameLabel, String surnameLabel, 
        String aliasLabel, String aliveLabel) throws Exception {
        Integer id = result.getInt(idLabel);
        String name = result.getString(nameLabel);
        String surname = result.getString(surnameLabel);
        String alias = result.getString(aliasLabel);
        Byte alive = result.getByte(aliveLabel);
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
        return fromListResult(result, ID, NAME, SURNAME, ALIAS, ALIVE);
    }
}

package com.iprogrammerr.smart.query.meta.table;

import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class Book {

    public static final String TABLE = "BOOK";
    public static final String ID = "id";
    public static final String AUTHOR_ID = "author_id";
    public static final String TITLE = "title";
    public static final String YEAR_OF_PUBLICATION = "year_of_publication";

    public final Integer id;
    public final Integer authorId;
    public final String title;
    public final Integer yearOfPublication;

    public Book(Integer id, Integer authorId, String title, Integer yearOfPublication) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.yearOfPublication = yearOfPublication;     
    }

    public static Book fromResult(ResultSet result, String idLabel, String authorIdLabel, String titleLabel, 
		String yearOfPublicationLabel) throws Exception {
        Integer id = result.getInt(idLabel);
        Integer authorId = result.getInt(authorIdLabel);
        String title = result.getString(titleLabel);
        Integer yearOfPublication = result.getInt(yearOfPublicationLabel);
        return new Book(id, authorId, title, yearOfPublication);
    }

    public static Book fromResult(ResultSet result) throws Exception {
        return fromResult(result, ID, AUTHOR_ID, TITLE, YEAR_OF_PUBLICATION);
    }

    public static List<Book> listFromResult(ResultSet result, String idLabel, String authorIdLabel, String titleLabel, 
	   	String yearOfPublicationLabel) throws Exception {
        List<Book> list = new ArrayList<>();
        do {
            list.add(fromResult(result, idLabel, authorIdLabel, titleLabel, yearOfPublicationLabel));
        } while (result.next());
        return list;
    }

    public static List<Book> listFromResult(ResultSet result) throws Exception {
        return fromListResult(result, ID, AUTHOR_ID, TITLE, YEAR_OF_PUBLICATION);
    }
}
```
ActiveRecords:
```java
package com.iprogrammerr.smart.query.meta.table;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;

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

    public AuthorRecord setAlive(Byte alive) {  
        set(Author.ALIVE, alive);
        return this;    
    }
}

package com.iprogrammerr.smart.query.meta.table;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;

public class BookRecord extends ActiveRecord<Integer, Book> { 

    public BookRecord(QueryFactory factory, Integer id) {
	     super(factory, Book.TABLE, new UpdateableColumn<>(Book.ID, id), Integer.class, true, 
	        new UpdateableColumn<>(Book.AUTHOR_ID), new UpdateableColumn<>(Book.TITLE), 
	        new UpdateableColumn<>(Book.YEAR_OF_PUBLICATION)); 
    }

    public BookRecord(QueryFactory factory) { 
        this(factory, null); 
    }

    @Override
    public Book fetch() { 
        return fetchQuery().fetch(r -> {
            r.next();
            return Book.fromResult(r); 
        });     
    }
    
    public BookRecord setAuthorId(Integer authorId) { 
        set(Book.AUTHOR_ID, authorId);
        return this; 
    }

    public BookRecord setTitle(String title) { 
        set(Book.TITLE, title);
        return this; 
    }

    public BookRecord setYearOfPublication(Integer yearOfPublication) {     
        set(Book.YEAR_OF_PUBLICATION, yearOfPublication);
        return this; 
    }
}
```
Using [Smart Query:](https://github.com/Iprogrammerr/Smart-Query)
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
    .fetch(r -> {
        r.next();
        return Author.fromResult(r);
    });
        
List<Book> books = queryFactory.newQuery().dsl()
    .selectAll().from(Book.TABLE).where(Book.AUTHOR_ID).equal().value(id)
    .query()
    .fetch(r -> {
        r.next();
        return Book.listFromResult(r);
    });

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