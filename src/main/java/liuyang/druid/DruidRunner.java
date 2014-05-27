package liuyang.druid;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import liuyang.druid.DruidParser.DruidContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class DruidRunner {

    Map<String, Object> run(String code) throws IOException {
        return run(new ByteArrayInputStream(code.getBytes()));
    }

    Map<String, Object> run(InputStream code) throws IOException {
        DruidLexer lexer = new DruidLexer(new ANTLRInputStream(code));
        DruidParser parser = new DruidParser(new CommonTokenStream(lexer));
        DruidContext context = parser.druid();
        if (parser.getNumberOfSyntaxErrors() != 0) {
            throw new IllegalArgumentException("Input string contains syntax errors!");
        }
        FunctionScanner functionScanner = new FunctionScanner();
        ParseTreeWalker.DEFAULT.walk(functionScanner, context);

        DruidInterpreter interpreter = new DruidInterpreter(functionScanner.getFunctions());
        interpreter.visitDruid(context);
        return interpreter.getValues();
    }

    public static void main(String[] args) throws IOException {
        List<String> argList = Arrays.asList(args);
        if (argList.size() == 0) {
            exitError();
        }
        if (argList.get(0).equals("run")) {
            if (argList.size() != 2) {
                exitError();
            }
            String fileName = argList.get(1);
            if (fileName == null) {
                exitError();
            } else {
                FileInputStream file;
                file = new FileInputStream(fileName);
                new DruidRunner().run(file);
                file.close();
            }
        } else {
            exitError();
        }
    }

    private static void exitError() {
        System.out.println(String.format("Druid v%s: ", DruidRunner.class.getPackage().getImplementationVersion()));
        System.out.println("Usage: ");
        System.out.println("\trun [file]");
        System.exit(1);
    }
}
