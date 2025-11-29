package ru.ssau.tk._shederu_._lab1_.service;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceTest.class);
    private static final int USERS_COUNT = 20;
    private static final int FUNCTIONS_PER_USER = 10;

    @Autowired
    private SearchService searchService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private CompositeFunctionRepository compositeFunctionRepository;

    @BeforeAll
    void setup() {
        logger.info("=== SEARCH SERVICE TEST ===");
        logger.info("Creating test data...");

        for (int i = 1; i <= USERS_COUNT; i++) {
            UserEntity user = new UserEntity("search_user_" + i, "password_" + i);
            UserEntity savedUser = userRepository.save(user);

            for (int j = 1; j <= FUNCTIONS_PER_USER; j++) {
                TabulatedFunctionEntity tabFunc = new TabulatedFunctionEntity(
                        "search_tab_func_" + i + "_" + j,
                        new byte[]{1, 2, 3},
                        new byte[]{4, 5, 6},
                        savedUser.getId()
                );
                tabulatedFunctionRepository.save(tabFunc);

                CompositeFunctionEntity compFunc = new CompositeFunctionEntity(
                        "search_comp_func_" + i + "_" + j + "(x)",
                        savedUser.getId()
                );
                compositeFunctionRepository.save(compFunc);
            }
        }

        logger.info("Test data setup completed");
    }

    @Test
    @Order(1)
    @DisplayName("1. Single Search - findUserById")
    void testSingleSearchUserById() {
        logger.info("\n=== TEST 1: SINGLE SEARCH - findUserById ===");

        long startTime = System.currentTimeMillis();
        var user = searchService.findUserById(1L);
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(user.isPresent());
        logger.info("✓ Found user: {} | Time: {} ms", user.get().getLogin(), duration);
    }

    @Test
    @Order(2)
    @DisplayName("2. Single Search - findUserByLogin")
    void testSingleSearchUserByLogin() {
        logger.info("\n=== TEST 2: SINGLE SEARCH - findUserByLogin ===");

        long startTime = System.currentTimeMillis();
        var user = searchService.findUserByLogin("search_user_1");
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(user.isPresent());
        logger.info("✓ Found user ID: {} | Time: {} ms", user.get().getId(), duration);
    }

    @Test
    @Order(3)
    @DisplayName("3. Multiple Search - findAllUsers")
    void testMultipleSearchAllUsers() {
        logger.info("\n=== TEST 3: MULTIPLE SEARCH - findAllUsers ===");

        long startTime = System.currentTimeMillis();
        List<UserEntity> users = searchService.findAllUsers();
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(users.size() >= USERS_COUNT);
        logger.info("✓ Found {} users | Time: {} ms", users.size(), duration);
    }

    @Test
    @Order(4)
    @DisplayName("4. Sorted Search - findAllUsersSorted")
    void testSortedSearchUsers() {
        logger.info("\n=== TEST 4: SORTED SEARCH - findAllUsersSorted ===");

        long startTime = System.currentTimeMillis();
        List<UserEntity> usersSorted = searchService.findAllUsersSorted("login", Sort.Direction.ASC);
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(usersSorted.size() >= USERS_COUNT);
        logger.info("✓ Sorted {} users by login | Time: {} ms", usersSorted.size(), duration);

        if (usersSorted.size() > 0) {
            logger.info("  First: {}", usersSorted.get(0).getLogin());
            logger.info("  Last: {}", usersSorted.get(usersSorted.size() - 1).getLogin());
        }
    }

    @Test
    @Order(5)
    @DisplayName("5. BFS Search - findUsersBFS")
    void testBFSSearch() {
        logger.info("\n=== TEST 5: BFS SEARCH - findUsersBFS ===");

        long startTime = System.currentTimeMillis();
        Map<UserEntity, List<TabulatedFunctionEntity>> result = searchService.findUsersBFS();
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(result.size() >= USERS_COUNT);
        int totalFunctions = result.values().stream().mapToInt(List::size).sum();
        logger.info("✓ BFS: {} users, {} total functions | Time: {} ms",
                result.size(), totalFunctions, duration);
    }

    @Test
    @Order(6)
    @DisplayName("6. DFS Search - findUsersDFS")
    void testDFSSearch() {
        logger.info("\n=== TEST 6: DFS SEARCH - findUsersDFS ===");

        long startTime = System.currentTimeMillis();
        Map<UserEntity, List<TabulatedFunctionEntity>> result = searchService.findUsersDFS();
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(result.size() >= USERS_COUNT);
        int totalFunctions = result.values().stream().mapToInt(List::size).sum();
        logger.info("✓ DFS: {} users, {} total functions | Time: {} ms",
                result.size(), totalFunctions, duration);
    }

    @Test
    @Order(7)
    @DisplayName("7. Hierarchy BFS - findFunctionsByHierarchyBFS")
    void testHierarchyBFS() {
        logger.info("\n=== TEST 7: HIERARCHY BFS - findFunctionsByHierarchyBFS ===");

        long startTime = System.currentTimeMillis();
        Map<Long, List<TabulatedFunctionEntity>> result = searchService.findFunctionsByHierarchyBFS();
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(result.size() >= USERS_COUNT);
        logger.info("✓ Hierarchy BFS: {} users | Time: {} ms", result.size(), duration);
    }

    @Test
    @Order(8)
    @DisplayName("8. Hierarchy DFS - findFunctionsByHierarchyDFS")
    void testHierarchyDFS() {
        logger.info("\n=== TEST 8: HIERARCHY DFS - findFunctionsByHierarchyDFS ===");

        long startTime = System.currentTimeMillis();
        Map<Long, List<TabulatedFunctionEntity>> result = searchService.findFunctionsByHierarchyDFS();
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(result.size() >= USERS_COUNT);
        logger.info("✓ Hierarchy DFS: {} users | Time: {} ms", result.size(), duration);
    }

    @Test
    @Order(9)
    @DisplayName("9. Sorted Function Search")
    void testSortedFunctionSearch() {
        logger.info("\n=== TEST 9: SORTED SEARCH - findFunctionsByUserIdSorted ===");

        long startTime = System.currentTimeMillis();
        List<TabulatedFunctionEntity> functions = searchService.findFunctionsByUserIdSorted(
                1L, "name", Sort.Direction.DESC
        );
        long duration = System.currentTimeMillis() - startTime;

        assertEquals(FUNCTIONS_PER_USER, functions.size());
        logger.info("✓ Sorted functions: {} | Time: {} ms", functions.size(), duration);

        if (functions.size() > 0) {
            logger.info("  First: {}", functions.get(0).getName());
            logger.info("  Last: {}", functions.get(functions.size() - 1).getName());
        }
    }

    @Test
    @Order(10)
    @DisplayName("10. Search Composite by Expression")
    void testSearchCompositeByExpression() {
        logger.info("\n=== TEST 10: SEARCH - findCompositeFunctionByExpression ===");

        long startTime = System.currentTimeMillis();
        var function = searchService.findCompositeFunctionByExpression("search_comp_func_1_1(x)");
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(function.isPresent());
        logger.info("✓ Found composite function: {} | Time: {} ms",
                function.get().getExpression(), duration);
    }

    @AfterAll
    void cleanup() {
        logger.info("\n=== TEST SUITE COMPLETED ===");
        logger.info("All search tests passed successfully!");
    }
}
