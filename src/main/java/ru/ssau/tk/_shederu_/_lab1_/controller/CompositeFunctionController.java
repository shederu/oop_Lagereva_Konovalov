package ru.ssau.tk._shederu_._lab1_.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.CompositeFunctionRepository;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;
import ru.ssau.tk._shederu_._lab1_.service.CompositeFunctionService;

import java.util.List;

@RestController
@RequestMapping("/api/composite-functions")
public class CompositeFunctionController {
    private static final Logger logger = LoggerFactory.getLogger(CompositeFunctionController.class);

    @Autowired
    private CompositeFunctionService compositeFunctionService;

    @Autowired
    private CompositeFunctionRepository compositeFunctionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<List<CompositeFunctionDto>> getAllFunctions(Authentication auth) {
        logger.info("User {} fetching all composite functions", auth.getName());
        List<CompositeFunctionDto> functions = compositeFunctionService.getAllFunctions();
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<CompositeFunctionDto> getFunctionById(@PathVariable Long id, Authentication auth) {
        logger.debug("User {} fetching composite function with ID: {}", auth.getName(), id);
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        CompositeFunctionDto function = compositeFunctionService.getFunctionById(id);
        return function != null ? ResponseEntity.ok(function) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CompositeFunctionDto>> getFunctionsByUserId(@PathVariable Long userId, Authentication auth) {
        logger.info("ADMIN {} fetching composite functions for user {}", auth.getName(), userId);
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<CompositeFunctionDto> functions = compositeFunctionService.getFunctionsByUserId(userId);
        return ResponseEntity.ok(functions);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<CompositeFunctionDto> createFunction(@RequestBody CompositeFunctionDto functionDto, Authentication auth) {
        logger.info("User {} (CREATOR/ADMIN) creating composite function: {}", auth.getName(), functionDto.getExpression());

        if (functionDto == null || functionDto.getExpression() == null || functionDto.getExpression().trim().isEmpty() ||
                functionDto.getUserId() == null || functionDto.getUserId() <= 0) {
            logger.warn("Invalid composite function data provided");
            return ResponseEntity.badRequest().build();
        }

        CompositeFunctionDto created = compositeFunctionService.createFunction(functionDto);
        logger.info("Composite function created successfully: id={}, expression={}", created.getId(), created.getExpression());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<CompositeFunctionDto> updateFunction(@PathVariable Long id,
                                                               @RequestBody CompositeFunctionDto functionDto,
                                                               Authentication auth) {
        logger.info("User {} attempting to update composite function {}", auth.getName(), id);

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        if (functionDto == null || functionDto.getExpression() == null || functionDto.getExpression().trim().isEmpty() ||
                functionDto.getUserId() == null || functionDto.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CompositeFunctionEntity existing = compositeFunctionRepository.findById(id).orElse(null);
        if (existing == null) {
            logger.warn("Composite function not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = existing.getUserId().equals(
                userRepository.findByLogin(auth.getName()).get().getId()
        );

        if (!isAdmin && !isOwner) {
            logger.warn("User {} tried to update composite function {} which they don't own", auth.getName(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CompositeFunctionDto updated = compositeFunctionService.updateFunction(id, functionDto);
        logger.info("Composite function {} updated successfully by {}", id, auth.getName());
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id, Authentication auth) {
        logger.info("User {} attempting to delete composite function {}", auth.getName(), id);

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CompositeFunctionEntity existing = compositeFunctionRepository.findById(id).orElse(null);
        if (existing == null) {
            logger.warn("Composite function not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = existing.getUserId().equals(
                userRepository.findByLogin(auth.getName()).get().getId()
        );

        if (!isAdmin && !isOwner) {
            logger.warn("User {} tried to delete composite function {} which they don't own", auth.getName(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        compositeFunctionService.deleteFunction(id);
        logger.info("Composite function {} deleted successfully by {}", id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
