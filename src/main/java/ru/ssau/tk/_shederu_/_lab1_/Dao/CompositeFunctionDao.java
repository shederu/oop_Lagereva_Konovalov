package ru.ssau.tk._shederu_._lab1_.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompositeFunctionDao {
    private static final Logger logger = LoggerFactory.getLogger(CompositeFunctionDao.class);
    private final DataSourceProvider dataSourceProvider;

    public CompositeFunctionDao(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<CompositeFunctionEntity> findById(Long id) {
        String sql = "SELECT * FROM composite_function WHERE id = ?";
        logger.info("Начало поиска композитной функции по id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CompositeFunctionEntity function = mapResultSetToEntity(rs);
                    logger.info("Композитная функция по id: {} найдена", id);
                    return Optional.of(function);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска композитной функции по id: {}", id, e);
        }
        logger.debug("Композитная функция по id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<CompositeFunctionEntity> findByExpression(String expression) {
        String sql = "SELECT * FROM composite_function WHERE expression = ?";
        logger.info("Начало поиска по выражению: {}", expression);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, expression);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CompositeFunctionEntity function = mapResultSetToEntity(rs);
                    logger.info("Композитная функция с выражением: {} найдена", expression);
                    return Optional.of(function);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска по выражению: {}", expression, e);
        }
        logger.debug("Композитная функция с выражением: {} не найдена", expression);
        return Optional.empty();
    }

    public List<CompositeFunctionEntity> findByExpressionContaining(String pattern) {
        String sql = "SELECT * FROM composite_function WHERE expression LIKE ?";
        logger.info("Начало поиска по паттерну: {}", pattern);
        List<CompositeFunctionEntity> functions = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + pattern + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    functions.add(mapResultSetToEntity(rs));
                }
            }
            logger.info("Найдено {} функций по паттерну: {}", functions.size(), pattern);
        } catch (SQLException e) {
            logger.error("Ошибка поиска по паттерну: {}", pattern, e);
        }
        return functions;
    }

    public List<CompositeFunctionEntity> findByUserId(Long userId) {
        String sql = "SELECT * FROM composite_function WHERE user_id = ?";
        logger.info("Начало поиска функций пользователя: {}", userId);
        List<CompositeFunctionEntity> functions = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    functions.add(mapResultSetToEntity(rs));
                }
            }
            logger.info("Найдено {} функций пользователя: {}", functions.size(), userId);
        } catch (SQLException e) {
            logger.error("Ошибка поиска функций пользователя: {}", userId, e);
        }
        return functions;
    }

    public List<CompositeFunctionEntity> findByExpressionContainingAndUserId(String pattern, Long userId) {
        String sql = "SELECT * FROM composite_function WHERE expression LIKE ? AND user_id = ?";
        logger.info("Поиск по паттерну: {} для пользователя: {}", pattern, userId);
        List<CompositeFunctionEntity> functions = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + pattern + "%");
            stmt.setLong(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    functions.add(mapResultSetToEntity(rs));
                }
            }
            logger.info("Найдено {} функций", functions.size());
        } catch (SQLException e) {
            logger.error("Ошибка поиска", e);
        }
        return functions;
    }

    public List<CompositeFunctionEntity> findAll() {
        String sql = "SELECT * FROM composite_function ORDER BY expression";
        logger.info("Выгрузка всех композитных функций");
        List<CompositeFunctionEntity> functions = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                functions.add(mapResultSetToEntity(rs));
            }
            logger.info("Выгружено {} функций", functions.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех функций", e);
        }
        return functions;
    }

    public Long create(CompositeFunctionEntity function) {
        String sql = "INSERT INTO composite_function (expression, user_id) VALUES (?, ?)";
        logger.info("Создание композитной функции: {} для пользователя: {}",
                function.getExpression(), function.getUserId());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, function.getExpression());
            stmt.setLong(2, function.getUserId());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Композитная функция создана с id: {}", id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания композитной функции", e);
        }
        logger.error("Композитная функция не была создана");
        return null;
    }

    public boolean updateExpression(Long id, String expression) {
        String sql = "UPDATE composite_function SET expression = ? WHERE id = ?";
        logger.info("Обновление выражения для функции с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, expression);
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Выражение функции с id: {} обновлено", id);
            } else {
                logger.warn("Функция с id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления выражения для id: {}", id, e);
        }
        return false;
    }

    public boolean updateUserId(Long id, Long newUserId) {
        String sql = "UPDATE composite_function SET user_id = ? WHERE id = ?";
        logger.info("Передача функции id: {} пользователю: {}", id, newUserId);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, newUserId);
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Функция id: {} передана пользователю: {}", id, newUserId);
            } else {
                logger.warn("Функция с id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка передачи функции id: {} пользователю: {}", id, newUserId, e);
        }
        return false;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM composite_function WHERE id = ?";
        logger.info("Удаление композитной функции с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Композитная функция с id: {} удалена", id);
            } else {
                logger.warn("Функция с id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления функции с id: {}", id, e);
        }
        return false;
    }

    public boolean deleteByExpression(String expression) {
        String sql = "DELETE FROM composite_function WHERE expression = ?";
        logger.info("Удаление функции с выражением: {}", expression);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, expression);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Функция с выражением: {} удалена", expression);
            } else {
                logger.warn("Функция с выражением: {} не найдена", expression);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления функции с выражением: {}", expression, e);
        }
        return false;
    }

    public void deleteAll() {
        String sql = "DELETE FROM composite_function";
        logger.info("Удаление всех композитных функций");

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {

            int affectedRows = stmt.executeUpdate(sql);
            logger.info("Удалено {} функций", affectedRows);
        } catch (SQLException e) {
            logger.error("Ошибка удаления всех функций", e);
        }
    }

    private CompositeFunctionEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        CompositeFunctionEntity function = new CompositeFunctionEntity();
        function.setId(rs.getLong("id"));
        function.setExpression(rs.getString("expression"));
        function.setUserId(rs.getLong("user_id"));
        return function;
    }
}