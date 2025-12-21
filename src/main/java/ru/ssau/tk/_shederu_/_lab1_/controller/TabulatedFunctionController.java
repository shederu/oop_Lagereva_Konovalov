package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;
import ru.ssau.tk._shederu_._lab1_.functions.TabulatedFunction;
import ru.ssau.tk._shederu_._lab1_.io.FunctionsIO;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;
import ru.ssau.tk._shederu_._lab1_.service.TabulatedFunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/functions")
public class TabulatedFunctionController {

    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionController.class);

    @Autowired
    private TabulatedFunctionService functionService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'VIEWER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUserFunctions(Authentication auth) {
        Long userId = userRepository.findByLogin(auth.getName()).get().getId();
        List<TabulatedFunctionDto> functions = functionService.getFunctionsByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("functions", functions);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'VIEWER', 'ADMIN')")
    public ResponseEntity<TabulatedFunctionDto> getFunctionById(@PathVariable Long id) {
        TabulatedFunctionDto function = functionService.getFunctionById(id);
        return function != null ? ResponseEntity.ok(function) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        functionService.deleteFunction(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TabulatedFunctionDto>> searchFunctions(@RequestParam String query) {
        return ResponseEntity.ok(functionService.findByName(query));
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadFromFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "Uploaded Function") String name,
            Authentication auth) {

        try {
            TabulatedFunction function;
            try (BufferedInputStream is = new BufferedInputStream(file.getInputStream())) {
                function = FunctionsIO.deserialize(is);
            }

            Long userId = userRepository.findByLogin(auth.getName()).get().getId();
            TabulatedFunctionEntity saved = functionService.saveFunctionToDb(function, name, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("id", saved.getId());
            response.put("name", saved.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Upload error", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('CREATOR', 'VIEWER', 'ADMIN')")
    public ResponseEntity<byte[]> downloadFunction(@PathVariable Long id) {
        try {
            TabulatedFunction function = functionService.loadFunctionFromDb(id);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FunctionsIO.serialize(new BufferedOutputStream(bos), function);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"function_" + id + ".bin\"")
                    .body(bos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<TabulatedFunctionDto> exportFunction(@PathVariable Long id) {
        return getFunctionById(id);
    }
}
