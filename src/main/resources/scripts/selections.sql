SELECT * FROM "user" WHERE id = ?;
SELECT * FROM "user" WHERE login = ?;

SELECT * FROM tabulated_function WHERE id = ?;
SELECT * FROM tabulated_function WHERE name = ?;
SELECT * FROM tabulated_function WHERE user_id = ?;
SELECT * FROM tabulated_function WHERE name = ? AND user_id = ?;

SELECT * FROM composite_function WHERE id = ?;
SELECT * FROM composite_function WHERE expression = ?;
SELECT * FROM composite_function WHERE expression LIKE ?;
SELECT * FROM composite_function WHERE user_id = ?;
SELECT * FROM composite_function WHERE expression LIKE ? AND user_id = ?;
