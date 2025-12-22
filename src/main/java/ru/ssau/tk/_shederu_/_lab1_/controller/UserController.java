package ru.ssau.tk._shederu_._lab1_.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;
import ru.ssau.tk._shederu_._lab1_.dto.UserRegistrationDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;
import ru.ssau.tk._shederu_._lab1_.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ ДОБАВЬ ЭТУ СТРОКУ

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserRegistrationDto dto) {
        try {
            System.out.println("Received DTO: " + dto);
            System.out.println("Login: " + dto.getLogin());
            System.out.println("Password: " + dto.getPassword());

            if (dto.getLogin() == null || dto.getLogin().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Логин не может быть пустым");
                return ResponseEntity.badRequest().body(error);
            }

            if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Пароль не может быть пустым");
                return ResponseEntity.badRequest().body(error);
            }

            UserEntity user = userService.registerUser(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Пользователь зарегистрирован");
            response.put("id", user.getId());
            response.put("login", user.getLogin());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserRegistrationDto dto) {
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Login: " + dto.getLogin());
            System.out.println("Password: " + dto.getPassword());

            UserEntity user = userService.findByLogin(dto.getLogin());

            if (user == null) {
                System.out.println("User not found!");
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Пользователь не найден");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            System.out.println("User found: " + user.getLogin());
            System.out.println("Stored password hash: " + user.getPassword());
            System.out.println("Input password: " + dto.getPassword());

            // Проверяем пароль
            boolean matches = passwordEncoder.matches(dto.getPassword(), user.getPassword());
            System.out.println("Password matches: " + matches);

            if (!matches) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Неверный пароль");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Вход успешен");
            response.put("id", user.getId());
            response.put("login", user.getLogin());

            System.out.println("Login successful!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        logger.debug("Fetching current user: {}", authentication.getName());
        UserEntity user = userRepository.findByLogin(authentication.getName()).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(convertToDto(user));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers(Authentication auth) {
        logger.info("ADMIN {} fetching all users", auth.getName());
        List<UserDto> users = userService.getAllUsers()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id, Authentication auth) {
        logger.debug("ADMIN {} fetching user with ID: {}", auth.getName(), id);
        try {
            UserEntity user = userService.getUserById(id);
            return user != null ? ResponseEntity.ok(convertToDto(user)) : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            logger.warn("User not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignRole(@PathVariable Long userId, @PathVariable String roleName, Authentication auth) {
        logger.info("User {} is assigning role {} to user {}", auth.getName(), roleName, userId);
        try {
            userService.assignRoleToUser(userId, roleName);
            return ResponseEntity.ok("Role assigned successfully");
        } catch (Exception e) {
            logger.error("Failed to assign role: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                              @RequestBody UserDto userDetails,
                                              Authentication auth) {
        logger.info("ADMIN {} is updating user with ID {}", auth.getName(), id);

        if (id == null || id <= 0) {
            logger.warn("Update failed: invalid ID {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            UserEntity entityToUpdate = convertDtoToEntity(userDetails);
            UserEntity updated = userService.updateUser(id, entityToUpdate);
            logger.info("User with ID {} updated successfully", id);
            return ResponseEntity.ok(convertToDto(updated));
        } catch (RuntimeException e) {
            logger.warn("Update failed for ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeRole(@PathVariable Long userId, @PathVariable String roleName, Authentication auth) {
        logger.info("User {} is removing role {} from user {}", auth.getName(), roleName, userId);
        try {
            userService.removeRoleFromUser(userId, roleName);
            return ResponseEntity.ok("Role removed successfully");
        } catch (Exception e) {
            logger.error("Failed to remove role: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication auth) {
        logger.info("User {} is deleting user with ID {}", auth.getName(), id);
        if (!userRepository.existsById(id)) {
            logger.warn("Deletion failed: user with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        logger.info("User with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    private UserDto convertToDto(UserEntity user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        return dto;
    }

    private UserEntity convertDtoToEntity(UserDto dto) {
        UserEntity entity = new UserEntity();
        entity.setLogin(dto.getLogin());
        entity.setPassword(dto.getPassword());
        return entity;
    }
}
