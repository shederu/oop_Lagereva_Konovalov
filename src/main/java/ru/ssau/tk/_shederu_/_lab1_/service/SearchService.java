package ru.ssau.tk._shederu_._lab1_.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.*;

import java.util.*;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private CompositeFunctionRepository compositeFunctionRepository;

    public Optional<UserEntity> findUserById(Long id) {
        logger.debug("SearchService: findUserById({})", id);
        return userRepository.findById(id);
    }

    public Optional<UserEntity> findUserByLogin(String login) {
        logger.debug("SearchService: findUserByLogin({})", login);
        return userRepository.findByLogin(login);
    }

    public List<UserEntity> findAllUsers() {
        logger.debug("SearchService: findAllUsers");
        return userRepository.findAll();
    }

    public List<UserEntity> findAllUsersSorted(String field, Sort.Direction direction) {
        logger.debug("SearchService: findAllUsersSorted(field={}, direction={})", field, direction);
        return userRepository.findAll(Sort.by(direction, field));
    }

    public Map<UserEntity, List<TabulatedFunctionEntity>> findUsersBFS() {
        logger.debug("SearchService: findUsersBFS - поиск в ширину");
        Map<UserEntity, List<TabulatedFunctionEntity>> result = new LinkedHashMap<>();

        Queue<UserEntity> queue = new LinkedList<>(userRepository.findAll());
        int level = 0;

        while (!queue.isEmpty()) {
            UserEntity user = queue.poll();
            List<TabulatedFunctionEntity> functions = tabulatedFunctionRepository.findByUserId(user.getId());
            result.put(user, functions);

            logger.trace("BFS Level {}: User {}, Functions count {}", level, user.getId(), functions.size());
            level++;
        }

        logger.debug("SearchService: BFS completed, users processed: {}", result.size());
        return result;
    }

    public Map<UserEntity, List<TabulatedFunctionEntity>> findUsersDFS() {
        logger.debug("SearchService: findUsersDFS - поиск в глубину");
        Map<UserEntity, List<TabulatedFunctionEntity>> result = new LinkedHashMap<>();
        Stack<UserEntity> stack = new Stack<>();

        stack.addAll(userRepository.findAll());

        while (!stack.isEmpty()) {
            UserEntity user = stack.pop();
            List<TabulatedFunctionEntity> functions = tabulatedFunctionRepository.findByUserId(user.getId());
            result.put(user, functions);

            logger.trace("DFS: User {}, Functions count {}", user.getId(), functions.size());
        }

        logger.debug("SearchService: DFS completed, users processed: {}", result.size());
        return result;
    }

    public Optional<TabulatedFunctionEntity> findTabulatedFunctionById(Long id) {
        logger.debug("SearchService: findTabulatedFunctionById({})", id);
        return tabulatedFunctionRepository.findById(id);
    }

    public Optional<TabulatedFunctionEntity> findTabulatedFunctionByName(String name) {
        logger.debug("SearchService: findTabulatedFunctionByName({})", name);
        return tabulatedFunctionRepository.findByName(name);
    }

    public List<TabulatedFunctionEntity> findFunctionsByUserId(Long userId) {
        logger.debug("SearchService: findFunctionsByUserId({})", userId);
        return tabulatedFunctionRepository.findByUserId(userId);
    }

    public List<TabulatedFunctionEntity> findFunctionsByUserIdSorted(Long userId, String field, Sort.Direction direction) {
        logger.debug("SearchService: findFunctionsByUserIdSorted(userId={}, field={}, direction={})",
                userId, field, direction);
        List<TabulatedFunctionEntity> functions = findFunctionsByUserId(userId);

        functions.sort((f1, f2) -> {
            int comparison = 0;
            if ("name".equals(field)) {
                comparison = f1.getName().compareTo(f2.getName());
            } else if ("id".equals(field)) {
                comparison = f1.getId().compareTo(f2.getId());
            }
            return direction == Sort.Direction.ASC ? comparison : -comparison;
        });

        return functions;
    }

    public Map<Long, List<TabulatedFunctionEntity>> findFunctionsByHierarchyBFS() {
        logger.debug("SearchService: findFunctionsByHierarchyBFS - иерархический поиск в ширину");
        Map<Long, List<TabulatedFunctionEntity>> result = new LinkedHashMap<>();

        Queue<UserEntity> queue = new LinkedList<>(userRepository.findAll());

        while (!queue.isEmpty()) {
            UserEntity user = queue.poll();
            List<TabulatedFunctionEntity> functions = tabulatedFunctionRepository.findByUserId(user.getId());
            result.put(user.getId(), functions);

            logger.trace("Hierarchy BFS: User {}, Functions count {}", user.getId(), functions.size());
        }

        logger.debug("SearchService: Hierarchy BFS completed");
        return result;
    }

    public Map<Long, List<TabulatedFunctionEntity>> findFunctionsByHierarchyDFS() {
        logger.debug("SearchService: findFunctionsByHierarchyDFS - иерархический поиск в глубину");
        Map<Long, List<TabulatedFunctionEntity>> result = new LinkedHashMap<>();

        Stack<UserEntity> stack = new Stack<>();
        stack.addAll(userRepository.findAll());

        while (!stack.isEmpty()) {
            UserEntity user = stack.pop();
            List<TabulatedFunctionEntity> functions = tabulatedFunctionRepository.findByUserId(user.getId());
            result.put(user.getId(), functions);

            logger.trace("Hierarchy DFS: User {}, Functions count {}", user.getId(), functions.size());
        }

        logger.debug("SearchService: Hierarchy DFS completed");
        return result;
    }

    public Optional<CompositeFunctionEntity> findCompositeFunctionById(Long id) {
        logger.debug("SearchService: findCompositeFunctionById({})", id);
        return compositeFunctionRepository.findById(id);
    }

    public Optional<CompositeFunctionEntity> findCompositeFunctionByExpression(String expression) {
        logger.debug("SearchService: findCompositeFunctionByExpression({})", expression);
        return compositeFunctionRepository.findByExpression(expression);
    }

    public List<CompositeFunctionEntity> findCompositesByUserId(Long userId) {
        logger.debug("SearchService: findCompositesByUserId({})", userId);
        return compositeFunctionRepository.findByUserId(userId);
    }

    public List<CompositeFunctionEntity> findCompositesByUserIdSorted(Long userId, String field, Sort.Direction direction) {
        logger.debug("SearchService: findCompositesByUserIdSorted(userId={}, field={}, direction={})",
                userId, field, direction);
        List<CompositeFunctionEntity> functions = findCompositesByUserId(userId);

        functions.sort((f1, f2) -> {
            int comparison = 0;
            if ("expression".equals(field)) {
                comparison = f1.getExpression().compareTo(f2.getExpression());
            } else if ("id".equals(field)) {
                comparison = f1.getId().compareTo(f2.getId());
            }
            return direction == Sort.Direction.ASC ? comparison : -comparison;
        });

        return functions;
    }

    public List<CompositeFunctionEntity> findCompositesByExpressionContaining(String pattern) {
        logger.debug("SearchService: findCompositesByExpressionContaining({})", pattern);
        return compositeFunctionRepository.findByExpressionContaining(pattern);
    }
}
