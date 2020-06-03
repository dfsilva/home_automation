CREATE TABLE housepy.Users
(
    uid   UUID PRIMARY KEY,
    name  VARCHAR NOT NULL,
    email VARCHAR NOT NULL
);

CREATE TABLE housepy.Devices
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR NOT NULL,
    type char(3) NOT NULL --net or raw
);

CREATE TABLE housepy.Sensors
(
    id char(3) NOT NULL,
    device_id BIGINT NOT NULL,
    data_type VARCHAR NOT NULL, --int, string, float, bool
    PRIMARY KEY(id, device_id)
);

CREATE TABLE housepy.Commands
(
    id BIGINT NOT NULL,
    sensor_id char(3) NOT NULL,
    device_id BIGINT NOT NULL,
    value VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    PRIMARY KEY(id, sensor_id, device_id)
);

CREATE TABLE housepy.Triggers
(
    id BIGINT NOT NULL,
    sensor_id char(3) NOT NULL,
    device_id BIGINT NOT NULL,
    to_sensor_id char(3) NOT NULL,
    to_device_id BIGINT NOT NULL,
    activate_value VARCHAR NOT NULL,
    set_value VARCHAR NOT NULL,
    duration VARCHAR NULL,
    PRIMARY KEY (id, sensor_id, device_id)
);