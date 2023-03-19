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