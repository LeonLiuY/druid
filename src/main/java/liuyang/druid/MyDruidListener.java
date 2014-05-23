package liuyang.druid;

import java.util.HashMap;
import java.util.Map;

import liuyang.druid.DruidParser.AssignContext;
import liuyang.druid.DruidParser.DefineContext;
import liuyang.druid.DruidParser.ExtendContext;
import liuyang.druid.DruidParser.ValueContext;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class MyDruidListener extends DruidBaseListener {

    private Map<String, Integer> values = new HashMap<>();

    private DirectedGraph<String, DefaultEdge> dependencyGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

    public Map<String, Integer> getValues() {
        return values;
    }

    @Override
    public void enterDefine(DefineContext ctx) {
        String name = ctx.ID().getText();
        if (values.containsKey(name)) {
            throw new IllegalStateException("variable "
                    + name
                    + " already defined");
        } else {
            values.put(name, null);
        }
    }

    @Override
    public void enterAssign(AssignContext ctx) {
        String name = ctx.ID().getText();
        if (!values.containsKey(name)) {
            throw new IllegalStateException("variable "
                    + name
                    + " not defined");
        } else {
            Integer value = val(ctx.value());
            values.put(name, value);
            trigger(name, value);
        }
    }

    private void trigger(String name, Integer value) {
        if (dependencyGraph.containsVertex(name)) {
            for (DefaultEdge edge : dependencyGraph.outgoingEdgesOf(name)) {
                String target = dependencyGraph.getEdgeTarget(edge);
                values.put(target, value);
                trigger(target, value);
            }
        }
    }

    private Integer val(ValueContext value) {
        if (value.ID() != null) {
            String name = value.ID().getText();
            if (!values.containsKey(name)) {
                throw new IllegalStateException("variable "
                        + name
                        + " not defined");
            } else {
                return values.get(name);
            }
        } else {
            return new Integer(value.INT().getText());
        }
    }

    @Override
    public void enterExtend(ExtendContext ctx) {
        String name = ctx.ID().get(0).getText();
        String from = ctx.ID().get(1).getText();
        dependencyGraph.addVertex(name);
        dependencyGraph.addVertex(from);
        dependencyGraph.addEdge(from, name);
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(dependencyGraph);
        if (cycleDetector.detectCycles()) {
            throw new IllegalStateException("cycle dependency found!");
        }
    }

}
