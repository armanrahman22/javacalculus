package javacalculus.test;

import junit.framework.Assert;

import org.junit.Test;

public class CalcDIFFTest extends BaseTest {

    @Test
    public void testQuadratic() {
        final String QUERY = "DIFF(x^2,x)";
        final String EXPECTED = "2*x";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }

    @Test
    public void testQuadraticPlusConst() {
        final String QUERY = "DIFF(x^2 + 5,x)";
        final String EXPECTED = "2*x";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }

    @Test
    public void testBinomial() {
        final String QUERY = "DIFF((x+1)^2 + 5,x)";
        final String EXPECTED = "2*(1+x)";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }

    @Test
    public void testTrig1() {
        final String QUERY = "DIFF(SIN(x),x)";
        final String EXPECTED = "COS(x)";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }

    @Test
    public void testTrig2() {
        final String QUERY = "DIFF(COS(x),x)";
        final String EXPECTED = "-SIN(x)";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }
}
