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

DROP TABLE IF EXISTS organism;
CREATE TABLE organism (
	dna VARCHAR(255) NOT NULL,
	name VARCHAR(100),
	PRIMARY KEY (dna)
);
