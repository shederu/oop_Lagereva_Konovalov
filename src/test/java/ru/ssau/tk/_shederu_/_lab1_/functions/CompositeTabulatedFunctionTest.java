package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompositeTabulatedFunctionTest {
    private final double delta = 0.001;

    @Test
    public void testArrayAndArrayComposition() {
        double[] x1 = {3, 5, 8, 12};
        double[] y1 = {-5, 2, 4, 8};
        ArrayTabulatedFunction inner = new ArrayTabulatedFunction(x1, y1);

        double[] x2 = {-15, 4, 5, 6};
        double[] y2 = {9, 13, 6, 12};
        ArrayTabulatedFunction outer = new ArrayTabulatedFunction(x2, y2);

        MathFunctions composition = inner.andThen(outer);

        assertEquals(6.536, composition.apply(-3.2), delta);
        assertEquals(24.0, composition.apply(12), delta);
        assertEquals(612.0, composition.apply(110), delta);
        assertEquals(-7.315, composition.apply(-22), delta);
    }

    @Test
    public void testTabulatedAndMathFunctionComposition() {
        double[] x = {1, 2, 3, 4};
        double[] y = {6, 3, 2, 0};
        ArrayTabulatedFunction tabulated = new ArrayTabulatedFunction(x, y);

        MathFunctions sqrtFunction = new SqrFunction();

        MathFunctions composition = tabulated.andThen(sqrtFunction);

        // sqrt(x^2) = |x|, но в нашем случае x положительный
        assertEquals(36.0, composition.apply(1.0), delta);
        assertEquals(9.0, composition.apply(2.0), delta);
        assertEquals(4.0, composition.apply(3.0), delta);
    }
}
