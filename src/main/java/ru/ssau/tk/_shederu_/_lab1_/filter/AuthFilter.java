package ru.ssau.tk._shederu_._lab1_.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk._shederu_._lab1_.config.DbConfig;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.UserDao;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.service.AuthService;
import ru.ssau.tk._shederu_._lab1_.service.UserService;

import java.io.IOException;
import java.util.Base64;

@WebFilter("/api/*")
public class AuthFilter implements Filter {

    private AuthService authService;
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        DataSourceProvider dataSourceProvider = new DataSourceProvider(
                DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASSWORD);
        UserDao userDao = new UserDao(dataSourceProvider);
        this.authService = new AuthService(userDao);
        this.userService = new UserService(userDao, null, authService);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();
        String method = req.getMethod();

        if (path.equals("/api/users/register") && method.equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            sendUnauthorized(resp, "Missing or invalid Authorization header");
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            credentials = new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            sendUnauthorized(resp, "Invalid Base64 encoding");
            return;
        }

        String[] values = credentials.split(":", 2);
        if (values.length != 2) {
            sendUnauthorized(resp, "Invalid credentials format");
            return;
        }

        String login = values[0];
        String password = values[1];

        UserEntity user = authService.authenticate(login, password);
        if (user == null) {
            sendUnauthorized(resp, "Invalid username or password");
            return;
        }

        if (!hasAccess(user, path, method)) {
            sendForbidden(resp, "Insufficient permissions");
            return;
        }

        req.setAttribute("currentUser", user);
        chain.doFilter(request, response);
    }

    private boolean hasAccess(UserEntity user, String path, String method) {
        if (user.hasRole("ADMIN")) {
            return true;
        }

        if (user.hasRole("VIEWER")) {
            return method.equals("GET");
        }

        if (user.hasRole("CREATOR")) {
            return true;
        }

        return false;
    }

    private void sendUnauthorized(HttpServletResponse resp, String message)
            throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private void sendForbidden(HttpServletResponse resp, String message)
            throws IOException {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    @Override
    public void destroy() {}
}