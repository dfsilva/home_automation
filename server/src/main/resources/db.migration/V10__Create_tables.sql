CREATE TABLE Users
(
    uid   UUID PRIMARY KEY,
    name  VARCHAR NOT NULL,
    email VARCHAR NOT NULL
);

CREATE TABLE Devices
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR NOT NULL,
    type char(3) NOT NULL --net or raw
);

CREATE TABLE Sensors
(
    id char(3) PRIMARY KEY,
    device_id BIGINT PRIMARY KEY,
    data_type VARCHAR NOT NULL --int, string, float, bool
);

CREATE TABLE Commands
(
    id BIGINT PRIMARY KEY,
    sensor_id char(3) PRIMARY KEY,
    device_id BIGINT PRIMARY KEY,
    value VARCHAR NOT NULL,
    description VARCHAR NOT NULL
);

CREATE TABLE Triggers
(
    id BIGINT PRIMARY KEY,
    sensor_id char(3) PRIMARY KEY,
    device_id BIGINT PRIMARY KEY,
    to_sensor_id char(3),
    to_device_id BIGINT PRIMARY KEY,
    activate_value VARCHAR NOT NULL,
    set_value VARCHAR NOT NULL,
    duration VARCHAR NULL
);