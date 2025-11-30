package ru.ssau.tk._shederu_._lab1_.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._shederu_._lab1_.Dao.DataSourceProvider;
import ru.ssau.tk._shederu_._lab1_.Dao.TabulatedFunctionDao;
import ru.ssau.tk._shederu_._lab1_.config.DbConfig;
import ru.ssau.tk._shederu_._lab1_.dto.TabulatedFunctionDto;
import ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/api/tabulated-functions/*")
public class TabulatedFunctionServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private TabulatedFunctionDao functionDao;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSourceProvider dataSourceProvider = new DataSourceProvider(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASSWORD
        );
        this.functionDao = new TabulatedFunctionDao(dataSourceProvider);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String userIdParam = req.getParameter("userId");
                List<TabulatedFunctionEntity> functions;

                if (userIdParam != null) {
                    Long userId = Long.parseLong(userIdParam);
                    functions = functionDao.findByUserId(userId);
                } else {
                    functions = functionDao.findAll();
                }

                List<TabulatedFunctionDto> dtos = functions.stream()
                        .map(this::entityToDto)
                        .collect(Collectors.toList());
                resp.getWriter().write(objectMapper.writeValueAsString(dtos));
            } else {
                Long id = extractId(pathInfo);
                Optional<TabulatedFunctionEntity> function = functionDao.findById(id);

                if (function.isPresent()) {
                    resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(function.get())));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Function not found\"}");
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            TabulatedFunctionDto dto = objectMapper.readValue(req.getReader(), TabulatedFunctionDto.class);
            TabulatedFunctionEntity entity = dtoToEntity(dto);

            Long newId = functionDao.create(entity);
            entity.setId(newId);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(entityToDto(entity)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid request body\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Long id = extractId(pathInfo);

            if (!functionDao.findById(id).isPresent()) {
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
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Long id = extractId(pathInfo);

            if (functionDao.deleteById(id)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Function not found\"}");
            }
        } catch (NumberFormatException e) {
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
        dto.setData(entity.getData());
        dto.setDerivative(entity.getDerivative());
        dto.setUserId(entity.getUserId());
        return dto;
    }

    private TabulatedFunctionEntity dtoToEntity(TabulatedFunctionDto dto) {
        TabulatedFunctionEntity entity = new TabulatedFunctionEntity();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setData(dto.getData());
        entity.setDerivative(dto.getDerivative());
        entity.setUserId(dto.getUserId());
        return entity;
    }
}
