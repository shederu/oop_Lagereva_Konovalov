package ru.ssau.tk._shederu_._lab1_.performance;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.Dao.*;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ManualPerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(ManualPerformanceTest.class);
    private static final int USERS_COUNT = 100;
    private static final int FUNCTIONS_PER_USER = 100;
    private static final int TOTAL_RECORDS = USERS_COUNT * FUNCTIONS_PER_USER;

    private static final String URL = "jdbc:h2:mem:manualdb;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static DataSourceProvider dataSourceProvider;
    private UserDao userDao;
    private TabulatedFunctionDao tabulatedFunctionDao;
    private CompositeFunctionDao compositeFunctionDao;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        logger.info("=== MANUAL (JDBC DAO) PERFORMANCE TEST ===");
        logger.info("Users: {}", USERS_COUNT);
        logger.info("Functions per user: {}", FUNCTIONS_PER_USER);
        logger.info("Total records: {}", TOTAL_RECORDS);

        dataSourceProvider = new DataSourceProvider(URL, USERNAME, PASSWORD);
        createTables();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDao(dataSourceProvider);
        tabulatedFunctionDao = new TabulatedFunctionDao(dataSourceProvider);
        compositeFunctionDao = new CompositeFunctionDao(dataSourceProvider);
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

    @Test
    @Order(1)
    @DisplayName("1. Generate Test Data")
    void testDataGeneration() {
        logger.info("\n=== DATA GENERATION ===");

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= USERS_COUNT; i++) {
            UserEntity user = new UserEntity("user_" + i, "password_" + i);
            Long userId = userDao.create(user);

            for (int j = 1; j <= FUNCTIONS_PER_USER; j++) {
                TabulatedFunctionEntity tabFunc = new TabulatedFunctionEntity(
                        "tab_func_" + i + "_" + j,
                        generateRandomBytes(50),
                        generateRandomBytes(50),
                        userId
                );
                tabulatedFunctionDao.create(tabFunc);

                CompositeFunctionEntity compFunc = new CompositeFunctionEntity(
                        "comp_func_" + i + "_" + j + "(x)",
                        userId
                );
                compositeFunctionDao.create(compFunc);
            }

            if (i % 10 == 0) {
                logger.info("Created users: {} / {}", i, USERS_COUNT);
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Data generation time: {} ms", duration);
        logger.info("Users in DB: {}", userDao.findAll().size());
        logger.info("Tabulated functions: {}", tabulatedFunctionDao.findAll().size());
        logger.info("Composite functions: {}", compositeFunctionDao.findAll().size());
    }

    @Test
    @Order(2)
    @DisplayName("2. USER TABLE - findAll")
    void testUserFindAll() {
        logger.info("\n=== USER: findAll ===");

        long startTime = System.currentTimeMillis();
        List<UserEntity> users = userDao.findAll();
        long endTime = System.currentTimeMillis();

        logger.info("Time: {} ms | Records: {}", endTime - startTime, users.size());
        assertEquals(USERS_COUNT, users.size());
    }

    @Test
    @Order(3)
    @DisplayName("3. USER TABLE - findById (100 queries)")
    void testUserFindById() {
        logger.info("\n=== USER: findById ===");

        List<Long> ids = getRandomUserIds(100);

        long startTime = System.currentTimeMillis();
        for (Long id : ids) {
            userDao.findById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, ids.size(), duration / (double) ids.size());
    }

    @Test
    @Order(4)
    @DisplayName("4. USER TABLE - findByLogin (100 queries)")
    void testUserFindByLogin() {
        logger.info("\n=== USER: findByLogin ===");

        List<String> logins = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            logins.add("user_" + new Random().nextInt(USERS_COUNT) + 1);
        }

        long startTime = System.currentTimeMillis();
        for (String login : logins) {
            userDao.findByLogin(login);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, logins.size(), duration / (double) logins.size());
    }

    @Test
    @Order(5)
    @DisplayName("5. USER TABLE - create (100 inserts)")
    void testUserCreate() {
        logger.info("\n=== USER: create ===");

        int insertCount = 100;

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= insertCount; i++) {
            userDao.create(new UserEntity("new_user_" + i, "password"));
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Inserts: {} | Avg: {} ms/insert",
                duration, insertCount, duration / (double) insertCount);
    }

    @Test
    @Order(6)
    @DisplayName("6. USER TABLE - updatePassword (50 updates)")
    void testUserUpdate() {
        logger.info("\n=== USER: updatePassword ===");

        List<Long> ids = getRandomUserIds(50);

        long startTime = System.currentTimeMillis();
        for (Long id : ids) {
            userDao.updatePassword(id, "updated_password");
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Updates: {} | Avg: {} ms/update",
                duration, ids.size(), duration / (double) ids.size());
    }

    @Test
    @Order(7)
    @DisplayName("7. USER TABLE - deleteById (50 deletes)")
    void testUserDelete() {
        logger.info("\n=== USER: deleteById ===");

        List<Long> idsToDelete = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Long id = userDao.create(new UserEntity("delete_user_" + i, "pass"));
            idsToDelete.add(id);
        }

        long startTime = System.currentTimeMillis();
        for (Long id : idsToDelete) {
            userDao.deleteById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Deletes: {} | Avg: {} ms/delete",
                duration, idsToDelete.size(), duration / (double) idsToDelete.size());
    }

    @Test
    @Order(8)
    @DisplayName("8. TABULATED_FUNCTION - findAll")
    void testTabulatedFunctionFindAll() {
        logger.info("\n=== TABULATED_FUNCTION: findAll ===");

        long startTime = System.currentTimeMillis();
        List<TabulatedFunctionEntity> functions = tabulatedFunctionDao.findAll();
        long endTime = System.currentTimeMillis();

        logger.info("Time: {} ms | Records: {}", endTime - startTime, functions.size());
        assertEquals(TOTAL_RECORDS, functions.size());
    }

    @Test
    @Order(9)
    @DisplayName("9. TABULATED_FUNCTION - findById (100 queries)")
    void testTabulatedFunctionFindById() {
        logger.info("\n=== TABULATED_FUNCTION: findById ===");

        List<Long> ids = getRandomFunctionIds(100);

        long startTime = System.currentTimeMillis();
        for (Long id : ids) {
            tabulatedFunctionDao.findById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, ids.size(), duration / (double) ids.size());
    }

    @Test
    @Order(10)
    @DisplayName("10. TABULATED_FUNCTION - findByName (100 queries)")
    void testTabulatedFunctionFindByName() {
        logger.info("\n=== TABULATED_FUNCTION: findByName ===");

        List<String> names = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 100; i++) {
            int userId = random.nextInt(USERS_COUNT) + 1;
            int funcNum = random.nextInt(FUNCTIONS_PER_USER) + 1;
            names.add("tab_func_" + userId + "_" + funcNum);
        }

        long startTime = System.currentTimeMillis();
        for (String name : names) {
            tabulatedFunctionDao.findByName(name);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, names.size(), duration / (double) names.size());
    }

    @Test
    @Order(11)
    @DisplayName("11. TABULATED_FUNCTION - findByUserId (50 queries)")
    void testTabulatedFunctionFindByUserId() {
        logger.info("\n=== TABULATED_FUNCTION: findByUserId ===");

        List<Long> userIds = getRandomUserIds(50);

        long startTime = System.currentTimeMillis();
        int totalRecords = 0;
        for (Long userId : userIds) {
            List<TabulatedFunctionEntity> functions = tabulatedFunctionDao.findByUserId(userId);
            totalRecords += functions.size();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Total records: {} | Avg: {} ms/query",
                duration, userIds.size(), totalRecords, duration / (double) userIds.size());
    }

    @Test
    @Order(12)
    @DisplayName("12. TABULATED_FUNCTION - create (100 inserts)")
    void testTabulatedFunctionCreate() {
        logger.info("\n=== TABULATED_FUNCTION: create ===");

        Long userId = 1L;
        int insertCount = 100;

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= insertCount; i++) {
            tabulatedFunctionDao.create(new TabulatedFunctionEntity(
                    "new_tab_func_" + i,
                    generateRandomBytes(50),
                    generateRandomBytes(50),
                    userId
            ));
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Inserts: {} | Avg: {} ms/insert",
                duration, insertCount, duration / (double) insertCount);
    }

    @Test
    @Order(13)
    @DisplayName("13. TABULATED_FUNCTION - deleteById (50 deletes)")
    void testTabulatedFunctionDelete() {
        logger.info("\n=== TABULATED_FUNCTION: deleteById ===");

        Long userId = 1L;
        List<Long> idsToDelete = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Long id = tabulatedFunctionDao.create(new TabulatedFunctionEntity(
                    "delete_tab_func_" + i,
                    generateRandomBytes(50),
                    generateRandomBytes(50),
                    userId
            ));
            idsToDelete.add(id);
        }

        long startTime = System.currentTimeMillis();
        for (Long id : idsToDelete) {
            tabulatedFunctionDao.deleteById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Deletes: {} | Avg: {} ms/delete",
                duration, idsToDelete.size(), duration / (double) idsToDelete.size());
    }

    @Test
    @Order(14)
    @DisplayName("14. COMPOSITE_FUNCTION - findAll")
    void testCompositeFunctionFindAll() {
        logger.info("\n=== COMPOSITE_FUNCTION: findAll ===");

        long startTime = System.currentTimeMillis();
        List<CompositeFunctionEntity> functions = compositeFunctionDao.findAll();
        long endTime = System.currentTimeMillis();

        logger.info("Time: {} ms | Records: {}", endTime - startTime, functions.size());
        assertEquals(TOTAL_RECORDS, functions.size());
    }

    @Test
    @Order(15)
    @DisplayName("15. COMPOSITE_FUNCTION - findById (100 queries)")
    void testCompositeFunctionFindById() {
        logger.info("\n=== COMPOSITE_FUNCTION: findById ===");

        List<Long> ids = getRandomFunctionIds(100);

        long startTime = System.currentTimeMillis();
        for (Long id : ids) {
            compositeFunctionDao.findById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, ids.size(), duration / (double) ids.size());
    }

    @Test
    @Order(16)
    @DisplayName("16. COMPOSITE_FUNCTION - findByExpression (100 queries)")
    void testCompositeFunctionFindByExpression() {
        logger.info("\n=== COMPOSITE_FUNCTION: findByExpression ===");

        List<String> expressions = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 100; i++) {
            int userId = random.nextInt(USERS_COUNT) + 1;
            int funcNum = random.nextInt(FUNCTIONS_PER_USER) + 1;
            expressions.add("comp_func_" + userId + "_" + funcNum + "(x)");
        }

        long startTime = System.currentTimeMillis();
        for (String expression : expressions) {
            compositeFunctionDao.findByExpression(expression);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, expressions.size(), duration / (double) expressions.size());
    }

    @Test
    @Order(17)
    @DisplayName("17. COMPOSITE_FUNCTION - findByUserId (50 queries)")
    void testCompositeFunctionFindByUserId() {
        logger.info("\n=== COMPOSITE_FUNCTION: findByUserId ===");

        List<Long> userIds = getRandomUserIds(50);

        long startTime = System.currentTimeMillis();
        int totalRecords = 0;
        for (Long userId : userIds) {
            List<CompositeFunctionEntity> functions = compositeFunctionDao.findByUserId(userId);
            totalRecords += functions.size();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Total records: {} | Avg: {} ms/query",
                duration, userIds.size(), totalRecords, duration / (double) userIds.size());
    }

    @Test
    @Order(18)
    @DisplayName("18. COMPOSITE_FUNCTION - findByExpressionContaining (50 queries)")
    void testCompositeFunctionFindByPattern() {
        logger.info("\n=== COMPOSITE_FUNCTION: findByExpressionContaining ===");

        String[] patterns = {"func_1_", "func_2_", "func_3_", "func_4_", "func_5_"};

        long startTime = System.currentTimeMillis();
        int totalRecords = 0;
        for (String pattern : patterns) {
            List<CompositeFunctionEntity> functions = compositeFunctionDao.findByExpressionContaining(pattern);
            totalRecords += functions.size();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Patterns: {} | Total records: {} | Avg: {} ms/pattern",
                duration, patterns.length, totalRecords, duration / (double) patterns.length);
    }

    @Test
    @Order(19)
    @DisplayName("19. COMPOSITE_FUNCTION - create (100 inserts)")
    void testCompositeFunctionCreate() {
        logger.info("\n=== COMPOSITE_FUNCTION: create ===");

        Long userId = 1L;
        int insertCount = 100;

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= insertCount; i++) {
            compositeFunctionDao.create(new CompositeFunctionEntity(
                    "new_comp_func_" + i + "(x)",
                    userId
            ));
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Inserts: {} | Avg: {} ms/insert",
                duration, insertCount, duration / (double) insertCount);
    }

    @Test
    @Order(20)
    @DisplayName("20. COMPOSITE_FUNCTION - deleteById (50 deletes)")
    void testCompositeFunctionDelete() {
        logger.info("\n=== COMPOSITE_FUNCTION: deleteById ===");

        Long userId = 1L;
        List<Long> idsToDelete = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Long id = compositeFunctionDao.create(new CompositeFunctionEntity(
                    "delete_comp_func_" + i + "(x)",
                    userId
            ));
            idsToDelete.add(id);
        }

        long startTime = System.currentTimeMillis();
        for (Long id : idsToDelete) {
            compositeFunctionDao.deleteById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Deletes: {} | Avg: {} ms/delete",
                duration, idsToDelete.size(), duration / (double) idsToDelete.size());
    }

    @Test
    @Order(21)
    @DisplayName("21. Final Statistics")
    void testFinalStatistics() {
        logger.info("\n=== FINAL STATISTICS (JDBC DAO) ===");
        logger.info("Total users: {}", userDao.findAll().size());
        logger.info("Total tabulated functions: {}", tabulatedFunctionDao.findAll().size());
        logger.info("Total composite functions: {}", compositeFunctionDao.findAll().size());
    }

    private byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        return bytes;
    }

    private List<Long> getRandomUserIds(int count) {
        List<Long> ids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            ids.add((long) (random.nextInt(USERS_COUNT) + 1));
        }
        return ids;
    }

    private List<Long> getRandomFunctionIds(int count) {
        List<Long> ids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            ids.add((long) (random.nextInt(TOTAL_RECORDS) + 1));
        }
        return ids;
    }
}
