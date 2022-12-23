create table if not exists GENRE
(
    GENRE_ID   INTEGER AUTO_INCREMENT PRIMARY KEY,
    GENRE_NAME CHARACTER VARYING(50) not null
);

create table if not exists MPA
(
    MPA_ID   INTEGER               not null PRIMARY KEY,
    MPA_NAME CHARACTER VARYING(20) not null
);

create table if not exists FILMS
(
    FILM_ID      INTEGER AUTO_INCREMENT PRIMARY KEY,
    FILM_NAME    CHARACTER VARYING(50)  not null,
    DESCRIPTION  CHARACTER VARYING(500) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    MPA_ID       INTEGER                not null,
    FOREIGN KEY (MPA_ID) REFERENCES MPA (MPA_ID)
);

create table if not exists FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    FOREIGN KEY (FILM_ID) REFERENCES FILMS (FILM_ID) on delete cascade,
    FOREIGN KEY (GENRE_ID) REFERENCES GENRE (GENRE_ID)
);

create table if not exists DIRECTORS
(
    DIRECTOR_ID   INTEGER auto_increment,
    DIRECTOR_NAME CHARACTER VARYING(50) not null
        constraint DIRECTORS_UNIQUE_NAME
            unique,
    constraint "DIRECTORS_pk"
        primary key (DIRECTOR_ID)
);
create table if not exists FILMS_DIRECTORS
(
    FILM_DIRECTOR_ID INTEGER auto_increment
        primary key,
    DIRECTOR_ID      INTEGER not null,
    FILM_ID          INTEGER not null,
    constraint "FILMS_DIRECTORS_FILMS_null_fk"
        foreign key (FILM_ID) references FILMS
            on update cascade on delete cascade,
    constraint FILMS_DIRECTORS_DIRECTORS_DIRECTOR_ID_FK
        foreign key (DIRECTOR_ID) references DIRECTORS
            on update cascade on delete cascade
);

create table if not exists USERS
(
    USER_ID    INTEGER AUTO_INCREMENT PRIMARY KEY,
    USER_EMAIL CHARACTER VARYING(50) UNIQUE not null,
    USER_LOGIN CHARACTER VARYING(50)        not null,
    USER_NAME  CHARACTER VARYING(50),
    BIRTHDAY   DATE                         not null
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    FOREIGN KEY (FILM_ID) REFERENCES FILMS (FILM_ID) on delete cascade,
    FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) on delete cascade
);

create table if not exists FRIENDSHIP
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    FOREIGN KEY (USER_ID) REFERENCES users (USER_ID) on delete cascade,
    FOREIGN KEY (FRIEND_ID) REFERENCES users (USER_ID) on delete cascade
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    FOREIGN KEY (FILM_ID) REFERENCES FILMS (FILM_ID) on delete cascade,
    FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) on delete cascade
);

create table if not exists REVIEWS
(
    REVIEW_ID   INTEGER AUTO_INCREMENT PRIMARY KEY,
    CONTENT     CHARACTER VARYING(500) not null,
    USEFUL      INTEGER,
    IS_POSITIVE BOOLEAN                not null,
    FILM_ID     INTEGER                not null,
    USER_ID     INTEGER                not null,
    FOREIGN KEY (FILM_ID) REFERENCES FILMS (FILM_ID) on delete cascade,
    FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) on delete cascade
);

create table if not exists REVIEW_LIKES
(
    REVIEW_ID INTEGER not null,
    USER_ID   INTEGER not null,
    IS_LIKE   BOOLEAN,
    FOREIGN KEY (REVIEW_ID) REFERENCES REVIEWS (REVIEW_ID) on delete cascade,
    FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) on update cascade on delete cascade
);
create table if not exists FEED
(
    FEED_TIMESTAMP  TIMESTAMP         not null,
    USER_ID    INTEGER                not null,
    EVENT_TYPE CHARACTER VARYING(10)  not null,
    OPERATION  CHARACTER VARYING(10)  not null,
    EVENT_ID   INTEGER auto_increment not null,
    ENTITY_ID  INTEGER                not null,
    FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) on delete cascade
);