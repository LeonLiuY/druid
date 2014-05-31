package liuyang.druid;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class DruidFileSignalTest extends DruidTestBase {

    @Test
    public void testFileSignal() throws IOException {
        runner.run("var a, b, c; "
                + " a <- println(@file('a.txt') + 'this is a ');"
                + " b = println(@file('a.txt') + 'this is b ');"
                + " c <- println(@file('a.txt') + 'this is c ');");
    }
}
