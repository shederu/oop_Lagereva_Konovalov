package ru.ssau.tk._shederu_._lab1_.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserEntity authenticate(String login, String password) {
        logger.info("Аутентификация пользователя: {}", login);

        UserEntity user = userDao.findByLogin(login).orElse(null);
        if (user == null) {
            logger.warn("Пользователь {} не найден", login);
            return null;
        }

        if (BCrypt.checkpw(password, user.getPassword())) {
            logger.info("Пользователь {} аутентифицирован", login);
            return user;
        } else {
            logger.warn("Неверный пароль для пользователя {}", login);
            return null;
        }
    }

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}