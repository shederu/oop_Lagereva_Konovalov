package ru.ssau.tk._shederu_._lab1_.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.TabulatedFunctionDao;
import ru.ssau.tk._shederu_._lab1_.config.DbConfig;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/api/tabulated-functions/*")
public class TabulatedFunctionServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private TabulatedFunctionDao functionDao;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSourceProvider dataSourceProvider = new DataSourceProvider(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASSWORD);
        this.functionDao = new TabulatedFunctionDao(dataSourceProvider);
        logger.info("TabulatedFunctionServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String userIdParam = req.getParameter("userId");
                List<TabulatedFunctionEntity> functions;

                if (userIdParam != null) {
                    Long userId = Long.parseLong(userIdParam);
                    logger.debug("GET all tabulated functions for userId: {}", userId);
                    functions = functionDao.findByUserId(userId);
                } else {
                    logger.debug("GET all tabulated functions");
                    functions = functionDao.findAll();
                }

                List<TabulatedFunctionDto> dtos = functions.stream()
                        .map(this::entityToDto)
                        .collect(Collectors.toList());
                resp.getWriter().write(objectMapper.writeValueAsString(dtos));
            } else {
                Long id = extractId(pathInfo);
                logger.debug("GET tabulated function by id: {}", id);
                Optional<TabulatedFunctionEntity> function = functionDao.findById(id);

                if (function.isPresent()) {
                    resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(function.get())));
                } else {
                    logger.warn("Tabulated function not found with id: {}", id);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Function not found\"}");
                }
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format in GET request", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        logger.info("POST /api/tabulated-functions");

        try {
            TabulatedFunctionDto dto = objectMapper.readValue(req.getReader(), TabulatedFunctionDto.class);
            logger.info("Parsed DTO: name={}, userId={}", dto.getName(), dto.getUserId());

            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Name is required\"}");
                return;
            }
            if (dto.getUserId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"UserId is required\"}");
                return;
            }

            if (!dto.hasValidData()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Invalid or missing data (base64 format)\"}");
                return;
            }
            if (!dto.hasValidDerivative()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Invalid or missing derivative (base64 format)\"}");
                return;
            }

            TabulatedFunctionEntity entity = dtoToEntity(dto);
            Long newId = functionDao.create(entity);

            if (newId == null) {
                logger.error("Failed to create function in database");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Database error\"}");
                return;
            }

            entity.setId(newId);
            TabulatedFunctionDto responseDto = entityToDto(entity);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(responseDto));

        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid JSON format: " + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            logger.error("Validation error", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Long id = extractId(pathInfo);
            logger.debug("PUT update tabulated function id: {}", id);

            if (!functionDao.findById(id).isPresent()) {
                logger.warn("Tabulated function not found for update with id: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Function not found\"}");
                return;
            }

            TabulatedFunctionDto dto = objectMapper.readValue(req.getReader(), TabulatedFunctionDto.class);
            functionDao.updateName(id, dto.getName());
            functionDao.updateData(id, dto.getData());
            functionDao.updateDerivative(id, dto.getDerivative());

            Optional<TabulatedFunctionEntity> updated = functionDao.findById(id);
            resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(updated.get())));
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format in PUT request", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Long id = extractId(pathInfo);
            logger.debug("DELETE tabulated function id: {}", id);

            if (functionDao.deleteById(id)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                logger.warn("Tabulated function not found for deletion with id: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Function not found\"}");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format in DELETE request", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
        }
    }

    private Long extractId(String pathInfo) {
        String idStr = pathInfo.substring(1);
        return Long.parseLong(idStr);
    }

    private TabulatedFunctionDto entityToDto(TabulatedFunctionEntity entity) {
        TabulatedFunctionDto dto = new TabulatedFunctionDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUserId(entity.getUserId());
        dto.setData(entity.getData());
        dto.setDerivative(entity.getDerivative());

        return dto;
    }

    private TabulatedFunctionEntity dtoToEntity(TabulatedFunctionDto dto) {
        TabulatedFunctionEntity entity = new TabulatedFunctionEntity();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setUserId(dto.getUserId());
        if (dto.getData() == null) {
            logger.error("Data is null in DTO");
            throw new IllegalArgumentException("Data is required");
        }
        if (dto.getDerivative() == null) {
            logger.error("Derivative is null in DTO");
            throw new IllegalArgumentException("Derivative is required");
        }
        entity.setData(dto.getData());
        entity.setDerivative(dto.getDerivative());
        return entity;
    }
}