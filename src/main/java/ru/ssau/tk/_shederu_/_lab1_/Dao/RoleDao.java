package ru.ssau.tk._shederu_._lab1_.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.entities.RoleEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleDao {
    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);
    private final DataSourceProvider dataSourceProvider;

    public RoleDao(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<RoleEntity> findById(Long id) {
        String sql = "SELECT * FROM role WHERE id = ?";
        logger.info("Поиск роли по id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RoleEntity role = mapResultSetToEntity(rs);
                    logger.info("Роль по id: {} найдена", id);
                    return Optional.of(role);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска роли по id: {}", id, e);
        }
        logger.debug("Роль по id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<RoleEntity> findByName(String name) {
        String sql = "SELECT * FROM role WHERE name = ?";
        logger.info("Поиск роли по имени: {}", name);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RoleEntity role = mapResultSetToEntity(rs);
                    logger.info("Роль: {} найдена", name);
                    return Optional.of(role);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска роли по имени: {}", name, e);
        }
        logger.debug("Роль: {} не найдена", name);
        return Optional.empty();
    }

    public List<RoleEntity> findAll() {
        String sql = "SELECT * FROM role ORDER BY name";
        logger.info("Загрузка всех ролей");
        List<RoleEntity> roles = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                roles.add(mapResultSetToEntity(rs));
            }
            logger.info("Загружено {} ролей", roles.size());
        } catch (SQLException e) {
            logger.error("Ошибка загрузки всех ролей", e);
        }
        return roles;
    }

    public Long create(RoleEntity role) {
        String sql = "INSERT INTO role (name) VALUES (?)";
        logger.info("Создание роли: {}", role.getName());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, role.getName());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Роль: {} создана с id: {}", role.getName(), id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания роли: {}", role.getName(), e);
        }
        logger.error("Роль: {} не была создана", role.getName());
        return null;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM role WHERE id = ?";
        logger.info("Удаление роли с id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Роль с id: {} удалена", id);
            } else {
                logger.warn("Роль с id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления роли с id: {}", id, e);
        }
        return false;
    }

    private RoleEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        RoleEntity role = new RoleEntity();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        return role;
    }
}