-- Connect to the database
\c dockerdemo;

-- Drop table if exists
DROP TABLE IF EXISTS person;

-- Create table
CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name TEXT
);

INSERT INTO person (name) VALUES ('dockerData1');
INSERT INTO person (name) VALUES ('dockerData2');