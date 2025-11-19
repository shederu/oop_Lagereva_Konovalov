package ru.ssau.tk._shederu_._lab1_.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompositeFunctionDaoTest {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static DataSourceProvider dataSourceProvider;
    private CompositeFunctionDao compositeFunctionDao;
    private UserDao userDao;
    private Long testUserId;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        dataSourceProvider = new DataSourceProvider(URL, USERNAME, PASSWORD);
        createTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        compositeFunctionDao = new CompositeFunctionDao(dataSourceProvider);
        userDao = new UserDao(dataSourceProvider);

        clearTables();

        UserEntity testUser = new UserEntity("test_user", "password123");
        testUserId = userDao.create(testUser);
    }

    private static void createTables() throws SQLException {
        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS \"user\" (" +
                            "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                            "    login VARCHAR(50) UNIQUE NOT NULL," +
                            "    password VARCHAR(255) NOT NULL" +
                            ")"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS composite_function (" +
                            "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                            "    expression TEXT NOT NULL," +
                            "    user_id BIGINT NOT NULL," +
                            "    FOREIGN KEY (user_id) REFERENCES \"user\"(id) ON DELETE CASCADE" +
                            ")"
            );
        }
    }

    private void clearTables() throws SQLException {
        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM composite_function");
            stmt.execute("DELETE FROM \"user\"");
        }
    }

    @Test
    void testInsertOne() {
        CompositeFunctionEntity entity = new CompositeFunctionEntity("sin(x) + cos(x)", testUserId);

        Long id = compositeFunctionDao.create(entity);

        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    void testInsertMultiple() {
        compositeFunctionDao.create(new CompositeFunctionEntity("f(g(x))", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("h(k(x))", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("sin(cos(x))", testUserId));

        List<CompositeFunctionEntity> all = compositeFunctionDao.findAll();
        assertEquals(3, all.size());
    }

    @Test
    void testInsertDifferentExpressions() {
        Long id1 = compositeFunctionDao.create(new CompositeFunctionEntity("x^2", testUserId));
        Long id2 = compositeFunctionDao.create(new CompositeFunctionEntity("sin(x) + cos(x) * tan(x)", testUserId));
        Long id3 = compositeFunctionDao.create(new CompositeFunctionEntity("log(exp(x))", testUserId));

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);

        Optional<CompositeFunctionEntity> func1 = compositeFunctionDao.findById(id1);
        Optional<CompositeFunctionEntity> func2 = compositeFunctionDao.findById(id2);
        Optional<CompositeFunctionEntity> func3 = compositeFunctionDao.findById(id3);

        assertTrue(func1.isPresent());
        assertEquals("x^2", func1.get().getExpression());

        assertTrue(func2.isPresent());
        assertEquals("sin(x) + cos(x) * tan(x)", func2.get().getExpression());

        assertTrue(func3.isPresent());
        assertEquals("log(exp(x))", func3.get().getExpression());
    }

    @Test
    void testFindById() {
        CompositeFunctionEntity entity = new CompositeFunctionEntity("f(g(h(x)))", testUserId);
        Long id = compositeFunctionDao.create(entity);

        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findById(id);

        assertTrue(found.isPresent());
        assertEquals("f(g(h(x)))", found.get().getExpression());
        assertEquals(testUserId, found.get().getUserId());
    }

    @Test
    void testFindByExpression() {
        compositeFunctionDao.create(new CompositeFunctionEntity("sin(cos(x))", testUserId));

        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findByExpression("sin(cos(x))");

        assertTrue(found.isPresent());
        assertEquals("sin(cos(x))", found.get().getExpression());
    }

    @Test
    void testFindByExpressionNotFound() {
        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findByExpression("nonexistent(x)");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByExpressionContaining() {
        compositeFunctionDao.create(new CompositeFunctionEntity("sin(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("sin(cos(x))", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("cos(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("tan(x)", testUserId));

        List<CompositeFunctionEntity> found = compositeFunctionDao.findByExpressionContaining("sin");

        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(f -> f.getExpression().equals("sin(x)")));
        assertTrue(found.stream().anyMatch(f -> f.getExpression().equals("sin(cos(x))")));
    }

    @Test
    void testFindByUserId() {
        compositeFunctionDao.create(new CompositeFunctionEntity("f1(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("f2(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("f3(x)", testUserId));

        List<CompositeFunctionEntity> found = compositeFunctionDao.findByUserId(testUserId);

        assertEquals(3, found.size());
        assertTrue(found.stream().allMatch(f -> f.getUserId().equals(testUserId)));
    }

    @Test
    void testFindByExpressionContainingAndUserId() {
        UserEntity anotherUser = new UserEntity("another_user", "pass");
        Long anotherUserId = userDao.create(anotherUser);

        compositeFunctionDao.create(new CompositeFunctionEntity("sin(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("sin(y)", anotherUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("cos(x)", testUserId));

        List<CompositeFunctionEntity> found = compositeFunctionDao.findByExpressionContainingAndUserId("sin", testUserId);

        assertEquals(1, found.size());
        assertEquals("sin(x)", found.get(0).getExpression());
        assertEquals(testUserId, found.get(0).getUserId());
    }

    @Test
    void testFindAll() {
        compositeFunctionDao.create(new CompositeFunctionEntity("a(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("b(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("c(x)", testUserId));

        List<CompositeFunctionEntity> all = compositeFunctionDao.findAll();

        assertEquals(3, all.size());
    }

    @Test
    void testUpdateExpression() {
        Long id = compositeFunctionDao.create(new CompositeFunctionEntity("old_expression", testUserId));

        boolean updated = compositeFunctionDao.updateExpression(id, "new_expression");

        assertTrue(updated);
        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals("new_expression", found.get().getExpression());
    }

    @Test
    void testUpdateUserId() {
        UserEntity newUser = new UserEntity("new_user", "pass");
        Long newUserId = userDao.create(newUser);

        Long id = compositeFunctionDao.create(new CompositeFunctionEntity("func(x)", testUserId));

        boolean updated = compositeFunctionDao.updateUserId(id, newUserId);

        assertTrue(updated);
        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(newUserId, found.get().getUserId());
    }

    @Test
    void testUpdateNonExistent() {
        boolean updated = compositeFunctionDao.updateExpression(999L, "new_expression");

        assertFalse(updated);
    }

    @Test
    void testDeleteById() {
        Long id = compositeFunctionDao.create(new CompositeFunctionEntity("del1(x)", testUserId));

        boolean deleted = compositeFunctionDao.deleteById(id);

        assertTrue(deleted);
        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByExpression() {
        compositeFunctionDao.create(new CompositeFunctionEntity("del2(x)", testUserId));

        boolean deleted = compositeFunctionDao.deleteByExpression("del2(x)");

        assertTrue(deleted);
        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findByExpression("del2(x)");
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteNonExistent() {
        boolean deleted = compositeFunctionDao.deleteById(999L);

        assertFalse(deleted);
    }

    @Test
    void testDeleteAll() {
        compositeFunctionDao.create(new CompositeFunctionEntity("f1(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("f2(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("f3(x)", testUserId));

        compositeFunctionDao.deleteAll();

        List<CompositeFunctionEntity> all = compositeFunctionDao.findAll();
        assertEquals(0, all.size());
    }

    @Test
    void testCascadeDelete() {
        compositeFunctionDao.create(new CompositeFunctionEntity("func1(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("func2(x)", testUserId));

        userDao.deleteById(testUserId);

        List<CompositeFunctionEntity> functions = compositeFunctionDao.findByUserId(testUserId);
        assertEquals(0, functions.size());
    }

    @Test
    void testLongExpression() {
        StringBuilder longExpr = new StringBuilder("f(");
        for (int i = 0; i < 100; i++) {
            longExpr.append("g(");
        }
        longExpr.append("x");
        for (int i = 0; i < 100; i++) {
            longExpr.append(")");
        }
        longExpr.append(")");

        String expression = longExpr.toString();
        Long id = compositeFunctionDao.create(new CompositeFunctionEntity(expression, testUserId));

        assertNotNull(id);
        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(expression, found.get().getExpression());
    }

    @Test
    void testSpecialCharacters() {
        String specialExpr = "f(x) = sin(π*x) + cos(θ) * √2 + ∫dx";
        Long id = compositeFunctionDao.create(new CompositeFunctionEntity(specialExpr, testUserId));

        assertNotNull(id);
        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findByExpression(specialExpr);
        assertTrue(found.isPresent());
        assertEquals(specialExpr, found.get().getExpression());
    }

    @Test
    void testEmptyExpression() {
        Long id = compositeFunctionDao.create(new CompositeFunctionEntity("", testUserId));

        assertNotNull(id);
        Optional<CompositeFunctionEntity> found = compositeFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals("", found.get().getExpression());
    }

    @Test
    void testFindByEmptyPattern() {
        compositeFunctionDao.create(new CompositeFunctionEntity("func1(x)", testUserId));
        compositeFunctionDao.create(new CompositeFunctionEntity("func2(x)", testUserId));

        List<CompositeFunctionEntity> found = compositeFunctionDao.findByExpressionContaining("");

        assertEquals(2, found.size());
    }

    @Test
    void testMultipleUsersWithSameExpression() {
        UserEntity user1 = new UserEntity("user1", "pass1");
        UserEntity user2 = new UserEntity("user2", "pass2");
        Long userId1 = userDao.create(user1);
        Long userId2 = userDao.create(user2);

        Long id1 = compositeFunctionDao.create(new CompositeFunctionEntity("f(x)", userId1));
        Long id2 = compositeFunctionDao.create(new CompositeFunctionEntity("f(x)", userId2));

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);

        List<CompositeFunctionEntity> user1Funcs = compositeFunctionDao.findByUserId(userId1);
        List<CompositeFunctionEntity> user2Funcs = compositeFunctionDao.findByUserId(userId2);

        assertEquals(1, user1Funcs.size());
        assertEquals(1, user2Funcs.size());
        assertEquals("f(x)", user1Funcs.get(0).getExpression());
        assertEquals("f(x)", user2Funcs.get(0).getExpression());
    }

    @Test
    void testSqlInjectionProtection() {
        String maliciousPattern = "'; DROP TABLE composite_function; --";
        compositeFunctionDao.create(new CompositeFunctionEntity("safe_function(x)", testUserId));

        List<CompositeFunctionEntity> found = compositeFunctionDao.findByExpressionContaining(maliciousPattern);

        assertEquals(0, found.size());

        List<CompositeFunctionEntity> all = compositeFunctionDao.findAll();
        assertEquals(1, all.size());
    }
}
