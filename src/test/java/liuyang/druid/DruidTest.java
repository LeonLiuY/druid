package liuyang.druid;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DruidTest {

    private DruidRunner runner = new DruidRunner();

    @Test
    public void druidTest() throws IOException {
        Map<String, Integer> result = runner.run(" var a;"
                + " var b;"
                + " var c;"
                + " var d;"
                + " a = 1;"
                + " b = a;"
                + " c <- a;"
                + " a = 2;");
        System.out.println(result);
        assertEquals(new Integer(2), result.get("a"));
        assertEquals(new Integer(1), result.get("b"));
        assertEquals(new Integer(2), result.get("c"));
        assertEquals(null, result.get("d"));

        result = runner.run("var a, b ,c;"
                + " a = 1;"
                + " b = 2;"
                + " c <- a + b;"
                + " a = 3;"
                + " b = 4;");
        System.out.println(result);
        assertEquals(new Integer(3), result.get("a"));
        assertEquals(new Integer(4), result.get("b"));
        assertEquals(new Integer(7), result.get("c"));
    }

    @Test
    public void testNotInitialized() throws IOException {
        try {
            runner.run("var a, b; "
                    + " a = b;"
                    + " b = 1;");
        } catch (IllegalStateException e) {
            assertEquals("variable b is not initialized!", e.getMessage());
        }
    }

    @Test
    public void testMultiDefinition() throws IOException {
        Map<String, Integer> result = runner.run(" var a, b;"
                + " a = 1;"
                + " b = a;"
                + " var c, d;"
                + " c <- a;"
                + " a = 2;");
        System.out.println(result);
        assertEquals(new Integer(2), result.get("a"));
        assertEquals(new Integer(1), result.get("b"));
        assertEquals(new Integer(2), result.get("c"));
        assertEquals(null, result.get("d"));

        try {
            runner.run(" var a;"
                    + " b = 1;"
                    + " var b;"
                    + " a = 2;");
        } catch (IllegalStateException e) {
            assertEquals("variable b not defined!", e.getMessage());
            ;
        }
    }

    @Test
    public void druidTestWithCycle() throws IOException {
        try {
            runner.run("var a; var b; b = 1; a <- b; b <- a;");
        } catch (IllegalStateException e) {
            assertEquals("cycle dependency found!", e.getMessage());
        }
    }

    @Test
    public void testExpr() throws IOException {
        Map<String, Integer> result = runner.run(" var a, b, c, d;"
                + " a = 1 + 2 - 3;"
                + " b = 12 / 3 * 2;"
                + " c = 12 / (3 * 2);"
                + " d = a - (b + c);");
        System.out.println(result);
        assertEquals(new Integer(0), result.get("a"));
        assertEquals(new Integer(8), result.get("b"));
        assertEquals(new Integer(2), result.get("c"));
        assertEquals(new Integer(-10), result.get("d"));
    }

    @Test
    public void testExprExtend() throws IOException {
        Map<String, Integer> result = runner.run(" var a, b, c, d;"
                + " a = 1;"
                + " b = 2;"
                + " c <-  a + b;"
                + " d <- c * 2 - (a * b);"
                + " a = 3; b <- a + 1; a = 2;");
        System.out.println(result);
        assertEquals(new Integer(2), result.get("a"));
        assertEquals(new Integer(3), result.get("b"));
        assertEquals(new Integer(5), result.get("c"));
        assertEquals(new Integer(4), result.get("d"));
    }
}
