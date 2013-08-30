package javacalculus.test;

import junit.framework.Assert;

import org.junit.Test;

public class CalcINTTest extends BaseTest {

    @Test
    public void testQuadratic() {
        final String QUERY = "INT(x^2,x)";
        final String EXPECTED = "0.3333333*x^3";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }

    @Test
    public void testQuadraticPlusConst() {
        final String QUERY = "INT(x^2 + 5,x)";
        final String EXPECTED = "5*x+0.3333333*x^3";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }

    @Test
    public void testBinomial() {
        final String QUERY = "INT((x+1)^2,x)";
        final String EXPECTED = "0.3333333*(1+x)^3";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }

    @Test
    public void testTrig1() {
        final String QUERY = "INT(SIN(x),x)";
        final String EXPECTED = "-COS(x)";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }

    @Test
    public void testTrig2() {
        final String QUERY = "INT(COS(x),x)";
        final String EXPECTED = "SIN(x)";
        String result = engine.execute(QUERY);

        Assert.assertEquals(EXPECTED, result);
    }
}
