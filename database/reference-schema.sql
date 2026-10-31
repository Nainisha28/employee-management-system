CREATE TABLE IF NOT EXISTS departments 
(id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(40) NOT NULL UNIQUE, code_prefix CHAR(1) NOT NULL UNIQUE);
CREATE TABLE IF NOT EXISTS roles 
(id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(80) NOT NULL UNIQUE);
INSERT IGNORE INTO departments(name,code_prefix) VALUES ('IT','I'),('HR','H'),('Finance','F'),('Sales','S'),('Admin','A');
INSERT IGNORE INTO roles(name) VALUES ('Software Engineer'),('HR Executive'),('Accountant'),('Sales Executive'),('Administrator'),('Team Lead');

