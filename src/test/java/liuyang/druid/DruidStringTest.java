package liuyang.druid;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class DruidStringTest extends DruidTestBase {
    @Test
    public void testString() throws IOException {
        Map<String, Object> result = runner.run("var a, b, c;"
                + " a = 'a';"
                + " b = \"a\";"
                + " c = a + b;");
        assertEquals("a", result.get("a"));
        assertEquals("a", result.get("b"));
        assertEquals("aa", result.get("c"));
    }
}
