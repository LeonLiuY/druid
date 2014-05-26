package liuyang.druid;

import java.util.HashMap;
import java.util.Map;

import liuyang.druid.DruidParser.FunctionContext;

public class FunctionScanner extends DruidBaseListener {

    private Map<String, FunctionContext> functions = new HashMap<>();

    public Map<String, FunctionContext> getFunctions() {
        return functions;
    }

    @Override
    public void enterFunction(FunctionContext ctx) {
        String name = ctx.name.getText();
        if (functions.containsKey(name)) {
            throw new IllegalStateException("function "
                    + name
                    + " already defined!");
        }
        functions.put(name, ctx);
    }
}
