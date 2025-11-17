SELECT * FROM "user" WHERE id = ?;
SELECT * FROM "user" WHERE login = ?;

SELECT * FROM tabulated_function WHERE id = ?;
SELECT * FROM tabulated_function WHERE name = ?;

SELECT * FROM composite_function WHERE id = ?;
SELECT * FROM composite_function WHERE expression = ?;
SELECT * FROM composite_function WHERE expression LIKE ?;

SELECT * FROM "user" WHERE login = 'test_user';

