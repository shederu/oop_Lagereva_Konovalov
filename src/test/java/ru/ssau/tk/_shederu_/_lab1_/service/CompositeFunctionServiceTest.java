/*package ru.ssau.tk._shederu_._lab1_.service;

import org.junit.jupiter.api.*;
import ru.ssau.tk._shederu_._lab1_.Dao.CompositeFunctionDao;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompositeFunctionServiceTest {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static DataSourceProvider dataSourceProvider;
    private CompositeFunctionService functionService;
    private UserDao userDao;
    private Long testUserId;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        dataSourceProvider = new DataSourceProvider(URL, USERNAME, PASSWORD);
        createTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        userDao = new UserDao(dataSourceProvider);
        CompositeFunctionDao functionDao = new CompositeFunctionDao(dataSourceProvider);
        functionService = new CompositeFunctionService(functionDao);

        clearTables();
        testUserId = userDao.create(new UserEntity("test_user", "pass"));
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
    void testCreateFunction() {
        CompositeFunctionDto dto = functionService.createFunction("f(g(x))", testUserId);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals("f(g(x))", dto.getExpression());
        assertEquals(testUserId, dto.getUserId());
    }

    @Test
    void testGetFunctionById() {
        CompositeFunctionDto created = functionService.createFunction("h(k(x))", testUserId);

        Optional<CompositeFunctionDto> found = functionService.getFunctionById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("h(k(x))", found.get().getExpression());
        assertEquals(testUserId, found.get().getUserId());
    }

    @Test
    void testGetFunctionByExpression() {
        functionService.createFunction("sin(cos(x))", testUserId);

        Optional<CompositeFunctionDto> found = functionService.getFunctionByExpression("sin(cos(x))");

        assertTrue(found.isPresent());
        assertEquals("sin(cos(x))", found.get().getExpression());
    }

    @Test
    void testGetFunctionsByPattern() {
        functionService.createFunction("sin(x)", testUserId);
        functionService.createFunction("sin(y)", testUserId);
        functionService.createFunction("cos(x)", testUserId);

        List<CompositeFunctionDto> functions = functionService.getFunctionsByPattern("sin");

        assertEquals(2, functions.size());
    }

    @Test
    void testGetFunctionsByUserId() {
        functionService.createFunction("f1(x)", testUserId);
        functionService.createFunction("f2(x)", testUserId);

        List<CompositeFunctionDto> functions = functionService.getFunctionsByUserId(testUserId);

        assertEquals(2, functions.size());
        assertTrue(functions.stream().allMatch(f -> f.getUserId().equals(testUserId)));
    }

    @Test
    void testGetAllFunctions() {
        functionService.createFunction("a(x)", testUserId);
        functionService.createFunction("b(x)", testUserId);

        List<CompositeFunctionDto> functions = functionService.getAllFunctions();

        assertEquals(2, functions.size());
    }

    @Test
    void testDeleteFunction() {
        CompositeFunctionDto created = functionService.createFunction("delete(x)", testUserId);

        boolean deleted = functionService.deleteFunction(created.getId());

        assertTrue(deleted);
        assertFalse(functionService.getFunctionById(created.getId()).isPresent());
    }

    @Test
    void testGetNonExistentFunction() {
        Optional<CompositeFunctionDto> found = functionService.getFunctionById(999L);
        assertFalse(found.isPresent());
    }
}*/
