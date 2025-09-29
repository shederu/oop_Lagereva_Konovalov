package ru.ssau.tk._shederu_._lab1_.functions;

import junit.framework.TestCase;

public class MathFunctionsCompositionTest extends TestCase {
    public double delta = 0.001;

    public void testSimpleComposition() {
        MathFunctions idF = new IdentityFunction();
        MathFunctions sqr = new SqrFunction();

        MathFunctions comp = idF.andThen(sqr);

        assertEquals(4.0, comp.apply(2.0), delta);
        assertEquals(10.24, comp.apply(-3.2), delta);
        assertEquals(1115136, comp.apply(-1056), delta);
    }

    public void testDoubleComposition(){
        MathFunctions idF = new IdentityFunction();
        MathFunctions sqr = new SqrFunction();
        MathFunctions uni = new UnitFunction();

        MathFunctions comp = sqr.andThen(idF).andThen(uni);

        assertEquals(1.0, comp.apply(10.0), delta);
        assertEquals(1.0, comp.apply(-5.0), delta);
    }
}