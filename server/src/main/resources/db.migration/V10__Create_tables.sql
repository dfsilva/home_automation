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
    device_id BIGINT PRIMARY KEY,
    sensor_id char(3) PRIMARY KEY,
    data_type VARCHAR NOT NULL --int, string, float, bool
);

CREATE TABLE Triggers
(
    device_id BIGINT PRIMARY KEY,
    sensor_id char(3) PRIMARY KEY,
    to_sensor_id char(3) PRIMARY KEY,
    init_value VARCHAR NOT NULL,
    end_value VARCHAR NULL,
    duration VARCHAR
);

ALTER TABLE Sensors
    ADD CONSTRAINT sensor_device_fk FOREIGN KEY (device_id) REFERENCES Devices (id) ON UPDATE RESTRICT ON DELETE CASCADE;
ALTER TABLE Triggers
    ADD CONSTRAINT trigger_sensor_fk FOREIGN KEY (device_id, sensor_id) REFERENCES Sensors (device_id, sensor_id) ON UPDATE RESTRICT ON DELETE CASCADE;