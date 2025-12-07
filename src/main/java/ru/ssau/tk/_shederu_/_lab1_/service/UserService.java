package ru.ssau.tk._shederu_._lab1_.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.dto.UserRegistrationDto;
import ru.ssau.tk._shederu_._lab1_.entities.RoleEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.repository.RoleRepository;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserEntity registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByLogin(dto.getLogin())) {
            logger.warn("Registration failed: user with login {} already exists", dto.getLogin());
            throw new RuntimeException("User already exists");
        }

        UserEntity user = new UserEntity();
        user.setLogin(dto.getLogin());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        RoleEntity creatorRole = roleRepository.findByName("CREATOR")
                .orElseGet(() -> {
                    logger.info("Creating default CREATOR role");
                    return roleRepository.save(new RoleEntity("CREATOR"));
                });

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(creatorRole);
        user.setRoles(roles);

        UserEntity savedUser = userRepository.save(user);
        logger.info("User registered successfully: login={}, roles={}", dto.getLogin(), roles);
        return savedUser;
    }


    public void assignRoleToUser(Long userId, String roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
        logger.info("Role {} assigned to user {}", roleName, user.getLogin());
    }

    public void removeRoleFromUser(Long userId, String roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().removeIf(role -> role.getName().equals(roleName));
        userRepository.save(user);
        logger.info("Role {} removed from user {}", roleName, user.getLogin());
    }


    public List<UserEntity> getAllUsers() {
        logger.debug("Fetching all users");
        return userRepository.findAll();
    }

    public UserEntity getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    public UserEntity findByLogin(String login) {
        logger.debug("Fetching user by login: {}", login);
        return userRepository.findByLogin(login).orElse(null);
    }


    public UserEntity createUser(UserEntity user) {
        logger.info("Creating user: {}", user.getLogin());
        return userRepository.save(user);
    }

    public UserEntity updateUser(Long id, UserEntity userDetails) {
        logger.info("Updating user with ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    if (userDetails.getLogin() != null) {
                        user.setLogin(userDetails.getLogin());
                    }
                    if (userDetails.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }
                    UserEntity updated = userRepository.save(user);
                    logger.info("User {} updated successfully", id);
                    return updated;
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            logger.warn("Delete failed: user with ID {} not found", id);
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        logger.info("User with id {} deleted", id);
    }
}
