package ru.ssau.tk._shederu_._lab1_.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.repository.TabulatedFunctionRepository;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;
import ru.ssau.tk._shederu_._lab1_.service.TabulatedFunctionService;

import java.util.List;

@RestController
@RequestMapping("/api/tabulated-functions")
public class TabulatedFunctionController {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionController.class);

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<List<TabulatedFunctionDto>> getAllFunctions(Authentication auth) {
        logger.info("User {} fetching all tabulated functions", auth.getName());
        List<TabulatedFunctionDto> functions = tabulatedFunctionService.getAllFunctions();
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'CREATOR', 'ADMIN')")
    public ResponseEntity<TabulatedFunctionDto> getFunctionById(@PathVariable Long id, Authentication auth) {
        logger.debug("User {} fetching tabulated function with ID: {}", auth.getName(), id);
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        TabulatedFunctionDto function = tabulatedFunctionService.getFunctionById(id);
        return function != null ? ResponseEntity.ok(function) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TabulatedFunctionDto>> getFunctionsByUserId(@PathVariable Long userId, Authentication auth) {
        logger.info("ADMIN {} fetching functions for user {}", auth.getName(), userId);
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<TabulatedFunctionDto> functions = tabulatedFunctionService.getFunctionsByUserId(userId);
        return ResponseEntity.ok(functions);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<TabulatedFunctionDto> createFunction(@RequestBody TabulatedFunctionDto functionDto, Authentication auth) {
        logger.info("User {} (CREATOR/ADMIN) creating tabulated function: {}", auth.getName(), functionDto.getName());

        if (functionDto == null || functionDto.getName() == null || functionDto.getName().trim().isEmpty() ||
                functionDto.getData() == null || functionDto.getDerivative() == null ||
                functionDto.getUserId() == null || functionDto.getUserId() <= 0) {
            logger.warn("Invalid function data provided");
            return ResponseEntity.badRequest().build();
        }

        TabulatedFunctionDto created = tabulatedFunctionService.createFunction(functionDto);
        logger.info("Tabulated function created successfully: id={}, name={}", created.getId(), created.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<TabulatedFunctionDto> updateFunction(@PathVariable Long id,
                                                               @RequestBody TabulatedFunctionDto functionDto,
                                                               Authentication auth) {
        logger.info("User {} attempting to update tabulated function {}", auth.getName(), id);

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        if (functionDto == null || functionDto.getName() == null || functionDto.getName().trim().isEmpty() ||
                functionDto.getData() == null || functionDto.getDerivative() == null ||
                functionDto.getUserId() == null || functionDto.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        TabulatedFunctionEntity existing = tabulatedFunctionRepository.findById(id).orElse(null);
        if (existing == null) {
            logger.warn("Function not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = existing.getUserId().equals(
                userRepository.findByLogin(auth.getName()).get().getId()
        );

        if (!isAdmin && !isOwner) {
            logger.warn("User {} tried to update function {} which they don't own", auth.getName(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        TabulatedFunctionDto updated = tabulatedFunctionService.updateFunction(id, functionDto);
        logger.info("Function {} updated successfully by {}", id, auth.getName());
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id, Authentication auth) {
        logger.info("User {} attempting to delete tabulated function {}", auth.getName(), id);

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        TabulatedFunctionEntity existing = tabulatedFunctionRepository.findById(id).orElse(null);
        if (existing == null) {
            logger.warn("Function not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = existing.getUserId().equals(
                userRepository.findByLogin(auth.getName()).get().getId()
        );

        if (!isAdmin && !isOwner) {
            logger.warn("User {} tried to delete function {} which they don't own", auth.getName(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        tabulatedFunctionService.deleteFunction(id);
        logger.info("Function {} deleted successfully by {}", id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
