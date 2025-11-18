package ru.ssau.tk._shederu_._lab1_.Dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TabulatedFunctionDaoTest {

    @Autowired
    private TabulatedFunctionDao tabulatedFunctionDao;

    private byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        return bytes;
    }

    @BeforeEach
    void setUp() {
        tabulatedFunctionDao.deleteAll();
    }

    // ==================== INSERT ====================

    @Test
    @Order(1)
    @DisplayName("INSERT: Добавление одной функции")
    void testInsertOne() {
        TabulatedFunctionEntity entity = new TabulatedFunctionEntity("func1", generateRandomBytes(50), generateRandomBytes(50));
        TabulatedFunctionEntity saved = tabulatedFunctionDao.save(entity);

        assertNotNull(saved.getId());
        assertEquals("func1", saved.getName());
    }

    @Test
    @Order(2)
    @DisplayName("INSERT: Добавление трёх функций")
    void testInsertMultiple() {
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("func2", generateRandomBytes(50), generateRandomBytes(50)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("func3", generateRandomBytes(50), generateRandomBytes(50)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("func4", generateRandomBytes(50), generateRandomBytes(50)));

        assertEquals(3, tabulatedFunctionDao.count());
    }

    @Test
    @Order(3)
    @DisplayName("INSERT: Разные размеры данных")
    void testInsertDifferentSizes() {
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("small", generateRandomBytes(10), generateRandomBytes(10)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("medium", generateRandomBytes(100), generateRandomBytes(100)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("large", generateRandomBytes(500), generateRandomBytes(500)));

        assertEquals(3, tabulatedFunctionDao.count());
    }

    // ==================== SELECT ====================

    @Test
    @Order(4)
    @DisplayName("SELECT: Поиск по ID")
    void testFindById() {
        TabulatedFunctionEntity saved = tabulatedFunctionDao.save(
                new TabulatedFunctionEntity("search_id", generateRandomBytes(50), generateRandomBytes(50))
        );

        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("search_id", found.get().getName());
    }

    @Test
    @Order(5)
    @DisplayName("SELECT: Поиск по имени")
    void testFindByName() {
        tabulatedFunctionDao.save(
                new TabulatedFunctionEntity("search_name", generateRandomBytes(50), generateRandomBytes(50))
        );

        Optional<TabulatedFunctionEntity> found = tabulatedFunctionDao.findByName("search_name");

        assertTrue(found.isPresent());
        assertEquals("search_name", found.get().getName());
    }

    @Test
    @Order(6)
    @DisplayName("SELECT: Поиск по части имени")
    void testFindByNameContaining() {
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("sin_table", generateRandomBytes(50), generateRandomBytes(50)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("cos_table", generateRandomBytes(50), generateRandomBytes(50)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("exp_func", generateRandomBytes(50), generateRandomBytes(50)));

        List<TabulatedFunctionEntity> found = tabulatedFunctionDao.findByNameContaining("table");

        assertEquals(2, found.size());
    }


    @Test
    @Order(7)
    @DisplayName("SELECT: Проверка существования")
    void testExistsByName() {
        tabulatedFunctionDao.save(
                new TabulatedFunctionEntity("exists", generateRandomBytes(50), generateRandomBytes(50))
        );

        assertTrue(tabulatedFunctionDao.existsByName("exists"));
        assertFalse(tabulatedFunctionDao.existsByName("not_exists"));
    }

    @Test
    @Order(8)
    @DisplayName("SELECT: Получение всех")
    void testFindAll() {
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("a1", generateRandomBytes(50), generateRandomBytes(50)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("a2", generateRandomBytes(50), generateRandomBytes(50)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("a3", generateRandomBytes(50), generateRandomBytes(50)));

        List<TabulatedFunctionEntity> all = tabulatedFunctionDao.findAll();

        assertEquals(3, all.size());
    }

    // ==================== UPDATE ====================

    @Test
    @Order(9)
    @DisplayName("UPDATE: Переименование")
    void testUpdateName() {
        TabulatedFunctionEntity saved = tabulatedFunctionDao.save(
                new TabulatedFunctionEntity("oldname", generateRandomBytes(50), generateRandomBytes(50))
        );

        saved.setName("newname");
        tabulatedFunctionDao.save(saved);

        assertTrue(tabulatedFunctionDao.existsByName("newname"));
        assertFalse(tabulatedFunctionDao.existsByName("oldname"));
    }

    @Test
    @Order(10)
    @DisplayName("UPDATE: Обновление данных")
    void testUpdateData() {
        TabulatedFunctionEntity saved = tabulatedFunctionDao.save(
                new TabulatedFunctionEntity("update1", generateRandomBytes(50), generateRandomBytes(50))
        );

        byte[] newData = generateRandomBytes(100);
        saved.setData(newData);
        tabulatedFunctionDao.save(saved);

        Optional<TabulatedFunctionEntity> updated = tabulatedFunctionDao.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals(100, updated.get().getData().length);
    }

    // ==================== DELETE ====================

    @Test
    @Order(11)
    @DisplayName("DELETE: Удаление по ID")
    void testDeleteById() {
        TabulatedFunctionEntity saved = tabulatedFunctionDao.save(
                new TabulatedFunctionEntity("del1", generateRandomBytes(50), generateRandomBytes(50))
        );
        Long id = saved.getId();

        tabulatedFunctionDao.deleteById(id);

        assertFalse(tabulatedFunctionDao.findById(id).isPresent());
    }

    @Test
    @Order(12)
    @DisplayName("DELETE: Удаление по имени")
    void testDeleteByName() {
        tabulatedFunctionDao.save(
                new TabulatedFunctionEntity("del2", generateRandomBytes(50), generateRandomBytes(50))
        );

        tabulatedFunctionDao.deleteByName("del2");

        assertFalse(tabulatedFunctionDao.existsByName("del2"));
    }

    @Test
    @Order(13)
    @DisplayName("DELETE: Удаление всех")
    void testDeleteAll() {
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("d1", generateRandomBytes(50), generateRandomBytes(50)));
        tabulatedFunctionDao.save(new TabulatedFunctionEntity("d2", generateRandomBytes(50), generateRandomBytes(50)));

        tabulatedFunctionDao.deleteAll();

        assertEquals(0, tabulatedFunctionDao.count());
    }
}
