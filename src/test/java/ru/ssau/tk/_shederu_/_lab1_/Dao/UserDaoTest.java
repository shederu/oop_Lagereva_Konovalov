package ru.ssau.tk._shederu_._lab1_.Dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao.deleteAll();
    }

    @Test
    @Order(1)
    void testInsertUser() {
        UserEntity user = new UserEntity("john_doe", "password123");
        UserEntity saved = userDao.save(user);

        assertNotNull(saved.getId());
        assertEquals("john_doe", saved.getLogin());
        assertEquals("password123", saved.getPassword());
    }

    @Test
    @Order(2)
    void testInsertMultipleUsers() {
        userDao.save(new UserEntity("alice", "pass1"));
        userDao.save(new UserEntity("bob", "pass2"));
        userDao.save(new UserEntity("charlie", "pass3"));

        assertEquals(3, userDao.count());
    }

    @Test
    @Order(3)
    void testInsertDiverseUsers() {
        userDao.save(new UserEntity("admin", "admin_secure_pass_2024"));
        userDao.save(new UserEntity("user_1", "simple"));
        userDao.save(new UserEntity("test", "test123!@#"));
        userDao.save(new UserEntity("guest", "guest_password"));
        userDao.save(new UserEntity("developer", "dev_pass_xyz"));

        assertEquals(5, userDao.count());
    }

    @Test
    @Order(4)
    void testSelectById() {
        UserEntity user = userDao.save(new UserEntity("test_user", "testpass"));
        Optional<UserEntity> found = userDao.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("test_user", found.get().getLogin());
    }

    @Test
    @Order(5)
    void testSelectByLogin() {
        userDao.save(new UserEntity("search_user", "pass"));
        Optional<UserEntity> found = userDao.findByLogin("search_user");

        assertTrue(found.isPresent());
        assertEquals("search_user", found.get().getLogin());
    }

    @Test
    @Order(6)
    void testSelectNotFound() {
        Optional<UserEntity> found = userDao.findByLogin("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    @Order(7)
    void testSelectAll() {
        userDao.save(new UserEntity("user1", "pass1"));
        userDao.save(new UserEntity("user2", "pass2"));
        userDao.save(new UserEntity("user3", "pass3"));

        List<UserEntity> users = userDao.findAll();
        assertEquals(3, users.size());
    }

    @Test
    @Order(8)
    void testExistsByLogin() {
        userDao.save(new UserEntity("existing", "pass"));

        assertTrue(userDao.existsByLogin("existing"));
        assertFalse(userDao.existsByLogin("nonexistent"));
    }

    @Test
    @Order(9)
    void testUpdatePassword() {
        UserEntity user = userDao.save(new UserEntity("update_user", "oldpass"));
        user.setPassword("newpass");
        userDao.save(user);

        Optional<UserEntity> updated = userDao.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("newpass", updated.get().getPassword());
    }

    @Test
    @Order(10)
    void testUpdateLogin() {
        UserEntity user = userDao.save(new UserEntity("oldlogin", "pass"));
        user.setLogin("newlogin");
        userDao.save(user);

        assertTrue(userDao.existsByLogin("newlogin"));
        assertFalse(userDao.existsByLogin("oldlogin"));
    }

    @Test
    @Order(11)
    void testDeleteById() {
        UserEntity user = userDao.save(new UserEntity("delete_me", "pass"));
        Long id = user.getId();

        userDao.deleteById(id);

        assertFalse(userDao.findById(id).isPresent());
    }

    @Test
    @Order(12)
    void testDeleteByLogin() {
        userDao.save(new UserEntity("delete_by_login", "pass"));

        userDao.deleteByLogin("delete_by_login");


        assertFalse(userDao.existsByLogin("delete_by_login"));
    }
}
