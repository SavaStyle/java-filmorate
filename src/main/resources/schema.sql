create table if not exists GENRE
(
    GENRE_ID   INTEGER AUTO_INCREMENT PRIMARY KEY,
    GENRE_NAME CHARACTER VARYING(50) not null
);

create table if not exists MPA
(
    MPA_ID   INTEGER not null PRIMARY KEY,
    MPA_NAME CHARACTER VARYING(20) not null
);

create table if not exists FILMS
(
    FILM_ID      INTEGER AUTO_INCREMENT PRIMARY KEY,
    FILM_NAME    CHARACTER VARYING(50) not null,
    DESCRIPTION  CHARACTER VARYING(500) not null,
    RELEASE_DATE DATE not null,
    DURATION     INTEGER not null,
    MPA_ID       INTEGER not null,
    FOREIGN KEY (MPA_ID)  REFERENCES MPA(MPA_ID)
);

create table if not exists FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    FOREIGN KEY (FILM_ID) REFERENCES FILMS (FILM_ID) on delete cascade,
    FOREIGN KEY (GENRE_ID) REFERENCES GENRE (GENRE_ID)
);

create table if not exists USERS
(
    USER_ID    INTEGER AUTO_INCREMENT PRIMARY KEY,
    USER_EMAIL CHARACTER VARYING(50) UNIQUE not null,
    USER_LOGIN CHARACTER VARYING(50) not null,
    USER_NAME  CHARACTER VARYING(50),
    BIRTHDAY   DATE not null
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    FOREIGN KEY (FILM_ID) REFERENCES FILMS (FILM_ID)  on delete cascade,
    FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) on delete cascade
);

create table if not exists FRIENDSHIP
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    FOREIGN KEY (USER_ID) REFERENCES users (USER_ID) on delete cascade,
    FOREIGN KEY (FRIEND_ID) REFERENCES users (USER_ID) on delete cascade
);
