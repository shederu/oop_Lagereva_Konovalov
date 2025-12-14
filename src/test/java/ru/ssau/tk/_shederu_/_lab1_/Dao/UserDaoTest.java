/*package ru.ssau.tk._shederu_._lab1_.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static DataSourceProvider dataSourceProvider;
    private UserDao userDao;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        dataSourceProvider = new DataSourceProvider(URL, USERNAME, PASSWORD);
        createTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        userDao = new UserDao(dataSourceProvider);
        clearTables();
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
        }
    }

    private void clearTables() throws SQLException {
        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM \"user\"");
        }
    }


    @Test
    void testInsertUser() {
        UserEntity user = new UserEntity("john_doe", "password123");
        Long id = userDao.create(user);

        assertNotNull(id);
        assertTrue(id > 0);

        Optional<UserEntity> found = userDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals("john_doe", found.get().getLogin());
        assertEquals("password123", found.get().getPassword());
    }

    @Test
    void testInsertMultipleUsers() {
        userDao.create(new UserEntity("alice", "pass1"));
        userDao.create(new UserEntity("bob", "pass2"));
        userDao.create(new UserEntity("charlie", "pass3"));

        assertEquals(3, userDao.count());
    }

    @Test
    void testInsertDiverseUsers() {
        userDao.create(new UserEntity("admin", "admin_secure_pass_2024"));
        userDao.create(new UserEntity("user_1", "simple"));
        userDao.create(new UserEntity("test", "test123!@#"));
        userDao.create(new UserEntity("guest", "guest_password"));
        userDao.create(new UserEntity("developer", "dev_pass_xyz"));

        assertEquals(5, userDao.count());
    }

    @Test
    void testSaveNewUser() {
        UserEntity user = new UserEntity("save_user", "savepass");
        UserEntity saved = userDao.save(user);

        assertNotNull(saved.getId());
        assertEquals("save_user", saved.getLogin());
    }

    @Test
    void testSelectById() {
        Long id = userDao.create(new UserEntity("test_user", "testpass"));

        Optional<UserEntity> found = userDao.findById(id);

        assertTrue(found.isPresent());
        assertEquals("test_user", found.get().getLogin());
        assertEquals("testpass", found.get().getPassword());
    }

    @Test
    void testSelectByLogin() {
        userDao.create(new UserEntity("search_user", "pass"));

        Optional<UserEntity> found = userDao.findByLogin("search_user");

        assertTrue(found.isPresent());
        assertEquals("search_user", found.get().getLogin());
    }

    @Test
    void testSelectNotFound() {
        Optional<UserEntity> foundById = userDao.findById(999L);
        Optional<UserEntity> foundByLogin = userDao.findByLogin("nonexistent");

        assertFalse(foundById.isPresent());
        assertFalse(foundByLogin.isPresent());
    }

    @Test
    void testSelectAll() {
        userDao.create(new UserEntity("user1", "pass1"));
        userDao.create(new UserEntity("user2", "pass2"));
        userDao.create(new UserEntity("user3", "pass3"));

        List<UserEntity> users = userDao.findAll();

        assertEquals(3, users.size());
    }

    @Test
    void testExistsByLogin() {
        userDao.create(new UserEntity("existing", "pass"));

        assertTrue(userDao.existsByLogin("existing"));
        assertFalse(userDao.existsByLogin("nonexistent"));
    }

    @Test
    void testCount() {
        assertEquals(0, userDao.count());

        userDao.create(new UserEntity("user1", "pass1"));
        userDao.create(new UserEntity("user2", "pass2"));

        assertEquals(2, userDao.count());
    }

    @Test
    void testUpdatePassword() {
        Long id = userDao.create(new UserEntity("update_user", "oldpass"));

        boolean updated = userDao.updatePassword(id, "newpass");

        assertTrue(updated);
        Optional<UserEntity> found = userDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals("newpass", found.get().getPassword());
    }

    @Test
    void testUpdateLogin() {
        Long id = userDao.create(new UserEntity("oldlogin", "pass"));

        boolean updated = userDao.updateLogin(id, "newlogin");

        assertTrue(updated);
        Optional<UserEntity> found = userDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals("newlogin", found.get().getLogin());

        assertTrue(userDao.existsByLogin("newlogin"));
        assertFalse(userDao.existsByLogin("oldlogin"));
    }

    @Test
    void testUpdateNonExistent() {
        boolean updatedPassword = userDao.updatePassword(999L, "newpass");
        boolean updatedLogin = userDao.updateLogin(999L, "newlogin");

        assertFalse(updatedPassword);
        assertFalse(updatedLogin);
    }


    @Test
    void testDeleteById() {
        Long id = userDao.create(new UserEntity("delete_me", "pass"));

        boolean deleted = userDao.deleteById(id);

        assertTrue(deleted);
        assertFalse(userDao.findById(id).isPresent());
    }

    @Test
    void testDeleteByLogin() {
        userDao.create(new UserEntity("delete_by_login", "pass"));

        boolean deleted = userDao.deleteByLogin("delete_by_login");

        assertTrue(deleted);
        assertFalse(userDao.existsByLogin("delete_by_login"));
    }

    @Test
    void testDeleteNonExistent() {
        boolean deletedById = userDao.deleteById(999L);
        boolean deletedByLogin = userDao.deleteByLogin("nonexistent");

        assertFalse(deletedById);
        assertFalse(deletedByLogin);
    }

    @Test
    void testDeleteAll() {
        userDao.create(new UserEntity("user1", "pass1"));
        userDao.create(new UserEntity("user2", "pass2"));
        userDao.create(new UserEntity("user3", "pass3"));

        assertEquals(3, userDao.count());

        userDao.deleteAll();

        assertEquals(0, userDao.count());
    }

    @Test
    void testLongLogin() {
        String longLogin = "a".repeat(50);
        Long id = userDao.create(new UserEntity(longLogin, "pass"));

        assertNotNull(id);
        Optional<UserEntity> found = userDao.findByLogin(longLogin);
        assertTrue(found.isPresent());
        assertEquals(longLogin, found.get().getLogin());
    }

    @Test
    void testLongPassword() {
        String longPassword = "p".repeat(255);
        Long id = userDao.create(new UserEntity("user_long_pass", longPassword));

        assertNotNull(id);
        Optional<UserEntity> found = userDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(longPassword, found.get().getPassword());
    }

    @Test
    void testSpecialCharactersInLogin() {
        String specialLogin = "user_123-@.";
        Long id = userDao.create(new UserEntity(specialLogin, "pass"));

        assertNotNull(id);
        assertTrue(userDao.existsByLogin(specialLogin));
    }

    @Test
    void testSpecialCharactersInPassword() {
        String specialPassword = "p@$$w0rd!#%^&*()_+-={}[]|:;<>?,./";
        Long id = userDao.create(new UserEntity("special_user", specialPassword));

        assertNotNull(id);
        Optional<UserEntity> found = userDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(specialPassword, found.get().getPassword());
    }

    @Test
    void testDuplicateLogin() {
        userDao.create(new UserEntity("duplicate", "pass1"));

        Long secondId = userDao.create(new UserEntity("duplicate", "pass2"));

        assertNull(secondId);
    }
}*/
