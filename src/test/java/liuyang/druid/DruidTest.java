package liuyang.druid;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DruidTest {

    private DruidRunner runner = new DruidRunner();

    @Test
    public void druidTest() throws IOException {
        Map<String, Integer> result = runner.run("var a; var b; var c; var d; a = 1; b = a; c <- a; a = 2;");
        System.out.println(result);
        assertEquals(new Integer(2), result.get("a"));
        assertEquals(new Integer(1), result.get("b"));
        assertEquals(new Integer(2), result.get("c"));
        assertEquals(null, result.get("d"));
    }

    @Test
    public void druidTestWithCycle() throws IOException {
        try {
            runner.run("var a; var b; a <- b; b <- a;");
        } catch (IllegalStateException e) {
            assertEquals("cycle dependency found!", e.getMessage());
        }
    }
}
