package com.iprogrammerr.smart.query.meta.table;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
		return listFromResult(result, ID, AUTHOR_ID, TITLE, YEAR_OF_PUBLICATION);
	}
}