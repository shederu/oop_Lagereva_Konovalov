/*package ru.ssau.tk._shederu_._lab1_.service;

import org.junit.jupiter.api.*;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static DataSourceProvider dataSourceProvider;
    private UserService userService;
    private UserDao userDao;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        dataSourceProvider = new DataSourceProvider(URL, USERNAME, PASSWORD);
        createTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        userDao = new UserDao(dataSourceProvider);
        userService = new UserService(userDao);
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
    void testCreateUser() {
        UserDto dto = userService.createUser("john_doe", "password123");

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals("john_doe", dto.getLogin());
    }

    @Test
    void testGetUserById() {
        UserDto created = userService.createUser("test_user", "pass");

        Optional<UserDto> found = userService.getUserById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("test_user", found.get().getLogin());
    }

    @Test
    void testGetUserByLogin() {
        userService.createUser("search_user", "pass");

        Optional<UserDto> found = userService.getUserByLogin("search_user");

        assertTrue(found.isPresent());
        assertEquals("search_user", found.get().getLogin());
    }

    @Test
    void testGetAllUsers() {
        userService.createUser("user1", "pass1");
        userService.createUser("user2", "pass2");
        userService.createUser("user3", "pass3");

        List<UserDto> users = userService.getAllUsers();

        assertEquals(3, users.size());
    }

    @Test
    void testDeleteUser() {
        UserDto created = userService.createUser("delete_me", "pass");
        Long id = created.getId();

        boolean deleted = userService.deleteUser(id);

        assertTrue(deleted);
        assertFalse(userService.getUserById(id).isPresent());
    }

    @Test
    void testGetNonExistentUser() {
        Optional<UserDto> found = userService.getUserById(999L);
        assertFalse(found.isPresent());
    }
}
*/