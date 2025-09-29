package ru.ssau.tk._shederu_._lab1_.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class IdentityFunctionTest{
    private IdentityFunction test;

    @BeforeEach
    void setUp() {
        test = new IdentityFunction();
    }

    @AfterEach
    void tearDown() {
        test = null;
    }

    @Test
    public void testApply() {
        assertEquals(1.0, test.apply(1.0));
        assertEquals(9007199254740992.0, test.apply(9007199254740992.0));
        assertEquals( 4.9E-324, test.apply(4.9E-324));

    }
}
