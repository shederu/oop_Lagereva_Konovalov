DELETE FROM "user" WHERE id = ?;
DELETE FROM "user" WHERE login = ?;

DELETE FROM tabulated_function WHERE id = ?;
DELETE FROM tabulated_function WHERE name = ?;

DELETE FROM composite_function WHERE id = ?;
DELETE FROM composite_function WHERE expression = ?;

