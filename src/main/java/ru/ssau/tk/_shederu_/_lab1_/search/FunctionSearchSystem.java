package ru.ssau.tk._shederu_._lab1_.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.Dao.*;
import ru.ssau.tk._shederu_._lab1_.entities.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Система поиска функций с поддержкой нескольких алгоритмов
 */
public class FunctionSearchSystem {

    private static final Logger logger = LoggerFactory.getLogger(FunctionSearchSystem.class);

    private final CompositeFunctionDao compositeFunctionDao;
    private final TabulatedFunctionDao tabulatedFunctionDao;
    private final UserDao userDao;

    // ======================== ENUMS ========================

    public enum SearchAlgorithm {
        DEPTH_FIRST,
        BREADTH_FIRST,
        HIERARCHICAL
    }

    public enum SearchType {
        SINGLE,
        MULTIPLE
    }

    // ======================== INNER CLASSES ========================

    /**
     * Критерии поиска
     */
    public static class SearchCriteria {
        private String expressionPattern;
        private String namePattern;
        private String loginPattern;
        private Long userId;
        private Set<Class<?>> targetTypes;

        public SearchCriteria() {
            this.targetTypes = new HashSet<>();
        }

        public SearchCriteria withExpressionPattern(String pattern) {
            this.expressionPattern = pattern;
            return this;
        }

        public SearchCriteria withNamePattern(String pattern) {
            this.namePattern = pattern;
            return this;
        }

        public SearchCriteria withLoginPattern(String pattern) {
            this.loginPattern = pattern;
            return this;
        }

        public SearchCriteria withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public SearchCriteria withTargetType(Class<?> type) {
            this.targetTypes.add(type);
            return this;
        }

        public SearchCriteria withAllTargetTypes() {
            this.targetTypes.add(CompositeFunctionEntity.class);
            this.targetTypes.add(TabulatedFunctionEntity.class);
            this.targetTypes.add(UserEntity.class);
            return this;
        }

        // Getters
        public String getExpressionPattern() {
            return expressionPattern;
        }

        public String getNamePattern() {
            return namePattern;
        }

        public String getLoginPattern() {
            return loginPattern;
        }

        public Long getUserId() {
            return userId;
        }

        public Set<Class<?>> getTargetTypes() {
            return targetTypes;
        }

        public boolean isEmpty() {
            return expressionPattern == null && namePattern == null &&
                    loginPattern == null && userId == null && targetTypes.isEmpty();
        }
    }

    /**
     * Результат поиска
     */
    public static class SearchResult {
        private List<CompositeFunctionEntity> compositeFunctions;
        private List<TabulatedFunctionEntity> tabulatedFunctions;
        private List<UserEntity> users;

        public SearchResult() {
            this.compositeFunctions = new ArrayList<>();
            this.tabulatedFunctions = new ArrayList<>();
            this.users = new ArrayList<>();
        }

        // Getters
        public List<CompositeFunctionEntity> getCompositeFunctions() {
            return compositeFunctions;
        }

        public List<TabulatedFunctionEntity> getTabulatedFunctions() {
            return tabulatedFunctions;
        }

        public List<UserEntity> getUsers() {
            return users;
        }

        public int getTotalCount() {
            return compositeFunctions.size() + tabulatedFunctions.size() + users.size();
        }

        // Add methods
        public void addCompositeFunction(CompositeFunctionEntity function) {
            this.compositeFunctions.add(function);
        }

        public void addTabulatedFunction(TabulatedFunctionEntity function) {
            this.tabulatedFunctions.add(function);
        }

        public void addUser(UserEntity user) {
            this.users.add(user);
        }

        /**
         * Сортировка результатов по полю
         */
        public void sortByField(String fieldName, boolean ascending) {
            Comparator<CompositeFunctionEntity> compositeComparator = getCompositeComparator(fieldName, ascending);
            Comparator<TabulatedFunctionEntity> tabulatedComparator = getTabulatedComparator(fieldName, ascending);
            Comparator<UserEntity> userComparator = getUserComparator(fieldName, ascending);

            if (compositeComparator != null) {
                compositeFunctions.sort(compositeComparator);
            }

            if (tabulatedComparator != null) {
                tabulatedFunctions.sort(tabulatedComparator);
            }

            if (userComparator != null) {
                users.sort(userComparator);
            }
        }

        /**
         * Компаратор для CompositeFunctionEntity
         */
        private Comparator<CompositeFunctionEntity> getCompositeComparator(String fieldName, boolean ascending) {
            Comparator<CompositeFunctionEntity> comparator = null;

            if (fieldName == null) {
                return null;
            }

            switch (fieldName.toLowerCase()) {
                case "id":
                    comparator = Comparator.comparing(CompositeFunctionEntity::getId);
                    break;
                case "expression":
                    comparator = Comparator.comparing(CompositeFunctionEntity::getExpression);
                    break;
            }

            return comparator != null ? (ascending ? comparator : comparator.reversed()) : null;
        }

        /**
         * Компаратор для TabulatedFunctionEntity
         */
        private Comparator<TabulatedFunctionEntity> getTabulatedComparator(String fieldName, boolean ascending) {
            Comparator<TabulatedFunctionEntity> comparator = null;

            if (fieldName == null) {
                return null;
            }

            switch (fieldName.toLowerCase()) {
                case "id":
                    comparator = Comparator.comparing(TabulatedFunctionEntity::getId);
                    break;
                case "name":
                    comparator = Comparator.comparing(TabulatedFunctionEntity::getName);
                    break;
            }

            return comparator != null ? (ascending ? comparator : comparator.reversed()) : null;
        }

        /**
         * Компаратор для UserEntity
         */
        private Comparator<UserEntity> getUserComparator(String fieldName, boolean ascending) {
            Comparator<UserEntity> comparator = null;

            if (fieldName == null) {
                return null;
            }

            switch (fieldName.toLowerCase()) {
                case "id":
                    comparator = Comparator.comparing(UserEntity::getId);
                    break;
                case "login":
                    comparator = Comparator.comparing(UserEntity::getLogin);
                    break;
            }

            return comparator != null ? (ascending ? comparator : comparator.reversed()) : null;
        }
    }

    // ======================== CONSTRUCTOR ========================

    public FunctionSearchSystem(DataSourceProvider dataSourceProvider) {
        this.compositeFunctionDao = new CompositeFunctionDao(dataSourceProvider);
        this.tabulatedFunctionDao = new TabulatedFunctionDao(dataSourceProvider);
        this.userDao = new UserDao(dataSourceProvider);
        logger.info("Система поиска инициализирована");
    }

    // ======================== PUBLIC METHODS ========================

    /**
     * Основной метод поиска
     */
    public SearchResult search(SearchCriteria criteria, SearchAlgorithm algorithm, SearchType searchType) {
        logger.info("Начало поиска: алгоритм={}, тип={}, критерии={}",
                algorithm, searchType,
                criteria.isEmpty() ? "ВСЕ" : formatCriteria(criteria));

        SearchResult result = new SearchResult();

        if (criteria.isEmpty()) {
            return searchAll(algorithm);
        }

        switch (algorithm) {
            case DEPTH_FIRST:
                depthFirstSearch(criteria, result, searchType);
                break;
            case BREADTH_FIRST:
                breadthFirstSearch(criteria, result, searchType);
                break;
            case HIERARCHICAL:
                hierarchicalSearch(criteria, result, searchType);
                break;
        }

        logger.info("Поиск завершен: найдено {} результатов", result.getTotalCount());
        return result;
    }

    // ======================== PRIVATE SEARCH METHODS ========================

    /**
     * Поиск всех данных
     */
    private SearchResult searchAll(SearchAlgorithm algorithm) {
        SearchResult result = new SearchResult();
        logger.debug("Поиск всех данных");

        result.getCompositeFunctions().addAll(compositeFunctionDao.findAll());
        result.getTabulatedFunctions().addAll(tabulatedFunctionDao.findAll());
        result.getUsers().addAll(userDao.findAll());

        return result;
    }

    /**
     * Поиск в глубину (DFS)
     */
    private void depthFirstSearch(SearchCriteria criteria, SearchResult result, SearchType searchType) {
        logger.debug("Выполнение поиска в глубину");
        Set<Long> processedUsers = new HashSet<>();

        if (shouldSearchType(UserEntity.class, criteria)) {
            searchUsersDFS(criteria, result, processedUsers, searchType);
        }

        for (Long userId : processedUsers) {
            searchFunctionsByUserDFS(userId, criteria, result, searchType);
        }

        if (criteria.getUserId() == null) {
            searchFunctionsDFS(criteria, result, searchType);
        }
    }

    /**
     * Поиск в ширину (BFS)
     */
    private void breadthFirstSearch(SearchCriteria criteria, SearchResult result, SearchType searchType) {
        logger.debug("Выполнение поиска в ширину");
        Queue<Long> userQueue = new LinkedList<>();
        Set<Long> processedUsers = new HashSet<>();

        if (criteria.getUserId() != null) {
            userQueue.add(criteria.getUserId());
            processedUsers.add(criteria.getUserId());
        } else if (criteria.getLoginPattern() != null) {
            userDao.findAll().stream()
                    .filter(user -> user.getLogin().contains(criteria.getLoginPattern()))
                    .map(UserEntity::getId)
                    .forEach(userId -> {
                        userQueue.add(userId);
                        processedUsers.add(userId);
                    });
        }

        while (!userQueue.isEmpty()) {
            Long userId = userQueue.poll();
            processUserBFS(userId, criteria, result, userQueue, processedUsers, searchType);
        }

        if (userQueue.isEmpty() && criteria.getUserId() == null) {
            searchFunctionsBFS(criteria, result, searchType);
        }
    }

    /**
     * Иерархический поиск
     */
    private void hierarchicalSearch(SearchCriteria criteria, SearchResult result, SearchType searchType) {
        logger.debug("Выполнение иерархического поиска");

        if (shouldSearchType(UserEntity.class, criteria)) {
            List<UserEntity> users = findUsersByCriteria(criteria);
            result.getUsers().addAll(users);
            for (UserEntity user : users) {
                searchFunctionsForUserHierarchical(user.getId(), criteria, result, searchType);
            }
        }

        if (criteria.getUserId() == null) {
            searchFunctionsHierarchical(criteria, result, searchType);
        }
    }

    // ======================== DFS HELPER METHODS ========================

    private void searchUsersDFS(SearchCriteria criteria, SearchResult result, Set<Long> processedUsers, SearchType searchType) {
        List<UserEntity> users = findUsersByCriteria(criteria);
        for (UserEntity user : users) {
            if (searchType == SearchType.SINGLE && !processedUsers.isEmpty()) break;
            if (!processedUsers.contains(user.getId())) {
                result.addUser(user);
                processedUsers.add(user.getId());
                logger.trace("Найден пользователь в DFS: {}", user.getLogin());
            }
        }
    }

    private void searchFunctionsByUserDFS(Long userId, SearchCriteria criteria, SearchResult result, SearchType searchType) {
        if (shouldSearchType(CompositeFunctionEntity.class, criteria)) {
            List<CompositeFunctionEntity> composites = compositeFunctionDao.findByUserId(userId);
            for (CompositeFunctionEntity function : composites) {
                if (matchesCompositeCriteria(function, criteria)) {
                    result.addCompositeFunction(function);
                    logger.trace("Найдена композитная функция в DFS: {}", function.getExpression());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }

        if (shouldSearchType(TabulatedFunctionEntity.class, criteria)) {
            List<TabulatedFunctionEntity> tabulateds = tabulatedFunctionDao.findByUserId(userId);
            for (TabulatedFunctionEntity function : tabulateds) {
                if (matchesTabulatedCriteria(function, criteria)) {
                    result.addTabulatedFunction(function);
                    logger.trace("Найдена табулированная функция в DFS: {}", function.getName());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }
    }

    private void searchFunctionsDFS(SearchCriteria criteria, SearchResult result, SearchType searchType) {
        if (shouldSearchType(CompositeFunctionEntity.class, criteria)) {
            List<CompositeFunctionEntity> composites = compositeFunctionDao.findAll();
            for (CompositeFunctionEntity function : composites) {
                if (matchesCompositeCriteria(function, criteria)) {
                    result.addCompositeFunction(function);
                    logger.trace("Найдена композитная функция в DFS: {}", function.getExpression());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }

        if (shouldSearchType(TabulatedFunctionEntity.class, criteria)) {
            List<TabulatedFunctionEntity> tabulateds = tabulatedFunctionDao.findAll();
            for (TabulatedFunctionEntity function : tabulateds) {
                if (matchesTabulatedCriteria(function, criteria)) {
                    result.addTabulatedFunction(function);
                    logger.trace("Найдена табулированная функция в DFS: {}", function.getName());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }
    }

    // ======================== BFS HELPER METHODS ========================

    private void processUserBFS(Long userId, SearchCriteria criteria, SearchResult result,
                                Queue<Long> userQueue, Set<Long> processedUsers, SearchType searchType) {
        if (shouldSearchType(CompositeFunctionEntity.class, criteria)) {
            List<CompositeFunctionEntity> composites = compositeFunctionDao.findByUserId(userId);
            for (CompositeFunctionEntity function : composites) {
                if (matchesCompositeCriteria(function, criteria)) {
                    result.addCompositeFunction(function);
                    logger.trace("Найдена композитная функция в BFS: {}", function.getExpression());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }

        if (shouldSearchType(TabulatedFunctionEntity.class, criteria)) {
            List<TabulatedFunctionEntity> tabulateds = tabulatedFunctionDao.findByUserId(userId);
            for (TabulatedFunctionEntity function : tabulateds) {
                if (matchesTabulatedCriteria(function, criteria)) {
                    result.addTabulatedFunction(function);
                    logger.trace("Найдена табулированная функция в BFS: {}", function.getName());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }
    }

    private void searchFunctionsBFS(SearchCriteria criteria, SearchResult result, SearchType searchType) {
        if (shouldSearchType(CompositeFunctionEntity.class, criteria)) {
            List<CompositeFunctionEntity> composites = compositeFunctionDao.findAll();
            for (CompositeFunctionEntity function : composites) {
                if (matchesCompositeCriteria(function, criteria)) {
                    result.addCompositeFunction(function);
                    logger.trace("Найдена композитная функция в BFS: {}", function.getExpression());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }

        if (shouldSearchType(TabulatedFunctionEntity.class, criteria)) {
            List<TabulatedFunctionEntity> tabulateds = tabulatedFunctionDao.findAll();
            for (TabulatedFunctionEntity function : tabulateds) {
                if (matchesTabulatedCriteria(function, criteria)) {
                    result.addTabulatedFunction(function);
                    logger.trace("Найдена табулированная функция в BFS: {}", function.getName());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }
    }

    // ======================== HIERARCHICAL HELPER METHODS ========================

    private void searchFunctionsForUserHierarchical(Long userId, SearchCriteria criteria, SearchResult result, SearchType searchType) {
        if (shouldSearchType(CompositeFunctionEntity.class, criteria)) {
            List<CompositeFunctionEntity> composites = compositeFunctionDao.findByUserId(userId);
            for (CompositeFunctionEntity function : composites) {
                if (matchesCompositeCriteria(function, criteria)) {
                    result.addCompositeFunction(function);
                    logger.trace("Найдена композитная функция в иерархическом поиске: {}", function.getExpression());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }

        if (shouldSearchType(TabulatedFunctionEntity.class, criteria)) {
            List<TabulatedFunctionEntity> tabulateds = tabulatedFunctionDao.findByUserId(userId);
            for (TabulatedFunctionEntity function : tabulateds) {
                if (matchesTabulatedCriteria(function, criteria)) {
                    result.addTabulatedFunction(function);
                    logger.trace("Найдена табулированная функция в иерархическом поиске: {}", function.getName());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }
    }

    private void searchFunctionsHierarchical(SearchCriteria criteria, SearchResult result, SearchType searchType) {
        if (shouldSearchType(CompositeFunctionEntity.class, criteria)) {
            List<CompositeFunctionEntity> composites = compositeFunctionDao.findAll();
            for (CompositeFunctionEntity function : composites) {
                if (matchesCompositeCriteria(function, criteria)) {
                    result.addCompositeFunction(function);
                    logger.trace("Найдена композитная функция в иерархическом поиске: {}", function.getExpression());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }

        if (shouldSearchType(TabulatedFunctionEntity.class, criteria)) {
            List<TabulatedFunctionEntity> tabulateds = tabulatedFunctionDao.findAll();
            for (TabulatedFunctionEntity function : tabulateds) {
                if (matchesTabulatedCriteria(function, criteria)) {
                    result.addTabulatedFunction(function);
                    logger.trace("Найдена табулированная функция в иерархическом поиске: {}", function.getName());
                    if (searchType == SearchType.SINGLE) return;
                }
            }
        }
    }

    // ======================== UTILITY METHODS ========================

    /**
     * Найти пользователей по критериям
     */
    private List<UserEntity> findUsersByCriteria(SearchCriteria criteria) {
        if (criteria.getUserId() != null) {
            // ✅ ИСПРАВЛЕНО: используем Optional.orElse() или Optional.stream()
            return userDao.findById(criteria.getUserId())
                    .map(Collections::singletonList)  // Если есть - обернуть в List
                    .orElse(Collections.emptyList());  // Если нет - пустой List
        }

        if (criteria.getLoginPattern() != null) {
            return userDao.findAll().stream()
                    .filter(user -> user.getLogin().contains(criteria.getLoginPattern()))
                    .collect(Collectors.toList());
        }

        return userDao.findAll();
    }

    /**
     * Проверка, совпадает ли CompositeFunctionEntity с критериями
     */
    private boolean matchesCompositeCriteria(CompositeFunctionEntity function, SearchCriteria criteria) {
        if (criteria.getExpressionPattern() != null) {
            return function.getExpression().contains(criteria.getExpressionPattern());
        }
        return true;
    }

    /**
     * Проверка, совпадает ли TabulatedFunctionEntity с критериями
     */
    private boolean matchesTabulatedCriteria(TabulatedFunctionEntity function, SearchCriteria criteria) {
        if (criteria.getNamePattern() != null) {
            return function.getName().contains(criteria.getNamePattern());
        }
        return true;
    }

    /**
     * Проверка, нужно ли искать указанный тип
     */
    private boolean shouldSearchType(Class<?> type, SearchCriteria criteria) {
        if (criteria.getTargetTypes().isEmpty()) {
            return true;
        }
        return criteria.getTargetTypes().contains(type);
    }

    /**
     * Форматирование критериев для логирования
     */
    private String formatCriteria(SearchCriteria criteria) {
        StringBuilder sb = new StringBuilder();

        if (criteria.getExpressionPattern() != null) {
            sb.append("expression=").append(criteria.getExpressionPattern()).append(", ");
        }
        if (criteria.getNamePattern() != null) {
            sb.append("name=").append(criteria.getNamePattern()).append(", ");
        }
        if (criteria.getLoginPattern() != null) {
            sb.append("login=").append(criteria.getLoginPattern()).append(", ");
        }
        if (criteria.getUserId() != null) {
            sb.append("userId=").append(criteria.getUserId()).append(", ");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }
}
