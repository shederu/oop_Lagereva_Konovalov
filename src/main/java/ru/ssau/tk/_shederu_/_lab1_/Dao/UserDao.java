package ru.ssau.tk._shederu_._lab1_.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.entities.RoleEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.sql.*;
import java.util.*;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final DataSourceProvider dataSourceProvider;
    private final RoleDao roleDao;

    public UserDao(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
        this.roleDao = new RoleDao(dataSourceProvider);
    }

    public Optional<UserEntity> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        logger.info("Начало поиска пользователя по id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserEntity user = mapResultSetToEntity(rs);
                    loadUserRoles(user);
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
    public Optional<UserEntity> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        logger.info("Начало поиска пользователя по login: {}", login);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserEntity user = mapResultSetToEntity(rs);
                    loadUserRoles(user);
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
        String sql = "SELECT * FROM users ORDER BY login";
        logger.info("Выгрузка всех пользователей");
        List<UserEntity> users = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UserEntity user = mapResultSetToEntity(rs);
                loadUserRoles(user);
                users.add(user);
            }
            logger.info("Выгружено {} пользователей", users.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех пользователей", e);
        }
        return users;
    }

    public boolean existsByLogin(String login) {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ?";
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

    public Long create(UserEntity user) {
        String sql = "INSERT INTO users (login, password) VALUES (?, ?)";
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

                        for (RoleEntity role : user.getRoles()) {
                            addRoleToUser(id, role.getId());
                        }

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
            boolean updated = update(user);
            if (updated) {
                updateUserRoles(user);
                return user;
            } else {
                logger.error("Failed to update user with id: {}", user.getId());
                return null;
            }
        }
    }

    private boolean update(UserEntity user) {
        String sql = "UPDATE users SET login = ?, password = ? WHERE id = ?";
        logger.info("Обновление пользователя id: {}", user.getId());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            stmt.setLong(3, user.getId());

            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Пользователь id: {} обновлён", user.getId());
            } else {
                logger.warn("Пользователь id: {} не найден для обновления", user.getId());
            }
            return success;
        } catch (SQLException e) {
            logger.error("Ошибка обновления пользователя id: {}", user.getId(), e);
            return false;
        }
    }

    private void updateUserRoles(UserEntity user) {
        String deleteSql = "DELETE FROM user_roles WHERE user_id = ?";
        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

            stmt.setLong(1, user.getId());
            stmt.executeUpdate();

            for (RoleEntity role : user.getRoles()) {
                addRoleToUser(user.getId(), role.getId());
            }
        } catch (SQLException e) {
            logger.error("Ошибка обновления ролей пользователя id: {}", user.getId(), e);
        }
    }

    public void addRoleToUser(Long userId, Long roleId) {
        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        logger.info("Добавление роли {} пользователю {}", roleId, userId);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Ошибка добавления роли {} пользователю {}", roleId, userId, e);
        }
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

    public void removeRoleFromUser(Long userId, Long roleId) {
        String sql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
        logger.info("Удаление роли {} у пользователя {}", roleId, userId);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Ошибка удаления роли {} у пользователя {}", roleId, userId, e);
        }
    }

    private void loadUserRoles(UserEntity user) {
        String sql = "SELECT r.id, r.name FROM role r " +
                "INNER JOIN user_roles ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?";

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RoleEntity role = new RoleEntity();
                    role.setId(rs.getLong("id"));
                    role.setName(rs.getString("name"));
                    user.getRoles().add(role);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка загрузки ролей пользователя id: {}", user.getId(), e);
        }
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
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

    private UserEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        UserEntity user = new UserEntity();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        return user;
    }
}