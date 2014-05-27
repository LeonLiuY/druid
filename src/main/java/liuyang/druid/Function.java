package liuyang.druid;

import java.util.HashMap;
import java.util.Map;

public abstract class Function {

    private final int paramSize;

    private final String name;

    public Function(int paramSize, String name) {
        this.paramSize = paramSize;
        this.name = name;
    }

    Object run(Object[] params) {
        checkParamSize(params);
        return execute(params);
    }

    protected abstract Object execute(Object[] params);

    void checkParamSize(Object[] params) {
        int actualSize;
        if (params == null) {
            actualSize = 0;
        } else {
            actualSize = params.length;
        }
        if (paramSize != actualSize) {
            throw new IllegalStateException("function '"
                    + name
                    + "' params number not matched, expected "
                    + paramSize
                    + " but was "
                    + actualSize
                    + "!");
        }
    }

    public static final Map<String, Function> BUILT_IN_FUNCTIONS = new HashMap<>();
    static {
        BUILT_IN_FUNCTIONS.put("print", new Function(1, "print") {

            @Override
            protected Object execute(Object[] params) {
                System.out.print(params[0]);
                return params[0];
            }
        });
        BUILT_IN_FUNCTIONS.put("println", new Function(1, "println") {

            @Override
            protected Object execute(Object[] params) {
                System.out.println(params[0]);
                return params[0];
            }
        });
    }
}
