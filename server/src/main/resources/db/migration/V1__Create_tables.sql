CREATE TABLE housepy.users
(
    uid   VARCHAR PRIMARY KEY,
    name  VARCHAR NOT NULL,
    email VARCHAR NOT NULL
);

insert into housepy.Users values('ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Diego Ferreira da Silva', 'diegosiuniube@gmail.com');

CREATE TABLE housepy.auth_tokens
(
    user_id VARCHAR NOT NULL,
    token   VARCHAR NOT NULL,
    created timestamp NOT NULL,
    expires timestamp NOT NULL,
    PRIMARY KEY(user_id, token)
);

CREATE TABLE housepy.devices
(
    id    SERIAL PRIMARY KEY,
    owner VARCHAR NOT NULL,
    name  VARCHAR NOT NULL,
    type  char(3) NOT NULL --net or raw
);

CREATE TABLE housepy.sensors
(
    id char(3) NOT NULL,
    device_id BIGINT NOT NULL,
    data_type VARCHAR NOT NULL, --int, string, float, bool
    PRIMARY KEY(id, device_id)
);

CREATE TABLE housepy.commands
(
    id SERIAL NOT NULL,
    sensor_id char(3) NOT NULL,
    device_id BIGINT NOT NULL,
    value VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    PRIMARY KEY(id, sensor_id, device_id)
);

CREATE TABLE housepy.triggers
(
    id SERIAL NOT NULL,
    sensor_id char(3) NOT NULL,
    device_id BIGINT NOT NULL,
    to_sensor_id char(3) NOT NULL,
    to_device_id BIGINT NOT NULL,
    activate_value VARCHAR NOT NULL,
    set_value VARCHAR NOT NULL,
    duration VARCHAR NULL,
    PRIMARY KEY (id, sensor_id, device_id)
);