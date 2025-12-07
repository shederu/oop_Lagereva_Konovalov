package ru.ssau.tk._shederu_._lab1_.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.UserRegistrationDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;
import ru.ssau.tk._shederu_._lab1_.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserRegistrationDto dto) {
        logger.info("Registration request for login: {}", dto.getLogin());
        try {
            UserEntity user = userService.registerUser(dto);
            logger.info("User {} registered successfully with ID {}", dto.getLogin(), user.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("id", user.getId(), "login", user.getLogin(), "message", "User registered successfully"));
        } catch (Exception e) {
            logger.error("Registration failed for login {}: {}", dto.getLogin(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(Authentication authentication) {
        logger.debug("Fetching current user: {}", authentication.getName());
        UserEntity user = userRepository.findByLogin(authentication.getName()).orElse(null);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserEntity>> getAllUsers(Authentication auth) {
        logger.info("ADMIN {} fetching all users", auth.getName());
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id, Authentication auth) {
        logger.debug("ADMIN {} fetching user with ID: {}", auth.getName(), id);
        UserEntity user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
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
}
