/*package ru.ssau.tk._shederu_._lab1_.service;

import org.junit.jupiter.api.*;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.TabulatedFunctionDao;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TabulatedFunctionServiceTest {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static DataSourceProvider dataSourceProvider;
    private TabulatedFunctionService functionService;
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
        TabulatedFunctionDao functionDao = new TabulatedFunctionDao(dataSourceProvider);
        functionService = new TabulatedFunctionService(functionDao);

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
                    "CREATE TABLE IF NOT EXISTS tabulated_function (" +
                            "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                            "    name VARCHAR(100) NOT NULL," +
                            "    data BYTEA NOT NULL," +
                            "    derivative BYTEA NOT NULL," +
                            "    user_id BIGINT NOT NULL," +
                            "    FOREIGN KEY (user_id) REFERENCES \"user\"(id) ON DELETE CASCADE" +
                            ")"
            );
        }
    }

    private void clearTables() throws SQLException {
        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM tabulated_function");
            stmt.execute("DELETE FROM \"user\"");
        }
    }

    private byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        return bytes;
    }

    @Test
    void testCreateFunction() {
        TabulatedFunctionDto dto = functionService.createFunction(
                "sin(x)",
                generateRandomBytes(50),
                generateRandomBytes(50),
                testUserId
        );

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals("sin(x)", dto.getName());
        assertEquals(testUserId, dto.getUserId());
    }

    @Test
    void testGetFunctionById() {
        TabulatedFunctionDto created = functionService.createFunction(
                "cos(x)",
                generateRandomBytes(50),
                generateRandomBytes(50),
                testUserId
        );

        Optional<TabulatedFunctionDto> found = functionService.getFunctionById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("cos(x)", found.get().getName());
        assertEquals(testUserId, found.get().getUserId());
    }

    @Test
    void testGetFunctionByName() {
        functionService.createFunction(
                "tan(x)",
                generateRandomBytes(50),
                generateRandomBytes(50),
                testUserId
        );

        Optional<TabulatedFunctionDto> found = functionService.getFunctionByName("tan(x)");

        assertTrue(found.isPresent());
        assertEquals("tan(x)", found.get().getName());
    }

    @Test
    void testGetFunctionsByUserId() {
        functionService.createFunction("f1", generateRandomBytes(50), generateRandomBytes(50), testUserId);
        functionService.createFunction("f2", generateRandomBytes(50), generateRandomBytes(50), testUserId);
        functionService.createFunction("f3", generateRandomBytes(50), generateRandomBytes(50), testUserId);

        List<TabulatedFunctionDto> functions = functionService.getFunctionsByUserId(testUserId);

        assertEquals(3, functions.size());
        assertTrue(functions.stream().allMatch(f -> f.getUserId().equals(testUserId)));
    }

    @Test
    void testGetAllFunctions() {
        functionService.createFunction("a1", generateRandomBytes(50), generateRandomBytes(50), testUserId);
        functionService.createFunction("a2", generateRandomBytes(50), generateRandomBytes(50), testUserId);

        List<TabulatedFunctionDto> functions = functionService.getAllFunctions();

        assertEquals(2, functions.size());
    }

    @Test
    void testDeleteFunction() {
        TabulatedFunctionDto created = functionService.createFunction(
                "delete_me",
                generateRandomBytes(50),
                generateRandomBytes(50),
                testUserId
        );

        boolean deleted = functionService.deleteFunction(created.getId());

        assertTrue(deleted);
        assertFalse(functionService.getFunctionById(created.getId()).isPresent());
    }

    @Test
    void testGetNonExistentFunction() {
        Optional<TabulatedFunctionDto> found = functionService.getFunctionById(999L);
        assertFalse(found.isPresent());
    }
}
*/