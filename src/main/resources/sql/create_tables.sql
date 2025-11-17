CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);



CREATE TABLE tabulated_function (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    data BYTEA NOT NULL,
    derivative BYTEA NOT NULL
);

CREATE TABLE composite_function (
    id BIGSERIAL PRIMARY KEY,
    expression TEXT NOT NULL
);
