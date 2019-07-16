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

	public static Author fromResult(ResultSet result, String idLabel, String nameLabel, String surnameLabel, String aliasLabel, String aliveLabel) throws Exception {
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

	public static List<Author> fromListResult(ResultSet result, String idLabel, String nameLabel, String surnameLabel, String aliasLabel, String aliveLabel) throws Exception {
		List<Author> list = new ArrayList<>();
		while (result.next()) {
			list.add(fromResult(result, idLabel, nameLabel, surnameLabel, aliasLabel, aliveLabel));
		}
		return list;
	}
}