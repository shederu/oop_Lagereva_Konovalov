-- 1. User (для авторизации, без приватности функций)
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- 2. TabulatedFunction (общая для всех, без user_id)
CREATE TABLE tabulated_function (
    id BIGSERIAL PRIMARY KEY,
    function_name VARCHAR(100) NOT NULL,
    serialized_data BYTEA NOT NULL
);

-- 3. CompositeFunction (хранит вычисленные значения y и derivative)
CREATE TABLE composite_function (
    id BIGSERIAL PRIMARY KEY,
    expression TEXT NOT NULL,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    derivative DOUBLE PRECISION NOT NULL
);
