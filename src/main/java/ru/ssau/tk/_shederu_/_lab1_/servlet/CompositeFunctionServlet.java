package ru.ssau.tk._shederu_._lab1_.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._shederu_._lab1_.Dao.CompositeFunctionDao;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.config.DbConfig;
import ru.ssau.tk._shederu_._lab1_.dto.CompositeFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity;

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

@WebServlet("/api/composite-functions/*")
public class CompositeFunctionServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CompositeFunctionServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CompositeFunctionDao compositeDao;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSourceProvider dataSourceProvider = new DataSourceProvider(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASSWORD);
        this.compositeDao = new CompositeFunctionDao(dataSourceProvider);
        logger.info("CompositeFunctionServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String userIdParam = req.getParameter("userId");
                String patternParam = req.getParameter("pattern");
                List<CompositeFunctionEntity> functions;

                if (userIdParam != null && patternParam != null) {
                    Long userId = Long.parseLong(userIdParam);
                    logger.debug("GET composite functions for userId: {} with pattern: {}", userId, patternParam);
                    functions = compositeDao.findByExpressionContainingAndUserId(patternParam, userId);
                } else if (userIdParam != null) {
                    Long userId = Long.parseLong(userIdParam);
                    logger.debug("GET all composite functions for userId: {}", userId);
                    functions = compositeDao.findByUserId(userId);
                } else if (patternParam != null) {
                    logger.debug("GET composite functions with pattern: {}", patternParam);
                    functions = compositeDao.findByExpressionContaining(patternParam);
                } else {
                    logger.debug("GET all composite functions");
                    functions = compositeDao.findAll();
                }

                List<CompositeFunctionDto> dtos = functions.stream()
                        .map(this::entityToDto)
                        .collect(Collectors.toList());
                resp.getWriter().write(objectMapper.writeValueAsString(dtos));
            } else {
                Long id = extractId(pathInfo);
                logger.debug("GET composite function by id: {}", id);
                Optional<CompositeFunctionEntity> function = compositeDao.findById(id);

                if (function.isPresent()) {
                    resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(function.get())));
                } else {
                    logger.warn("Composite function not found with id: {}", id);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Composite function not found\"}");
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

        try {
            CompositeFunctionDto dto = objectMapper.readValue(req.getReader(), CompositeFunctionDto.class);
            logger.debug("POST create composite function for userId: {}", dto.getUserId());

            CompositeFunctionEntity entity = dtoToEntity(dto);
            Long newId = compositeDao.create(entity);
            entity.setId(newId);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(entity)));
        } catch (Exception e) {
            logger.error("Error creating composite function", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid request body\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Long id = extractId(pathInfo);
            logger.debug("PUT update composite function id: {}", id);

            if (!compositeDao.findById(id).isPresent()) {
                logger.warn("Composite function not found for update with id: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Composite function not found\"}");
                return;
            }

            CompositeFunctionDto dto = objectMapper.readValue(req.getReader(), CompositeFunctionDto.class);
            compositeDao.updateExpression(id, dto.getExpression());

            Optional<CompositeFunctionEntity> updated = compositeDao.findById(id);
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
            logger.debug("DELETE composite function id: {}", id);

            if (compositeDao.deleteById(id)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                logger.warn("Composite function not found for deletion with id: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Composite function not found\"}");
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

    private CompositeFunctionDto entityToDto(CompositeFunctionEntity entity) {
        CompositeFunctionDto dto = new CompositeFunctionDto();
        dto.setId(entity.getId());
        dto.setExpression(entity.getExpression());
        dto.setUserId(entity.getUserId());
        return dto;
    }

    private CompositeFunctionEntity dtoToEntity(CompositeFunctionDto dto) {
        CompositeFunctionEntity entity = new CompositeFunctionEntity();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setExpression(dto.getExpression());
        entity.setUserId(dto.getUserId());
        return entity;
    }
}