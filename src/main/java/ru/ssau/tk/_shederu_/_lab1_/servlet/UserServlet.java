package ru.ssau.tk._shederu_._lab1_.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.config.DbConfig;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;

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

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSourceProvider dataSourceProvider = new DataSourceProvider(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASSWORD);
        this.userDao = new UserDao(dataSourceProvider);
        logger.info("UserServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.debug("GET all users");
                List<UserEntity> users = userDao.findAll();
                List<UserDto> userDtos = users.stream().map(this::entityToDto).collect(Collectors.toList());
                resp.getWriter().write(objectMapper.writeValueAsString(userDtos));
            } else {
                Long id = extractId(pathInfo);
                logger.debug("GET user by id: {}", id);
                Optional<UserEntity> user = userDao.findById(id);

                if (user.isPresent()) {
                    resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(user.get())));
                } else {
                    logger.warn("User not found with id: {}", id);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"User not found\"}");
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
            UserDto userDto = objectMapper.readValue(req.getReader(), UserDto.class);
            logger.debug("POST create user with login: {}", userDto.getLogin());

            UserEntity userEntity = dtoToEntity(userDto);
            UserEntity saved = userDao.save(userEntity);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(saved)));
        } catch (Exception e) {
            logger.error("Error creating user", e);
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
            logger.debug("PUT update user id: {}", id);

            if (!userDao.findById(id).isPresent()) {
                logger.warn("User not found for update with id: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"User not found\"}");
                return;
            }

            UserDto userDto = objectMapper.readValue(req.getReader(), UserDto.class);
            userDto.setId(id);
            UserEntity userEntity = dtoToEntity(userDto);

            UserEntity saved = userDao.save(userEntity);
            resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(saved)));
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
            logger.debug("DELETE user id: {}", id);

            if (userDao.deleteById(id)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                logger.warn("User not found for deletion with id: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"User not found\"}");
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

    private UserDto entityToDto(UserEntity entity) {
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setLogin(entity.getLogin());
        return dto;
    }

    private UserEntity dtoToEntity(UserDto dto) {
        UserEntity entity = new UserEntity();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setLogin(dto.getLogin());
        return entity;
    }
}