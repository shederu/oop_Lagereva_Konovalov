package ru.ssau.tk._shederu_._lab1_.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TabulatedFunctionDaoTest {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static DataSourceProvider dataSourceProvider;
    private TabulatedFunctionDao tabulatedFunctionDao;
    private UserDao userDao;
    private Long testUserId;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        dataSourceProvider = new DataSourceProvider(URL, USERNAME, PASSWORD);
        createTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        tabulatedFunctionDao = new TabulatedFunctionDao(dataSourceProvider);
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
    void testInsertOne() {
        TabulatedFunctionEntity entity = new TabulatedFunctionEntity(
                "func1",
                generateRandomBytes(50),
                generateRandomBytes(50),
                testUserId
        );

        Long id = tabulatedFunctionDao.create(entity);

        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    void testInsertMultiple() {
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("func2", generateRandomBytes(50), generateRandomBytes(50), testUserId));
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("func3", generateRandomBytes(50), generateRandomBytes(50), testUserId));
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("func4", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        List<TabulatedFunctionEntity> all = tabulatedFunctionDao.findAll();
        assertEquals(3, all.size());
    }

    @Test
    void testInsertDifferentSizes() {
        Long id1 = tabulatedFunctionDao.create(new TabulatedFunctionEntity("small", generateRandomBytes(10), generateRandomBytes(10), testUserId));
        Long id2 = tabulatedFunctionDao.create(new TabulatedFunctionEntity("medium", generateRandomBytes(100), generateRandomBytes(100), testUserId));
        Long id3 = tabulatedFunctionDao.create(new TabulatedFunctionEntity("large", generateRandomBytes(500), generateRandomBytes(500), testUserId));

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);

        Optional<TabulatedFunctionEntity> small = tabulatedFunctionDao.findById(id1);
        Optional<TabulatedFunctionEntity> medium = tabulatedFunctionDao.findById(id2);
        Optional<TabulatedFunctionEntity> large = tabulatedFunctionDao.findById(id3);

        assertTrue(small.isPresent());
        assertEquals(10, small.get().getData().length);

        assertTrue(medium.isPresent());
        assertEquals(100, medium.get().getData().length);

        assertTrue(large.isPresent());
        assertEquals(500, large.get().getData().length);
    }


    @Test
    void testFindById() {
        byte[] data = generateRandomBytes(50);
        byte[] derivative = generateRandomBytes(50);
        TabulatedFunctionEntity entity = new TabulatedFunctionEntity("search_id", data, derivative, testUserId);
        Long id = tabulatedFunctionDao.create(entity);

        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(id);

        assertTrue(found.isPresent());
        assertEquals("search_id", found.get().getName());
        assertEquals(testUserId, found.get().getUserId());
        assertArrayEquals(data, found.get().getData());
        assertArrayEquals(derivative, found.get().getDerivative());
    }

    @Test
    void testFindByName() {
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("search_name", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findByName("search_name");

        assertTrue(found.isPresent());
        assertEquals("search_name", found.get().getName());
    }

    @Test
    void testFindByNameNotFound() {
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findByName("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUserId() {
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("f1", generateRandomBytes(50), generateRandomBytes(50), testUserId));
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("f2", generateRandomBytes(50), generateRandomBytes(50), testUserId));
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("f3", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        List<TabulatedFunctionEntity> found = tabulatedFunctionDao.findByUserId(testUserId);

        assertEquals(3, found.size());
        assertTrue(found.stream().allMatch(f -> f.getUserId().equals(testUserId)));
    }

    @Test
    void testFindByNameAndUserId() {
        UserEntity anotherUser = new UserEntity("another_user", "pass");
        Long anotherUserId = userDao.create(anotherUser);

        byte[] data1 = generateRandomBytes(50);
        byte[] data2 = generateRandomBytes(75);

        tabulatedFunctionDao.create(new TabulatedFunctionEntity("shared_name", data1, generateRandomBytes(50), testUserId));
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("shared_name", data2, generateRandomBytes(50), anotherUserId));

        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findByNameAndUserId("shared_name", testUserId);

        assertTrue(found.isPresent());
        assertEquals(testUserId, found.get().getUserId());
        assertArrayEquals(data1, found.get().getData());
    }

    @Test
    void testFindAll() {
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("a1", generateRandomBytes(50), generateRandomBytes(50), testUserId));
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("a2", generateRandomBytes(50), generateRandomBytes(50), testUserId));
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("a3", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        List<TabulatedFunctionEntity> all = tabulatedFunctionDao.findAll();

        assertEquals(3, all.size());
    }

    @Test
    void testUpdateName() {
        Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity("oldname", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        boolean updated = tabulatedFunctionDao.updateName(id, "newname");

        assertTrue(updated);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals("newname", found.get().getName());
    }

    @Test
    void testUpdateData() {
        Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity("update1", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        byte[] newData = generateRandomBytes(100);
        boolean updated = tabulatedFunctionDao.updateData(id, newData);

        assertTrue(updated);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertArrayEquals(newData, found.get().getData());
    }

    @Test
    void testUpdateDerivative() {
        Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity("update2", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        byte[] newDerivative = generateRandomBytes(75);
        boolean updated = tabulatedFunctionDao.updateDerivative(id, newDerivative);

        assertTrue(updated);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertArrayEquals(newDerivative, found.get().getDerivative());
    }

    @Test
    void testUpdateUserId() {
        UserEntity newUser = new UserEntity("new_user", "pass");
        Long newUserId = userDao.create(newUser);

        Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity("func", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        boolean updated = tabulatedFunctionDao.updateUserId(id, newUserId);

        assertTrue(updated);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(newUserId, found.get().getUserId());
    }

    @Test
    void testUpdateNonExistent() {
        boolean updated = tabulatedFunctionDao.updateName(999L, "newname");

        assertFalse(updated);
    }


    @Test
    void testDeleteById() {
        Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity("del1", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        boolean deleted = tabulatedFunctionDao.deleteById(id);

        assertTrue(deleted);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByName() {
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("del2", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        boolean deleted = tabulatedFunctionDao.deleteByName("del2");

        assertTrue(deleted);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findByName("del2");
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteNonExistent() {
        boolean deleted = tabulatedFunctionDao.deleteById(999L);

        assertFalse(deleted);
    }

    @Test
    void testCascadeDelete() {
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("func1", generateRandomBytes(50), generateRandomBytes(50), testUserId));
        tabulatedFunctionDao.create(new TabulatedFunctionEntity("func2", generateRandomBytes(50), generateRandomBytes(50), testUserId));

        userDao.deleteById(testUserId);

        List<TabulatedFunctionEntity> functions = tabulatedFunctionDao.findByUserId(testUserId);
        assertEquals(0, functions.size());
    }


    @Test
    void testEmptyByteArrays() {
        byte[] emptyData = new byte[0];
        byte[] emptyDerivative = new byte[0];

        Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity("empty", emptyData, emptyDerivative, testUserId));

        assertNotNull(id);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(0, found.get().getData().length);
        assertEquals(0, found.get().getDerivative().length);
    }

    @Test
    void testLargeByteArrays() {
        byte[] largeData = generateRandomBytes(10240);
        byte[] largeDerivative = generateRandomBytes(10240);

        Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity("large", largeData, largeDerivative, testUserId));

        assertNotNull(id);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(10240, found.get().getData().length);
        assertEquals(10240, found.get().getDerivative().length);
    }

    @Test
    void testSpecialCharactersInName() {
        String specialName = "func_with_спецсимволы_特殊字符_123!@#";

        Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity(specialName, generateRandomBytes(50), generateRandomBytes(50), testUserId));

        assertNotNull(id);
        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findByName(specialName);
        assertTrue(found.isPresent());
        assertEquals(specialName, found.get().getName());
    }
}
