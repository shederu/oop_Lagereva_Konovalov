package ru.ssau.tk._shederu_._lab1_.performance;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FrameworkPerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(FrameworkPerformanceTest.class);
    private static final int USERS_COUNT = 100;
    private static final int FUNCTIONS_PER_USER = 100;
    private static final int TOTAL_RECORDS = USERS_COUNT * FUNCTIONS_PER_USER;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private CompositeFunctionRepository compositeFunctionRepository;

    @BeforeAll
    static void setup() {
        logger.info("=== FRAMEWORK (SPRING DATA JPA) PERFORMANCE TEST ===");
        logger.info("Users: {}", USERS_COUNT);
        logger.info("Functions per user: {}", FUNCTIONS_PER_USER);
        logger.info("Total records: {}", TOTAL_RECORDS);
    }

    @Test
    @Order(1)
    @DisplayName("1. Generate Test Data")
    void testDataGeneration() {
        logger.info("\n=== DATA GENERATION ===");

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= USERS_COUNT; i++) {
            UserEntity user = new UserEntity("user_" + i, "password_" + i);
            UserEntity savedUser = userRepository.save(user);

            for (int j = 1; j <= FUNCTIONS_PER_USER; j++) {
                TabulatedFunctionEntity tabFunc = new TabulatedFunctionEntity(
                        "tab_func_" + i + "_" + j,
                        generateRandomBytes(50),
                        generateRandomBytes(50),
                        savedUser.getId()
                );
                tabulatedFunctionRepository.save(tabFunc);

                CompositeFunctionEntity compFunc = new CompositeFunctionEntity(
                        "comp_func_" + i + "_" + j + "(x)",
                        savedUser.getId()
                );
                compositeFunctionRepository.save(compFunc);
            }

            if (i % 10 == 0) {
                logger.info("Created users: {} / {}", i, USERS_COUNT);
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Data generation time: {} ms", duration);
        logger.info("Users in DB: {}", userRepository.count());
        logger.info("Tabulated functions: {}", tabulatedFunctionRepository.count());
        logger.info("Composite functions: {}", compositeFunctionRepository.count());
    }

    @Test
    @Order(2)
    @DisplayName("2. USER TABLE - findAll")
    void testUserFindAll() {
        logger.info("\n=== USER: findAll ===");

        long startTime = System.currentTimeMillis();
        List<UserEntity> users = userRepository.findAll();
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
            userRepository.findById(id);
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
            logins.add("user_" + (new Random().nextInt(USERS_COUNT) + 1));
        }

        long startTime = System.currentTimeMillis();
        for (String login : logins) {
            userRepository.findByLogin(login);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, logins.size(), duration / (double) logins.size());
    }

    @Test
    @Order(5)
    @DisplayName("5. USER TABLE - findAll with Pagination")
    void testUserFindAllWithPagination() {
        logger.info("\n=== USER: findAll with Pagination ===");

        int pageSize = 10;
        int totalPages = USERS_COUNT / pageSize;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < totalPages; i++) {
            Page<UserEntity> page = userRepository.findAll(PageRequest.of(i, pageSize));
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Pages: {} | Page size: {} | Avg: {} ms/page",
                duration, totalPages, pageSize, duration / (double) totalPages);
    }

    @Test
    @Order(6)
    @DisplayName("6. USER TABLE - existsByLogin (50 queries)")
    void testUserExistsByLogin() {
        logger.info("\n=== USER: existsByLogin ===");

        List<String> logins = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            logins.add("user_" + (new Random().nextInt(USERS_COUNT) + 1));
        }

        long startTime = System.currentTimeMillis();
        int existsCount = 0;
        for (String login : logins) {
            if (userRepository.existsByLogin(login)) {
                existsCount++;
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Exists: {} | Avg: {} ms/query",
                duration, logins.size(), existsCount, duration / (double) logins.size());
    }

    @Test
    @Order(7)
    @DisplayName("7. USER TABLE - save (100 inserts)")
    void testUserSave() {
        logger.info("\n=== USER: save ===");

        int insertCount = 100;

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= insertCount; i++) {
            userRepository.save(new UserEntity("new_user_" + i, "password"));
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Inserts: {} | Avg: {} ms/insert",
                duration, insertCount, duration / (double) insertCount);
    }

    @Test
    @Order(8)
    @DisplayName("8. USER TABLE - update (50 updates)")
    void testUserUpdate() {
        logger.info("\n=== USER: update ===");

        List<Long> ids = getRandomUserIds(50);

        long startTime = System.currentTimeMillis();
        for (Long id : ids) {
            userRepository.findById(id).ifPresent(user -> {
                user.setPassword("updated_password");
                userRepository.save(user);
            });
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Updates: {} | Avg: {} ms/update",
                duration, ids.size(), duration / (double) ids.size());
    }

    @Test
    @Order(9)
    @DisplayName("9. USER TABLE - deleteById (50 deletes)")
    void testUserDelete() {
        logger.info("\n=== USER: deleteById ===");

        List<Long> idsToDelete = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            UserEntity user = userRepository.save(new UserEntity("delete_user_" + i, "pass"));
            idsToDelete.add(user.getId());
        }

        long startTime = System.currentTimeMillis();
        for (Long id : idsToDelete) {
            userRepository.deleteById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Deletes: {} | Avg: {} ms/delete",
                duration, idsToDelete.size(), duration / (double) idsToDelete.size());
    }

    @Test
    @Order(10)
    @DisplayName("10. TABULATED_FUNCTION - findAll")
    void testTabulatedFunctionFindAll() {
        logger.info("\n=== TABULATED_FUNCTION: findAll ===");

        long startTime = System.currentTimeMillis();
        List<TabulatedFunctionEntity> functions = tabulatedFunctionRepository.findAll();
        long endTime = System.currentTimeMillis();

        logger.info("Time: {} ms | Records: {}", endTime - startTime, functions.size());
        assertEquals(TOTAL_RECORDS, functions.size());
    }

    @Test
    @Order(11)
    @DisplayName("11. TABULATED_FUNCTION - findById (100 queries)")
    void testTabulatedFunctionFindById() {
        logger.info("\n=== TABULATED_FUNCTION: findById ===");

        List<Long> ids = getRandomFunctionIds(100);

        long startTime = System.currentTimeMillis();
        for (Long id : ids) {
            tabulatedFunctionRepository.findById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, ids.size(), duration / (double) ids.size());
    }

    @Test
    @Order(12)
    @DisplayName("12. TABULATED_FUNCTION - findByName (100 queries)")
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
            tabulatedFunctionRepository.findByName(name);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, names.size(), duration / (double) names.size());
    }

    @Test
    @Order(13)
    @DisplayName("13. TABULATED_FUNCTION - findByUserId (50 queries)")
    void testTabulatedFunctionFindByUserId() {
        logger.info("\n=== TABULATED_FUNCTION: findByUserId ===");

        List<Long> userIds = getRandomUserIds(50);

        long startTime = System.currentTimeMillis();
        int totalRecords = 0;
        for (Long userId : userIds) {
            List<TabulatedFunctionEntity> functions = tabulatedFunctionRepository.findByUserId(userId);
            totalRecords += functions.size();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Total records: {} | Avg: {} ms/query",
                duration, userIds.size(), totalRecords, duration / (double) userIds.size());
    }

    @Test
    @Order(14)
    @DisplayName("14. TABULATED_FUNCTION - existsByName (50 queries)")
    void testTabulatedFunctionExistsByName() {
        logger.info("\n=== TABULATED_FUNCTION: existsByName ===");

        List<String> names = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 50; i++) {
            int userId = random.nextInt(USERS_COUNT) + 1;
            int funcNum = random.nextInt(FUNCTIONS_PER_USER) + 1;
            names.add("tab_func_" + userId + "_" + funcNum);
        }

        long startTime = System.currentTimeMillis();
        int existsCount = 0;
        for (String name : names) {
            if (tabulatedFunctionRepository.existsByName(name)) {
                existsCount++;
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Exists: {} | Avg: {} ms/query",
                duration, names.size(), existsCount, duration / (double) names.size());
    }

    @Test
    @Order(15)
    @DisplayName("15. TABULATED_FUNCTION - save (100 inserts)")
    void testTabulatedFunctionSave() {
        logger.info("\n=== TABULATED_FUNCTION: save ===");

        Long userId = 1L;
        int insertCount = 100;

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= insertCount; i++) {
            tabulatedFunctionRepository.save(new TabulatedFunctionEntity(
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
    @Order(16)
    @DisplayName("16. TABULATED_FUNCTION - deleteById (50 deletes)")
    void testTabulatedFunctionDelete() {
        logger.info("\n=== TABULATED_FUNCTION: deleteById ===");

        Long userId = 1L;
        List<Long> idsToDelete = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            TabulatedFunctionEntity func = tabulatedFunctionRepository.save(new TabulatedFunctionEntity(
                    "delete_tab_func_" + i,
                    generateRandomBytes(50),
                    generateRandomBytes(50),
                    userId
            ));
            idsToDelete.add(func.getId());
        }

        long startTime = System.currentTimeMillis();
        for (Long id : idsToDelete) {
            tabulatedFunctionRepository.deleteById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Deletes: {} | Avg: {} ms/delete",
                duration, idsToDelete.size(), duration / (double) idsToDelete.size());
    }

    @Test
    @Order(17)
    @DisplayName("17. COMPOSITE_FUNCTION - findAll")
    void testCompositeFunctionFindAll() {
        logger.info("\n=== COMPOSITE_FUNCTION: findAll ===");

        long startTime = System.currentTimeMillis();
        List<CompositeFunctionEntity> functions = compositeFunctionRepository.findAll();
        long endTime = System.currentTimeMillis();

        logger.info("Time: {} ms | Records: {}", endTime - startTime, functions.size());
        assertEquals(TOTAL_RECORDS, functions.size());
    }

    @Test
    @Order(18)
    @DisplayName("18. COMPOSITE_FUNCTION - findById (100 queries)")
    void testCompositeFunctionFindById() {
        logger.info("\n=== COMPOSITE_FUNCTION: findById ===");

        List<Long> ids = getRandomFunctionIds(100);

        long startTime = System.currentTimeMillis();
        for (Long id : ids) {
            compositeFunctionRepository.findById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, ids.size(), duration / (double) ids.size());
    }

    @Test
    @Order(19)
    @DisplayName("19. COMPOSITE_FUNCTION - findByExpression (100 queries)")
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
            compositeFunctionRepository.findByExpression(expression);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Avg: {} ms/query",
                duration, expressions.size(), duration / (double) expressions.size());
    }

    @Test
    @Order(20)
    @DisplayName("20. COMPOSITE_FUNCTION - findByUserId (50 queries)")
    void testCompositeFunctionFindByUserId() {
        logger.info("\n=== COMPOSITE_FUNCTION: findByUserId ===");

        List<Long> userIds = getRandomUserIds(50);

        long startTime = System.currentTimeMillis();
        int totalRecords = 0;
        for (Long userId : userIds) {
            List<CompositeFunctionEntity> functions = compositeFunctionRepository.findByUserId(userId);
            totalRecords += functions.size();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Total records: {} | Avg: {} ms/query",
                duration, userIds.size(), totalRecords, duration / (double) userIds.size());
    }

    @Test
    @Order(21)
    @DisplayName("21. COMPOSITE_FUNCTION - existsByExpression (50 queries)")
    void testCompositeFunctionExistsByExpression() {
        logger.info("\n=== COMPOSITE_FUNCTION: existsByExpression ===");

        List<String> expressions = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 50; i++) {
            int userId = random.nextInt(USERS_COUNT) + 1;
            int funcNum = random.nextInt(FUNCTIONS_PER_USER) + 1;
            expressions.add("comp_func_" + userId + "_" + funcNum + "(x)");
        }

        long startTime = System.currentTimeMillis();
        int existsCount = 0;
        for (String expression : expressions) {
            if (compositeFunctionRepository.existsByExpression(expression)) {
                existsCount++;
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Queries: {} | Exists: {} | Avg: {} ms/query",
                duration, expressions.size(), existsCount, duration / (double) expressions.size());
    }

    @Test
    @Order(22)
    @DisplayName("22. COMPOSITE_FUNCTION - save (100 inserts)")
    void testCompositeFunctionSave() {
        logger.info("\n=== COMPOSITE_FUNCTION: save ===");

        Long userId = 1L;
        int insertCount = 100;

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= insertCount; i++) {
            compositeFunctionRepository.save(new CompositeFunctionEntity(
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
    @Order(23)
    @DisplayName("23. COMPOSITE_FUNCTION - deleteById (50 deletes)")
    void testCompositeFunctionDelete() {
        logger.info("\n=== COMPOSITE_FUNCTION: deleteById ===");

        Long userId = 1L;
        List<Long> idsToDelete = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            CompositeFunctionEntity func = compositeFunctionRepository.save(new CompositeFunctionEntity(
                    "delete_comp_func_" + i + "(x)",
                    userId
            ));
            idsToDelete.add(func.getId());
        }

        long startTime = System.currentTimeMillis();
        for (Long id : idsToDelete) {
            compositeFunctionRepository.deleteById(id);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Time: {} ms | Deletes: {} | Avg: {} ms/delete",
                duration, idsToDelete.size(), duration / (double) idsToDelete.size());
    }

    @Test
    @Order(24)
    @DisplayName("24. Final Statistics")
    void testFinalStatistics() {
        logger.info("\n=== FINAL STATISTICS (SPRING DATA JPA) ===");
        logger.info("Total users: {}", userRepository.count());
        logger.info("Total tabulated functions: {}", tabulatedFunctionRepository.count());
        logger.info("Total composite functions: {}", compositeFunctionRepository.count());
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
