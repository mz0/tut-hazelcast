-- as DB-root
-- mysql> CREATE USER zyx@localhost IDENTIFIED BY '888';
-- mysql> GRANT ALL PRIVILEGES ON xyz.* to zyx@localhost;

-- $ mysql -uzyx -p888 < user.sql
CREATE DATABASE xyz;
USE xyz;

CREATE TABLE user (
 login VARCHAR(255) PRIMARY KEY,
 password VARCHAR(255) NOT NULL,
 salt VARCHAR(255) NOT NULL
);

INSERT INTO user(login, salt, password)
VALUES ('admin', '43e7d427e16e5dad9668b02e51227a12', CONCAT(
'5ad5220c22d98de88893cdca9cb620343404c9ef0d3f58db48160a954dead0bd',
'2321e2ae0de22b18df8e7e853638b9e68ad0d959452243c88a30c9585b01d4e1')
);
