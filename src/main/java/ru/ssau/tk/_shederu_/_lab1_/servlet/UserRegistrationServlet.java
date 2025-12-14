package ru.ssau.tk._shederu_._lab1_.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._shederu_._lab1_.config.DbConfig;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.RoleDao;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.dto.UserRegistrationDto;
import ru.ssau.tk._shederu_._lab1_.service.AuthService;
import ru.ssau.tk._shederu_._lab1_.service.UserService;

import java.io.IOException;

@WebServlet("/api/users/register")
public class UserRegistrationServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserService userService;

    @Override
    public void init() {
        DataSourceProvider dataSourceProvider = new DataSourceProvider(
                DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASSWORD);
        UserDao userDao = new UserDao(dataSourceProvider);
        RoleDao roleDao = new RoleDao(dataSourceProvider);
        AuthService authService = new AuthService(userDao);
        this.userService = new UserService(userDao, roleDao, authService);
        logger.info("UserRegistrationServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            UserRegistrationDto dto = objectMapper.readValue(req.getReader(), UserRegistrationDto.class);
            logger.info("Регистрация пользователя: {}", dto.getLogin());

            if (dto.getLogin() == null || dto.getLogin().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Login is required\"}");
                return;
            }

            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Password is required\"}");
                return;
            }

            var userDto = userService.registerUser(dto);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(userDto));

        } catch (RuntimeException e) {
            logger.error("Ошибка при регистрации пользователя", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при регистрации", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}