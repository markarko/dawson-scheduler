CREATE TABLE courses(
	number VARCHAR(10) UNIQUE NOT NULL,
    title VARCHAR(30),
    coid INT PRIMARY KEY
);
CREATE TABLE sections(
	section INT NOT NULL,
    coid INT REFERENCES courses(coid),
    seid INT PRIMARY KEY
);
CREATE TABLE classes(
	name VARCHAR(30),
    location VARCHAR(5),
    seid INT REFERENCES sections(seid),
    clid INT PRIMARY KEY
);