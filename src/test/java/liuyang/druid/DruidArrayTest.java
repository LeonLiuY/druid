package liuyang.druid;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DruidArrayTest extends DruidTestBase {
    @Test
    public void testArray() throws IOException {
        Map<String, Object> result = runner.run("var a, b, c; "
                + " a = [1,2,3];" + " b = [4,5,6];" + " c = a + b - [1,3,5];");
        assertEquals(Arrays.asList(1, 2, 3), result.get("a"));
        assertEquals(Arrays.asList(4, 5, 6), result.get("b"));
        assertEquals(Arrays.asList(2, 4, 6), result.get("c"));
    }

    @Test
    public void testArrayWithExtend() throws IOException {
        Map<String, Object> result = runner.run("var a, b, c; "
                + " a = [1,2,3];" + " b = [4,5,6];"
                + " c <- a + b - [1,3,5,7,9];" + " a = a + [7,8,9];");
        assertEquals(Arrays.asList(1, 2, 3, 7, 8, 9), result.get("a"));
        assertEquals(Arrays.asList(4, 5, 6), result.get("b"));
        assertEquals(Arrays.asList(2, 8, 4, 6), result.get("c"));
    }
    
    @Test
    public void testArrayCall() throws IOException {
        Map<String, Object> result = runner.run("var a, b, c; "
                + " a = [1,2,3];" + " b <- a[1+1];"
                + " c <- b + 2;" + " a = [2,3,4];");
        assertEquals(Arrays.asList(2,3,4), result.get("a"));
        assertEquals(new Integer(4), result.get("b"));
        assertEquals(new Integer(6), result.get("c"));
    }
}
