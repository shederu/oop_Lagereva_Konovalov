package ru.ssau.tk._shederu_._lab1_.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.Dao.RoleDao;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;
import ru.ssau.tk._shederu_._lab1_.dto.UserRegistrationDto;
import ru.ssau.tk._shederu_._lab1_.entities.RoleEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.mapper.UserMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final AuthService authService;

    public UserService(UserDao userDao, RoleDao roleDao, AuthService authService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.authService = authService;
    }

    public UserDto registerUser(UserRegistrationDto dto) {
        logger.info("Регистрация пользователя: {}", dto.getLogin());

        if (userDao.existsByLogin(dto.getLogin())) {
            logger.warn("Пользователь с логином {} уже существует", dto.getLogin());
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }

        UserEntity user = new UserEntity();
        user.setLogin(dto.getLogin());
        user.setPassword(authService.hashPassword(dto.getPassword()));

        RoleEntity creatorRole = roleDao.findByName("CREATOR")
                .orElseGet(() -> {
                    logger.info("Создание роли CREATOR");
                    RoleEntity newRole = new RoleEntity("CREATOR");
                    Long id = roleDao.create(newRole);
                    newRole.setId(id);
                    return newRole;
                });

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(creatorRole);
        user.setRoles(roles);

        Long userId = userDao.create(user);
        if (userId != null) {
            user.setId(userId);
            logger.info("Пользователь {} успешно зарегистрирован с ролью CREATOR", dto.getLogin());
            return UserMapper.toDTO(user);
        } else {
            logger.error("Ошибка при регистрации пользователя {}", dto.getLogin());
            throw new RuntimeException("Ошибка при регистрации пользователя");
        }
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

    public UserDto createUser(UserDto userDto, String password) {
        logger.info("Создание пользователя: {}", userDto.getLogin());

        UserEntity entity = UserMapper.toEntity(userDto, password);
        entity.setPassword(authService.hashPassword(password));

        Long id = userDao.create(entity);

        if (id != null) {
            entity.setId(id);
            UserDto dto = UserMapper.toDTO(entity);
            logger.info("Пользователь {} создан с id: {}", userDto.getLogin(), id);
            return dto;
        }

        logger.error("Не удалось создать пользователя: {}", userDto.getLogin());
        return null;
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        logger.info("Обновление пользователя id: {}", id);

        Optional<UserEntity> existingUser = userDao.findById(id);
        if (!existingUser.isPresent()) {
            logger.warn("Пользователь id: {} не найден", id);
            return null;
        }

        UserEntity entity = existingUser.get();
        entity.setLogin(userDto.getLogin());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            entity.setPassword(authService.hashPassword(userDto.getPassword()));
        }

        entity.setRoles(userDto.getRoles());

        UserEntity saved = userDao.save(entity);
        if (saved != null) {
            return UserMapper.toDTO(saved);
        }

        logger.error("Ошибка при обновлении пользователя id: {}", id);
        return null;
    }

    public boolean assignRoleToUser(Long userId, String roleName) {
        logger.info("Назначение роли {} пользователю id: {}", roleName, userId);

        Optional<UserEntity> userOpt = userDao.findById(userId);
        Optional<RoleEntity> roleOpt = roleDao.findByName(roleName);

        if (!userOpt.isPresent()) {
            logger.warn("Пользователь id: {} не найден", userId);
            return false;
        }

        if (!roleOpt.isPresent()) {
            logger.warn("Роль {} не найдена", roleName);
            return false;
        }

        UserEntity user = userOpt.get();
        RoleEntity role = roleOpt.get();

        if (user.hasRole(roleName)) {
            logger.info("У пользователя id: {} уже есть роль {}", userId, roleName);
            return true;
        }

        userDao.addRoleToUser(userId, role.getId());
        user.getRoles().add(role);

        logger.info("Роль {} назначена пользователю id: {}", roleName, userId);
        return true;
    }

    public boolean removeRoleFromUser(Long userId, String roleName) {
        logger.info("Удаление роли {} у пользователя id: {}", roleName, userId);

        Optional<UserEntity> userOpt = userDao.findById(userId);
        Optional<RoleEntity> roleOpt = roleDao.findByName(roleName);

        if (!userOpt.isPresent()) {
            logger.warn("Пользователь id: {} не найден", userId);
            return false;
        }

        if (!roleOpt.isPresent()) {
            logger.warn("Роль {} не найдена", roleName);
            return false;
        }

        UserEntity user = userOpt.get();
        RoleEntity role = roleOpt.get();

        if (!user.hasRole(roleName)) {
            logger.info("У пользователя id: {} нет роли {}", userId, roleName);
            return true;
        }

        userDao.removeRoleFromUser(userId, role.getId());
        user.getRoles().removeIf(r -> r.getName().equals(roleName));

        logger.info("Роль {} удалена у пользователя id: {}", roleName, userId);
        return true;
    }

    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя id: {}", id);
        return userDao.deleteById(id);
    }

    public boolean isAdmin(UserEntity user) {
        return user != null && user.hasRole("ADMIN");
    }

    public boolean isCreator(UserEntity user) {
        return user != null && user.hasRole("CREATOR");
    }

    public boolean isViewer(UserEntity user) {
        return user != null && user.hasRole("VIEWER");
    }
}