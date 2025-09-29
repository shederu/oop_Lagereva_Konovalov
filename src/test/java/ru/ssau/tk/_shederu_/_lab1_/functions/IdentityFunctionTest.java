package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IdentityFunctionTest extends TestCase {
    private IdentityFunction test;
    private final double delta = 1e-323;

    public void setUp() throws Exception {
        super.setUp();
        test = new IdentityFunction();
    }

    public void tearDown() throws Exception {
        test = null;
        super.tearDown();
    }

    public void testApply() {
        assertEquals(1.0, test.apply(1.0), delta);
        assertEquals(1.7976931348623157E308, test.apply(1.7976931348623157E308), delta);
        assertEquals(4.9E-324, test.apply(4.9E-324), delta);
        assertEquals(0.0, test.apply(0.0), delta);
        assertEquals(-1.7976931348623157E308, test.apply(-1.7976931348623157E308), delta);
        assertEquals(-4.9E-324, test.apply(-4.9E-324), delta);
    }
}