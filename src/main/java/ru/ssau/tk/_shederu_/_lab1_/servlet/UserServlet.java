package ru.ssau.tk._shederu_._lab1_.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.RoleDao;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.config.DbConfig;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.service.AuthService;
import ru.ssau.tk._shederu_._lab1_.service.UserService;

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
    private UserService userService;
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSourceProvider dataSourceProvider = new DataSourceProvider(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASSWORD);
        this.userDao = new UserDao(dataSourceProvider);
        RoleDao roleDao = new RoleDao(dataSourceProvider);
        AuthService authService = new AuthService(userDao);
        this.userService = new UserService(userDao, roleDao, authService);
        logger.info("UserServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        UserEntity currentUser = (UserEntity) req.getAttribute("currentUser");
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Authentication required\"}");
            return;
        }

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                if (!currentUser.hasRole("ADMIN")) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    resp.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                    return;
                }

                logger.debug("GET all users");
                List<UserDto> userDtos = userService.getAllUsers();
                resp.getWriter().write(objectMapper.writeValueAsString(userDtos));

            } else {
                Long id = extractId(pathInfo);
                logger.debug("GET user by id: {}", id);

                if (!currentUser.hasRole("ADMIN") && !currentUser.getId().equals(id)) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    resp.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                    return;
                }

                Optional<UserDto> user = userService.getUserById(id);
                if (user.isPresent()) {
                    resp.getWriter().write(objectMapper.writeValueAsString(user.get()));
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

        UserEntity currentUser = (UserEntity) req.getAttribute("currentUser");
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Authentication required\"}");
            return;
        }

        if (!currentUser.hasRole("ADMIN")) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("{\"error\": \"Insufficient permissions\"}");
            return;
        }

        try {
            UserDto userDto = objectMapper.readValue(req.getReader(), UserDto.class);
            logger.debug("POST create user with login: {}", userDto.getLogin());

            if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Password is required\"}");
                return;
            }

            UserDto createdUser = userService.createUser(userDto, userDto.getPassword());
            if (createdUser != null) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(objectMapper.writeValueAsString(createdUser));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Failed to create user\"}");
            }
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

        UserEntity currentUser = (UserEntity) req.getAttribute("currentUser");
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Authentication required\"}");
            return;
        }

        try {
            Long id = extractId(pathInfo);
            logger.debug("PUT update user id: {}", id);

            if (!currentUser.hasRole("ADMIN") && !currentUser.getId().equals(id)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                return;
            }

            UserDto userDto = objectMapper.readValue(req.getReader(), UserDto.class);
            userDto.setId(id);

            UserDto updatedUser = userService.updateUser(id, userDto);
            if (updatedUser != null) {
                resp.getWriter().write(objectMapper.writeValueAsString(updatedUser));
            } else {
                logger.warn("User not found for update with id: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"User not found\"}");
            }
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

        UserEntity currentUser = (UserEntity) req.getAttribute("currentUser");
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Authentication required\"}");
            return;
        }

        try {
            Long id = extractId(pathInfo);
            logger.debug("DELETE user id: {}", id);

            if (!currentUser.hasRole("ADMIN")) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                return;
            }

            if (currentUser.getId().equals(id)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Cannot delete yourself\"}");
                return;
            }

            if (userService.deleteUser(id)) {
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
}