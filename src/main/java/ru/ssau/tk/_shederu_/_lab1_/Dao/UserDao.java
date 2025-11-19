package ru.ssau.tk._shederu_._lab1_.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final DataSourceProvider dataSourceProvider;

    public UserDao(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<UserEntity> findById(Long id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";
        logger.info("Начало поиска пользователя по id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserEntity user = mapResultSetToEntity(rs);
                    logger.info("Пользователь по id: {} найден", id);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя по id: {}", id, e);
        }
        logger.debug("Пользователь по id: {} не найден", id);
        return Optional.empty();
    }

    public Optional<UserEntity> findByLogin(String login) {
        String sql = "SELECT * FROM \"user\" WHERE login = ?";
        logger.info("Начало поиска пользователя по login: {}", login);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserEntity user = mapResultSetToEntity(rs);
                    logger.info("Пользователь: {} найден", login);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя по login: {}", login, e);
        }
        logger.debug("Пользователь: {} не найден", login);
        return Optional.empty();
    }

    public List<UserEntity> findAll() {
        String sql = "SELECT * FROM \"user\" ORDER BY login";
        logger.info("Выгрузка всех пользователей");
        List<UserEntity> users = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToEntity(rs));
            }
            logger.info("Выгружено {} пользователей", users.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех пользователей", e);
        }
        return users;
    }

    public boolean existsByLogin(String login) {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE login = ?";
        logger.info("Проверка существования пользователя: {}", login);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    logger.debug("Пользователь {} {}", login, exists ? "существует" : "не существует");
                    return exists;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка проверки существования пользователя: {}", login, e);
        }
        return false;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM \"user\"";
        logger.info("Подсчёт всех пользователей");

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Всего пользователей: {}", count);
                return count;
            }
        } catch (SQLException e) {
            logger.error("Ошибка подсчёта пользователей", e);
        }
        return 0;
    }

    public Long create(UserEntity user) {
        String sql = "INSERT INTO \"user\" (login, password) VALUES (?, ?)";
        logger.info("Создание пользователя: {}", user.getLogin());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Пользователь: {} создан с id: {}", user.getLogin(), id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания пользователя: {}", user.getLogin(), e);
        }
        logger.error("Пользователь: {} не был создан", user.getLogin());
        return null;
    }
    public UserEntity save(UserEntity user) {
        if (user.getId() == null) {
            Long id = create(user);
            user.setId(id);
            return user;
        } else {
            logger.warn("Метод save для обновления не полностью реализован");
            return user;
        }
    }

    public boolean updatePassword(Long id, String password) {
        String sql = "UPDATE \"user\" SET password = ? WHERE id = ?";
        logger.info("Обновление пароля для пользователя с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, password);
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Пароль пользователя с id: {} обновлён", id);
            } else {
                logger.warn("Пользователь с id: {} не найден", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления пароля для id: {}", id, e);
        }
        return false;
    }

    public boolean updateLogin(Long id, String login) {
        String sql = "UPDATE \"user\" SET login = ? WHERE id = ?";
        logger.info("Обновление логина для пользователя с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Логин пользователя с id: {} обновлён на: {}", id, login);
            } else {
                logger.warn("Пользователь с id: {} не найден", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка обновления логина для id: {}", id, e);
        }
        return false;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";
        logger.info("Удаление пользователя с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Пользователь с id: {} удалён", id);
            } else {
                logger.warn("Пользователь с id: {} не найден", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления пользователя с id: {}", id, e);
        }
        return false;
    }

    public boolean deleteByLogin(String login) {
        String sql = "DELETE FROM \"user\" WHERE login = ?";
        logger.info("Удаление пользователя: {}", login);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Пользователь: {} удалён", login);
            } else {
                logger.warn("Пользователь: {} не найден", login);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления пользователя: {}", login, e);
        }
        return false;
    }

    public void deleteAll() {
        String sql = "DELETE FROM \"user\"";
        logger.info("Удаление всех пользователей");

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {

            int affectedRows = stmt.executeUpdate(sql);
            logger.info("Удалено {} пользователей", affectedRows);
        } catch (SQLException e) {
            logger.error("Ошибка удаления всех пользователей", e);
        }
    }

    private UserEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        UserEntity user = new UserEntity();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        return user;
    }
}
