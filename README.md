# Smart Query Meta
SQL tables representation generator based on database connection.
## Usage
```
mvn clean install
java -jar target/smart-query-meta-jar-with-dependecies.jar <path to application.properties>
```
Application.properties sample:
```
database.user=test
database.password=test
jdbc.url=jdbc:h2:mem:test
classes.package=com.iprogrammerr.smart.query.meta.table
#location of generated classes
classes.path=/home/user/projects/project/target/generated/sources
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
Output:
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

    public final int id;
    public final int authorId;
    public final String title;
    public final int yearOfPublication;

    public Book(int id, int authorId, String title, int yearOfPublication) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.yearOfPublication = yearOfPublication;     
    }

    public static Book fromResult(ResultSet result, String idLabel, String authorIdLabel, String titleLabel, 
		String yearOfPublicationLabel) throws Exception {
        int id = result.getInt(idLabel);
        int authorId = result.getInt(authorIdLabel);
        String title = result.getString(titleLabel);
        int yearOfPublication = result.getInt(yearOfPublicationLabel);
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
        return Book.fromListResult(r);
    });
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