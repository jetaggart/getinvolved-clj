CREATE TABLE users
(id serial PRIMARY KEY NOT NULL UNIQUE,
 username VARCHAR(30),
 first_name VARCHAR(255),
 last_name VARCHAR(255),
 admin BOOLEAN,
 last_login TIME,
 is_active BOOLEAN,
 password VARCHAR(100));
