package ru.ssau.tk._shederu_._lab1_.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.config.DbConfig;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.RoleDao;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.entities.RoleEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@WebListener
public class DataInitializerListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Инициализация данных приложения...");

        try {
            DataSourceProvider dataSourceProvider = new DataSourceProvider(
                    DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASSWORD);

            createTablesIfNotExists(dataSourceProvider);

            RoleDao roleDao = new RoleDao(dataSourceProvider);
            UserDao userDao = new UserDao(dataSourceProvider);

            initializeRoles(roleDao);
            initializeAdminUser(userDao, roleDao);

            logger.info("Инициализация данных завершена успешно");

        } catch (Exception e) {
            logger.error("Ошибка при инициализации данных", e);
            throw new RuntimeException("Failed to initialize application data", e);
        }
    }

    private void createTablesIfNotExists(DataSourceProvider dataSourceProvider) {
        String[] createTables = {

                "CREATE TABLE IF NOT EXISTS role (" +
                        "    id BIGSERIAL PRIMARY KEY," +
                        "    name VARCHAR(50) UNIQUE NOT NULL" +
                        ")",

                "CREATE TABLE IF NOT EXISTS users (" +
                        "    id BIGSERIAL PRIMARY KEY," +
                        "    login VARCHAR(100) UNIQUE NOT NULL," +
                        "    password VARCHAR(255) NOT NULL" +
                        ")",

                "CREATE TABLE IF NOT EXISTS user_roles (" +
                        "    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE," +
                        "    role_id BIGINT NOT NULL REFERENCES role(id) ON DELETE CASCADE," +
                        "    PRIMARY KEY (user_id, role_id)" +
                        ")",

                "CREATE TABLE IF NOT EXISTS tabulated_function (" +
                        "    id BIGSERIAL PRIMARY KEY," +
                        "    name VARCHAR(100) NOT NULL," +
                        "    data BYTEA NOT NULL," +
                        "    derivative BYTEA NOT NULL," +
                        "    user_id BIGINT NOT NULL," +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ")",

                "CREATE TABLE IF NOT EXISTS composite_function (" +
                        "    id BIGSERIAL PRIMARY KEY," +
                        "    expression TEXT NOT NULL," +
                        "    user_id BIGINT NOT NULL," +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ")"
        };

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : createTables) {
                stmt.executeUpdate(sql);
            }
            logger.info("Таблицы созданы или уже существуют");

        } catch (Exception e) {
            logger.error("Ошибка при создании таблиц", e);
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    private void initializeRoles(RoleDao roleDao) {
        String[] roleNames = {"ADMIN", "CREATOR", "VIEWER"};

        for (String roleName : roleNames) {
            if (roleDao.findByName(roleName).isEmpty()) {
                RoleEntity role = new RoleEntity(roleName);
                Long id = roleDao.create(role);
                if (id != null) {
                    logger.info("Создана роль: {}", roleName);
                } else {
                    logger.error("Не удалось создать роль: {}", roleName);
                }
            } else {
                logger.debug("Роль {} уже существует", roleName);
            }
        }
    }

    private void initializeAdminUser(UserDao userDao, RoleDao roleDao) {
        if (userDao.findByLogin("admin").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setLogin("admin");
            admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));

            RoleEntity adminRole = roleDao.findByName("ADMIN").orElse(null);
            if (adminRole != null) {
                Set<RoleEntity> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);
            }

            Long adminId = userDao.create(admin);
            if (adminId != null) {
                logger.info("Создан администратор: login=admin, password=admin123");
            } else {
                logger.error("Не удалось создать администратора");
            }
        } else {
            logger.debug("Администратор уже существует");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Приложение завершает работу...");
    }
}