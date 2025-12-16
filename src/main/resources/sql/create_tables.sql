
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tabulated_function (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    data BYTEA NOT NULL,
    derivative BYTEA NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE composite_function (
    id BIGSERIAL PRIMARY KEY,
    expression TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);


INSERT INTO role (name) VALUES ('ADMIN');
INSERT INTO role (name) VALUES ('CREATOR');
INSERT INTO role (name) VALUES ('VIEWER');
