package ru.ssau.tk._shederu_._lab1_.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<UserDto> getUserById(Long id) {
        logger.info("Запрос пользователя по id: {}", id);

        Optional<UserEntity> entity = userDao.findById(id);

        if (entity.isPresent()) {
            UserDto dto = UserMapper.toDTO(entity.get());
            logger.info("Пользователь id: {} трансформирован в DTO", id);
            return Optional.of(dto);
        }

        logger.debug("Пользователь id: {} не найден", id);
        return Optional.empty();
    }

    public Optional<UserDto> getUserByLogin(String login) {
        logger.info("Запрос пользователя по login: {}", login);

        Optional<UserEntity> entity = userDao.findByLogin(login);

        if (entity.isPresent()) {
            UserDto dto = UserMapper.toDTO(entity.get());
            logger.info("Пользователь {} трансформирован в DTO", login);
            return Optional.of(dto);
        }

        logger.debug("Пользователь {} не найден", login);
        return Optional.empty();
    }

    public List<UserDto> getAllUsers() {
        logger.info("Запрос всех пользователей");

        List<UserEntity> entities = userDao.findAll();
        List<UserDto> dtos = entities.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Получено {} пользователей, трансформировано в DTO", dtos.size());
        return dtos;
    }

    public UserDto createUser(String login, String password) {
        logger.info("Создание нового пользователя: {}", login);

        UserEntity entity = new UserEntity(login, password);
        Long id = userDao.create(entity);

        if (id != null) {
            entity.setId(id);
            UserDto dto = UserMapper.toDTO(entity);
            logger.info("Пользователь {} создан с id: {}", login, id);
            return dto;
        }

        logger.error("Не удалось создать пользователя: {}", login);
        return null;
    }

    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя id: {}", id);
        return userDao.deleteById(id);
    }
}
