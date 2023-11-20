DROP TABLE if exists friends cascade;
DROP TABLE if exists likes cascade;
DROP TABLE if exists film_genres cascade;
DROP TABLE if exists mpa cascade;
DROP TABLE if exists genres cascade;
DROP TABLE if exists users cascade;
DROP TABLE if exists films cascade;

CREATE TABLE IF NOT EXISTS mpa (
                                   id integer not null PRIMARY KEY auto_increment,
                                   name varchar(255)
);

CREATE TABLE IF NOT EXISTS films (
                        id integer PRIMARY KEY auto_increment,
                        name varchar(255),
                        description varchar(200),
                        releaseDate date,
                        duration integer,
                        mpa_id integer references mpa(id)
);

CREATE TABLE IF NOT EXISTS users (
                        id integer not null PRIMARY KEY auto_increment,
                        email varchar(255) not null UNIQUE,
                        login varchar(255) not null UNIQUE,
                        name varchar(255),
                        birthday date
);

CREATE TABLE IF NOT EXISTS genres (
                        id integer not null PRIMARY KEY auto_increment,
                        name varchar(255)
);

CREATE TABLE IF NOT EXISTS film_genres (
                        film_id integer references films(id),
                        genre_id integer references genres(id),
                        primary key (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
                         user_id integer references users(id),
                         film_id integer references films(id),
                         primary key (user_id, film_id)

);

CREATE TABLE IF NOT EXISTS friends (
                           user_id integer references users(id),
                           friend_id integer references users(id),
                           primary key (user_id, friend_id)
);