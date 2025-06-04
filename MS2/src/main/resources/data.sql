DROP TABLE IF EXISTS person;

CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name TEXT
);

INSERT INTO person (name) VALUES ('Mahesh');
INSERT INTO person (name) VALUES ('Singtel');