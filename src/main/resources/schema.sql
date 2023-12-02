DROP TABLE if exists friends cascade;
DROP TABLE if exists likes cascade;
DROP TABLE if exists film_genres cascade;
DROP TABLE if exists mpa cascade;
DROP TABLE if exists genres cascade;
DROP TABLE if exists review_likes cascade;
DROP TABLE if exists reviews cascade;
DROP TABLE if exists film_directors cascade;
DROP TABLE if exists directors cascade;
DROP TABLE if exists events cascade;
DROP TABLE if exists users cascade;
DROP TABLE if exists films cascade;

CREATE TABLE IF NOT EXISTS mpa (
                       id bigint not null PRIMARY KEY auto_increment,
                       name varchar(255)
);
CREATE TABLE IF NOT EXISTS directors (
                        id bigint not null primary key auto_increment,
                        name varchar(255)
);

CREATE TABLE IF NOT EXISTS films (
                        id bigint PRIMARY KEY auto_increment,
                        name varchar(255),
                        description varchar(200),
                        releaseDate date,
                        duration integer,
                        mpa_id integer references mpa(id)
);

CREATE TABLE IF NOT EXISTS users (
                        id bigint not null PRIMARY KEY auto_increment,
                        email varchar(255) not null UNIQUE,
                        login varchar(255) not null UNIQUE,
                        name varchar(255),
                        birthday date
);

CREATE TABLE IF NOT EXISTS reviews (
                       id bigint PRIMARY KEY auto_increment,
                       is_positive boolean,
                       content varchar(255),
                       user_id bigint references users(id),
                       film_id bigint references films(id),
                       useful integer default 0
);

CREATE TABLE IF NOT EXISTS review_likes (
    review_id bigint references reviews(id) on delete cascade,
    user_id bigint references users(id),
    primary key (review_id, user_id),
    is_like boolean
);

CREATE TABLE IF NOT EXISTS film_directors (
                      film_id bigint references films(id) on delete cascade,
                      director_id bigint references directors(id) on delete cascade,
                      primary key (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS events (
                      id bigint PRIMARY KEY auto_increment,
                      user_id bigint references users(id),
                      entity_id bigint references users(id),
                      event_type varchar(255),
                      operation varchar(255),
                      event_time timestamp
);

CREATE TABLE IF NOT EXISTS genres (
                        id bigint not null PRIMARY KEY auto_increment,
                        name varchar(255)
);

CREATE TABLE IF NOT EXISTS film_genres (
                        film_id bigint references films(id) on delete cascade,
                        genre_id bigint references genres(id) on delete cascade,
                        primary key (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
                         user_id bigint references users(id) on delete cascade,
                         film_id bigint references films(id) on delete cascade,
                         primary key (user_id, film_id)

);

CREATE TABLE IF NOT EXISTS friends (
                           user_id bigint references users(id) on delete cascade,
                           friend_id bigint references users(id) on delete cascade,
                           primary key (user_id, friend_id)
);