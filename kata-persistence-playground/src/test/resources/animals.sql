CREATE TABLE animals (
	id	INT
);
INSERT INTO animals VALUES (1);
INSERT INTO animals VALUES (2);
INSERT INTO animals VALUES (3);
INSERT INTO animals VALUES (4);
INSERT INTO animals VALUES (5);
INSERT INTO animals VALUES (6);


CREATE TABLE property_types (
	id	INT,
	p_type	VARCHAR(32) NOT NULL UNIQUE
);
INSERT INTO property_types VALUES (1, 'string');
INSERT INTO property_types VALUES (2, 'date');
INSERT INTO property_types VALUES (3, 'integer');


CREATE TABLE properties (
	id	INT,
	p_key	VARCHAR(32) NOT NULL UNIQUE,
	p_type  INT NOT NULL
);
INSERT INTO properties VALUES (1, 'name', 1);
INSERT INTO properties VALUES (2, 'dob', 2); -- date of birth
INSERT INTO properties VALUES (3, 'legs', 3);
INSERT INTO properties VALUES (4, 'species', 1);


CREATE TABLE animal_data (
	animal 	INT,
	property INT,
	data 	VARCHAR(32)
);
INSERT INTO animal_data VALUES (1, 1, 'adam');
INSERT INTO animal_data VALUES (1, 2, '1980-10-15');
INSERT INTO animal_data VALUES (1, 3, '4');
INSERT INTO animal_data VALUES (1, 4, 'dog');

INSERT INTO animal_data VALUES (2, 1, 'bob');
INSERT INTO animal_data VALUES (2, 2, '1980-10-31');
INSERT INTO animal_data VALUES (2, 3, '4');
INSERT INTO animal_data VALUES (2, 4, 'cat');

INSERT INTO animal_data VALUES (3, 1, 'chris');
INSERT INTO animal_data VALUES (3, 2, '1980-11-01');
INSERT INTO animal_data VALUES (3, 3, '8');
INSERT INTO animal_data VALUES (3, 4, 'spider');

INSERT INTO animal_data VALUES (4, 1, 'dan');
INSERT INTO animal_data VALUES (4, 2, '1981-01-01');
INSERT INTO animal_data VALUES (4, 3, '6');
INSERT INTO animal_data VALUES (4, 4, 'ant');

INSERT INTO animal_data VALUES (5, 1, 'evan');
INSERT INTO animal_data VALUES (5, 2, '1980-12-31');
INSERT INTO animal_data VALUES (5, 3, '4');
INSERT INTO animal_data VALUES (5, 4, 'fly');

INSERT INTO animal_data VALUES (6, 1, 'fredrik');
INSERT INTO animal_data VALUES (6, 2, '1981-10-15');
INSERT INTO animal_data VALUES (6, 3, '4');
INSERT INTO animal_data VALUES (6, 4, 'fly');
