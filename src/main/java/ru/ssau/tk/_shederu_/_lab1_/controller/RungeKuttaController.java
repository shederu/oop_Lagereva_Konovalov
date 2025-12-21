package ru.ssau.tk._shederu_._lab1_.controller;

import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._shederu_._lab1_.functions.*;
import ru.ssau.tk._shederu_._lab1_.operations.*;
import ru.ssau.tk._shederu_._lab1_.functions.factory.*;
import java.util.*;

@RestController
@RequestMapping("/api/operations/runge-kutta")
public class RungeKuttaController {

    private final TabulatedFunctionOperationService operationService;

    public RungeKuttaController() {
        this.operationService = new TabulatedFunctionOperationService(
                new ArrayTabulatedFunctionFactory()
        );
    }

    @PostMapping("/solve")
    public Map<String, Object> solveODE(@RequestBody OdeSolverRequest request) {
        try {
            MathFunctions f = getFunctionByType(request.getFunctionType());

            TabulatedFunction result = operationService.solveODE(
                    f,
                    request.getX0(),
                    request.getY0(),
                    request.getXEnd(),
                    request.getStep()
            );

            double[] xValues = new double[result.getCount()];
            double[] yValues = new double[result.getCount()];

            int i = 0;
            for (Point p : result) {
                xValues[i] = p.x;
                yValues[i] = p.y;
                i++;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("x", xValues);
            response.put("y", yValues);
            response.put("count", result.getCount());
            response.put("status", "success");

            return response;

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }

    @PostMapping("/solve-with-points")
    public Map<String, Object> solveODEWithPoints(@RequestBody OdeSolverPointsRequest request) {
        try {
            MathFunctions f = getFunctionByType(request.getFunctionType());

            TabulatedFunction result = operationService.solveODEWithPoints(
                    f,
                    request.getX0(),
                    request.getY0(),
                    request.getXEnd(),
                    request.getPointCount()
            );

            double[] xValues = new double[result.getCount()];
            double[] yValues = new double[result.getCount()];

            int i = 0;
            for (Point p : result) {
                xValues[i] = p.x;
                yValues[i] = p.y;
                i++;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("x", xValues);
            response.put("y", yValues);
            response.put("count", result.getCount());
            response.put("status", "success");

            return response;

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }

    @PostMapping("/integrate-derivative")
    public Map<String, Object> integrateTabulatedDerivative(@RequestBody IntegrationRequest request) {
        try {
            TabulatedFunction derivativeFunc = new ArrayTabulatedFunction(
                    request.getX(),
                    request.getY()
            );

            TabulatedFunction result = operationService.integrateFunction(
                    derivativeFunc,
                    request.getY0()
            );

            double[] xValues = new double[result.getCount()];
            double[] yValues = new double[result.getCount()];

            int i = 0;
            for (Point p : result) {
                xValues[i] = p.x;
                yValues[i] = p.y;
                i++;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("x", xValues);
            response.put("y", yValues);
            response.put("count", result.getCount());
            response.put("status", "success");

            return response;

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }

    private MathFunctions getFunctionByType(String functionType) {
        // Предполагаем, что функции перечислены в enum или классе MathFunctions
        switch (functionType.toUpperCase()) {
            case "UNIT":
                return new UnitFunction();
            case "IDENTITY":
                return new IdentityFunction();
            case "SQR":
                return new SqrFunction();
            case "ZERO":
                return new ZeroFunction();
            default:
                throw new IllegalArgumentException("Неизвестный тип функции: " + functionType);
        }
    }

    public static class OdeSolverRequest {
        private String functionType;
        private double x0;
        private double y0;
        private double xEnd;
        private double step;

        public String getFunctionType() { return functionType; }
        public void setFunctionType(String functionType) { this.functionType = functionType; }

        public double getX0() { return x0; }
        public void setX0(double x0) { this.x0 = x0; }

        public double getY0() { return y0; }
        public void setY0(double y0) { this.y0 = y0; }

        public double getXEnd() { return xEnd; }
        public void setXEnd(double xEnd) { this.xEnd = xEnd; }

        public double getStep() { return step; }
        public void setStep(double step) { this.step = step; }
    }

    public static class OdeSolverPointsRequest {
        private String functionType;
        private double x0;
        private double y0;
        private double xEnd;
        private int pointCount;

        public String getFunctionType() { return functionType; }
        public void setFunctionType(String functionType) { this.functionType = functionType; }

        public double getX0() { return x0; }
        public void setX0(double x0) { this.x0 = x0; }

        public double getY0() { return y0; }
        public void setY0(double y0) { this.y0 = y0; }

        public double getXEnd() { return xEnd; }
        public void setXEnd(double xEnd) { this.xEnd = xEnd; }

        public int getPointCount() { return pointCount; }
        public void setPointCount(int pointCount) { this.pointCount = pointCount; }
    }

    public static class IntegrationRequest {
        private double[] x;
        private double[] y;
        private double y0;

        public double[] getX() { return x; }
        public void setX(double[] x) { this.x = x; }

        public double[] getY() { return y; }
        public void setY(double[] y) { this.y = y; }

        public double getY0() { return y0; }
        public void setY0(double y0) { this.y0 = y0; }
    }
}