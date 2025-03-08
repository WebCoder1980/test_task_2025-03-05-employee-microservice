CREATE TABLE IF NOT EXISTS counter (
	"name" varchar(75) NOT NULL,
	currentid int8 NOT NULL,
	CONSTRAINT counter_pkey PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS position_types (
    id_ int8 NOT NULL,
    name varchar(150) NOT NULL,
    CONSTRAINT position_types_pkey PRIMARY KEY (id_)
);

CREATE TABLE IF NOT EXISTS shop (
    id_ int8 NOT NULL,
    name varchar(250) NOT NULL,
    address text NOT NULL,
    CONSTRAINT shop_pkey PRIMARY KEY (id_)
);

CREATE TABLE IF NOT EXISTS electro_item_type (
    id_ int8 NOT NULL,
    name varchar(150) NOT NULL,
    CONSTRAINT electro_item_type_pkey PRIMARY KEY (id_)
);

CREATE TABLE IF NOT EXISTS store_employee (
	id_ int8 NOT NULL,
	lastname varchar(100) NOT NULL,
	firstname varchar(100) NOT NULL,
	patronymic varchar(100) NOT NULL,
	birthDate date NOT NULL,
	positionId int8 NOT NULL,
    shopId int8 NOT NULL,
	gender bool NOT NULL,
	CONSTRAINT store_employee_pkey PRIMARY KEY (id_),
	CONSTRAINT fk_employee_position FOREIGN KEY (positionId) REFERENCES position_types(id_),
    CONSTRAINT fk_employee_shop FOREIGN KEY (shopId) REFERENCES shop(id_)
);

CREATE TABLE IF NOT EXISTS electro_employee (
    electroTypeId int8 NOT NULL,
    employeeId int8 NOT NULL,
    CONSTRAINT electro_employee_pkey PRIMARY KEY (electroTypeId, employeeId),
    CONSTRAINT fk_electro_item_type FOREIGN KEY (electroTypeId) REFERENCES electro_item_type(id_),
    CONSTRAINT fk_employee FOREIGN KEY (employeeId) REFERENCES store_employee(id_)
);