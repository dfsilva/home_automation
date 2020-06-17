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
    address   VARCHAR PRIMARY KEY,
    owner     VARCHAR NOT NULL,
    name      VARCHAR NOT NULL,
    type      CHAR(3) NOT NULL, --net or raw
    position  SMALLINT NOT NULL
);

insert into housepy.devices values ('s_11','ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Sala', 'raw', 0);
insert into housepy.devices values ('s_10','ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Quarto Casal', 'raw', 1);
insert into housepy.devices values ('s_12','ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Quarto Luísa', 'raw', 2);
insert into housepy.devices values ('02','ChCTNNSLXVRXghsiBxZLAmB4Adq2', 'Escritório', 'net', 3);

CREATE TABLE housepy.sensors
(
    number         BIGINT NOT NULL,
    type           CHAR(2) NOT NULL,
    device_address VARCHAR NOT NULL,
    data_type      VARCHAR NOT NULL, --int, string, float, bool
    name           VARCHAR NOT NULL,
    position       SMALLINT NOT NULL,
    PRIMARY KEY(number, type, device_address)
);

insert into housepy.sensors values (1, 'tp', 's_10','float', 'Temperatura', 0);
insert into housepy.sensors values (1, 'hm', 's_10','float', 'Humidade', 1);
insert into housepy.sensors values (1, 'ps', 's_10','bool', 'Presença', 2);
insert into housepy.sensors values (1, 'sm', 's_10','int', 'Fumaça', 3);
insert into housepy.sensors values (1, 'ld', 's_10','bool', 'Humidificador', 4);

insert into housepy.sensors values (1, 'tp', 's_11','float', 'Temperatura', 0);
insert into housepy.sensors values (1, 'hm', 's_11','float', 'Humidade', 1);
insert into housepy.sensors values (1, 'ps', 's_11','bool', 'Presença', 2);
insert into housepy.sensors values (1, 'sm', 's_11','int', 'FUMAÇA', 3);
insert into housepy.sensors values (1, 'ld', 's_11','bool', 'Portão de Entrada', 4);
insert into housepy.sensors values (2, 'ld', 's_11','bool', 'Portão de Saída', 5);

insert into housepy.sensors values (1, 'tp', 's_12','float', 'Temperatura', 0);
insert into housepy.sensors values (1, 'hm', 's_12','float', 'Humidade', 1);
insert into housepy.sensors values (1, 'ps', 's_12','bool', 'Presença', 2);
insert into housepy.sensors values (1, 'sm', 's_12','int', 'FUMAÇA', 3);

insert into housepy.sensors values (1, 'tp', '02','float', 'Temperatura', 0);
insert into housepy.sensors values (1, 'hm', '02','float', 'Humidade', 1);
insert into housepy.sensors values (1, 'ps', '02','bool', 'Presença', 2);


CREATE TABLE housepy.commands
(
    id SERIAL PRIMARY KEY,
    sensor_id BIGINT NOT NULL,
    value VARCHAR NOT NULL,
    description VARCHAR NOT NULL
);

CREATE TABLE housepy.triggers
(
    id SERIAL NOT NULL,
    sensor_id BIGINT NOT NULL,
    to_sensor_id BIGINT NOT NULL,
    activate_value VARCHAR NOT NULL,
    set_value VARCHAR NOT NULL,
    duration VARCHAR NULL
);