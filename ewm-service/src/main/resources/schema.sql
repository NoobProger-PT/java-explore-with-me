DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS participations CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    CONSTRAINT users_table_pk primary key (id),
    CONSTRAINT user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    pinned BOOLEAN,
    title  VARCHAR,
    CONSTRAINT compilation_pk primary key (id)
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR NOT NULL,
    CONSTRAINT category_name UNIQUE (name),
    CONSTRAINT categories_table_pk primary key (id)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id VARCHAR NOT NULL,
    description VARCHAR(7000),
    event_Date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    paid BOOLEAN,
    participant_Limit INTEGER,
    request_Moderation BOOLEAN,
    title VARCHAR(120) NOT NULL,
    created_On TIMESTAMP WITHOUT TIME ZONE,
    published_On TIMESTAMP WITHOUT TIME ZONE,
    state VARCHAR,
    initiator_id BIGINT NOT NULL,
    lat FLOAT,
    lon FLOAT,
    CONSTRAINT events_table_pk primary key (id),
    CONSTRAINT initiator_id_fk foreign key (initiator_id) references users,
    CONSTRAINT category_id_fk foreign key (category_id) references categories
);

CREATE TABLE IF NOT EXISTS participation (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_id  BIGINT NOT NULL,
    requester_id  BIGINT NOT NULL,
    status VARCHAR NOT NULL,
    CONSTRAINT participation_table_pk primary key (id),
    CONSTRAINT requester_id_fk foreign key (requester_id) references users,
    CONSTRAINT event_id_fk foreign key (event_id) references events
);

CREATE TABLE IF NOT EXISTS compilations_events (
    events_id BIGINT,
    compilation_id BIGINT,
    CONSTRAINT compilations_events_pk primary key (events_id, compilation_id),
    CONSTRAINT events_id_fk foreign key (events_id) references events,
    CONSTRAINT compilation_id_fk foreign key (compilation_id) references compilations

);