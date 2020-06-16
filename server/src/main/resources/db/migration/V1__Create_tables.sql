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
    id      SERIAL PRIMARY KEY,
    address VARCHAR NOT NULL,
    owner   VARCHAR NOT NULL,
    name    VARCHAR NOT NULL,
    type    char(3) NOT NULL --net or raw
);

insert into housepy.devices values (1, 's_10','ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Quarto Casal', 'raw');
insert into housepy.devices values (2, 's_11','ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Sala', 'raw');
insert into housepy.devices values (3, 's_12','ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Quarto Luísa', 'raw');
insert into housepy.devices values (4, '02','ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Escritório', 'net');

CREATE TABLE housepy.sensors
(
    id char(3) NOT NULL,
    device_id BIGINT NOT NULL,
    data_type VARCHAR NOT NULL, --int, string, float, bool
    name    VARCHAR NOT NULL,
    PRIMARY KEY(id, device_id)
);

insert into housepy.sensors values ('tp', 1,'float', 'Temperatura');
insert into housepy.sensors values ('hm', 1,'float', 'Humidade');
insert into housepy.sensors values ('ps', 1,'bool', 'Presença');
insert into housepy.sensors values ('sm', 1,'int', 'FUMAÇA');

insert into housepy.sensors values ('tp', 2,'float', 'Temperatura');
insert into housepy.sensors values ('hm', 2,'float', 'Humidade');
insert into housepy.sensors values ('ps', 2,'bool', 'Presença');
insert into housepy.sensors values ('sm', 2,'int', 'FUMAÇA');

insert into housepy.sensors values ('tp', 3,'float', 'Temperatura');
insert into housepy.sensors values ('hm', 3,'float', 'Humidade');
insert into housepy.sensors values ('ps', 3,'bool', 'Presença');
insert into housepy.sensors values ('sm', 3,'int', 'FUMAÇA');


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