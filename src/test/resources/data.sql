DROP TABLE if exists USERS, FILMS, FILMS_GENRES, MPA, GENRES, LIKES, FRIENDS;

CREATE TABLE IF NOT EXISTS MPA
(
    MPA_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME   VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME     VARCHAR(30),
    LOGIN    VARCHAR(30) NOT NULL,
    EMAIL    VARCHAR(30) NOT NULL,
    BIRTHDAY DATE        NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME         VARCHAR(100) NOT NULL,
    DESCRIPTION  VARCHAR(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    MPA_ID       INTEGER,
    FOREIGN KEY (MPA_ID) REFERENCES MPA (MPA_ID)
);

CREATE TABLE IF NOT EXISTS GENRES
(
    GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME     VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS_GENRES
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER,
    PRIMARY KEY (FILM_ID, GENRE_ID)
);

CREATE TABLE IF NOT EXISTS LIKES
(
    FILM_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    FOREIGN KEY (USER_ID) REFERENCES users (USER_ID) ON DELETE CASCADE,
    FOREIGN KEY (FILM_ID) REFERENCES films (FILM_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FRIENDS
(
    USER_ID   INTEGER NOT NULL,
    FRIEND_ID INTEGER NOT NULL,
    FOREIGN KEY (USER_ID) REFERENCES users (USER_ID) ON DELETE CASCADE,
    FOREIGN KEY (FRIEND_ID) REFERENCES users (USER_ID) ON DELETE CASCADE
);

MERGE INTO MPA (MPA_ID, NAME)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO GENRES (GENRE_ID, NAME)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES ('film1', 'description1', '1981-01-01', 101, 1),
       ('film2', 'description2', '1982-02-02', 102, 2),
       ('film3', 'description3', '1983-03-03', 103, 3),
       ('film4', 'description4', '1984-04-04', 104, 4);

INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES ('email1', 'login1', 'name1', '1981-01-01'),
       ('email2', 'login2', 'name2', '1982-02-02'),
       ('email3', 'login3', 'name3', '1983-03-03');