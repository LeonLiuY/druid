package liuyang.druid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import liuyang.druid.DruidParser.DruidContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class DruidRunner {

    Map<String, Integer> run(String code) throws IOException {
        DruidLexer lexer = new DruidLexer(new ANTLRInputStream(new ByteArrayInputStream(code.getBytes())));
        DruidParser parser = new DruidParser(new CommonTokenStream(lexer));
        DruidContext context = parser.druid();
        if (parser.getNumberOfSyntaxErrors() != 0){
            throw new IllegalArgumentException("Input string contains syntax errors!");
        }
        MyDruidListener listener = new MyDruidListener();
        ParseTreeWalker.DEFAULT.walk(listener, context);
        return listener.getValues();
    }
}
