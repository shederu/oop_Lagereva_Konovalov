package ru.ssau.tk._shederu_._lab1_.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabulatedFunctionDao {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionDao.class);
    private final DataSourceProvider dataSourceProvider;

    public TabulatedFunctionDao(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<TabulatedFunctionEntity> findById(Long id) {
        String sql = "SELECT * FROM tabulated_function WHERE id = ?";
        logger.info("Начало поиска табулированной функции по id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TabulatedFunctionEntity function = mapResultSetToEntity(rs);
                    logger.info("Табулированная функция по id: {} найдена", id);
                    return Optional.of(function);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функции по id: {}", id, e);
        }
        logger.debug("Табулированная функция по id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<TabulatedFunctionEntity> findByName(String name) {
        String sql = "SELECT * FROM tabulated_function WHERE name = ?";
        logger.info("Начало поиска функции по имени: {}", name);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TabulatedFunctionEntity function = mapResultSetToEntity(rs);
                    logger.info("Функция: {} найдена", name);
                    return Optional.of(function);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функции по имени: {}", name, e);
        }
        logger.debug("Функция: {} не найдена", name);
        return Optional.empty();
    }

    public List<TabulatedFunctionEntity> findByUserId(Long userId) {
        String sql = "SELECT * FROM tabulated_function WHERE user_id = ?";
        logger.info("Начало поиска функций пользователя с id: {}", userId);
        List<TabulatedFunctionEntity> functions = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    functions.add(mapResultSetToEntity(rs));
                }
            }
            logger.info("Найдено {} функций пользователя {}", functions.size(), userId);
        } catch (SQLException e) {
            logger.error("Ошибка поиска функций пользователя: {}", userId, e);
        }
        return functions;
    }

    public Optional<TabulatedFunctionEntity> findByNameAndUserId(String name, Long userId) {
        String sql = "SELECT * FROM tabulated_function WHERE name = ? AND user_id = ?";
        logger.info("Начало поиска функции: {} пользователя: {}", name, userId);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setLong(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TabulatedFunctionEntity function = mapResultSetToEntity(rs);
                    logger.info("Функция: {} пользователя: {} найдена", name, userId);
                    return Optional.of(function);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функции: {} пользователя: {}", name, userId, e);
        }
        logger.debug("Функция: {} пользователя: {} не найдена", name, userId);
        return Optional.empty();
    }

    public List<TabulatedFunctionEntity> findAll() {
        String sql = "SELECT * FROM tabulated_function ORDER BY name";
        logger.info("Выгрузка всех табулированных функций");
        List<TabulatedFunctionEntity> functions = new ArrayList<>();

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

    public Long create(TabulatedFunctionEntity function) {
        String sql = "INSERT INTO tabulated_function (name, data, derivative, user_id) VALUES (?, ?, ?, ?)";
        logger.info("Создание функции: {} для пользователя: {}", function.getName(), function.getUserId());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, function.getName());
            stmt.setBytes(2, function.getData());
            stmt.setBytes(3, function.getDerivative());
            stmt.setLong(4, function.getUserId());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Функция: {} создана с id: {}", function.getName(), id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания функции: {}", function.getName(), e);
        }
        logger.error("Функция: {} не была создана", function.getName());
        return null;
    }

    public boolean updateData(Long id, byte[] data) {
        String sql = "UPDATE tabulated_function SET data = ? WHERE id = ?";
        logger.info("Обновление data для функции с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, data);
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Data функции с id: {} обновлена", id);
            } else {
                logger.warn("Функция с id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления data для id: {}", id, e);
        }
        return false;
    }

    public boolean updateDerivative(Long id, byte[] derivative) {
        String sql = "UPDATE tabulated_function SET derivative = ? WHERE id = ?";
        logger.info("Обновление derivative для функции с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, derivative);
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Derivative функции с id: {} обновлена", id);
            } else {
                logger.warn("Функция с id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления derivative для id: {}", id, e);
        }
        return false;
    }

    public boolean updateName(Long id, String name) {
        String sql = "UPDATE tabulated_function SET name = ? WHERE id = ?";
        logger.info("Обновление имени функции с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Имя функции с id: {} обновлено на: {}", id, name);
            } else {
                logger.warn("Функция с id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления имени для id: {}", id, e);
        }
        return false;
    }

    public boolean updateUserId(Long id, Long newUserId) {
        String sql = "UPDATE tabulated_function SET user_id = ? WHERE id = ?";
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
        String sql = "DELETE FROM tabulated_function WHERE id = ?";
        logger.info("Удаление функции с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Функция с id: {} удалена", id);
            } else {
                logger.warn("Функция с id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления функции с id: {}", id, e);
        }
        return false;
    }

    private TabulatedFunctionEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        TabulatedFunctionEntity function = new TabulatedFunctionEntity();
        function.setId(rs.getLong("id"));
        function.setName(rs.getString("name"));
        function.setData(rs.getBytes("data"));
        function.setDerivative(rs.getBytes("derivative"));
        function.setUserId(rs.getLong("user_id"));
        return function;
    }
}