CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);


INSERT INTO users (username, password, role)
VALUES ('testUser', '{noop}$2a$10$D7.QOjQEn5tb8JpYvPvzPug/aZDOjgh52Fg8Dj8IjsumZyDdxu5Eu', 'USER');

INSERT INTO users (username, password, role)
VALUES ('testAdmin', '{noop}$2a$10$D7.QOjQEn5tb8JpYvPvzPug/aZDOjgh52Fg8Dj8IjsumZyDdxu5Eu', 'ADMIN');
