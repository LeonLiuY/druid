package liuyang.druid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import liuyang.druid.DruidParser.AssignContext;
import liuyang.druid.DruidParser.DefineContext;
import liuyang.druid.DruidParser.ExprContext;
import liuyang.druid.DruidParser.ExtendContext;
import liuyang.druid.DruidParser.NegExprContext;
import liuyang.druid.DruidParser.OpExprContext;
import liuyang.druid.DruidParser.ParenExprContext;
import liuyang.druid.DruidParser.ValueExprContext;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;

public class MyDruidListener extends DruidBaseListener {

    private Map<String, Integer> values = new HashMap<>();

    private class DependencyEdge {
        ExprContext exprContext;

        public DependencyEdge(ExprContext exprContext) {
            this.exprContext = exprContext;
        }
        
    }
    private DirectedGraph<String, DependencyEdge> dependencyGraph = new DefaultDirectedGraph<>(
            DependencyEdge.class);

    public Map<String, Integer> getValues() {
        return values;
    }

    @Override
    public void enterDefine(DefineContext ctx) {
        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            if (values.containsKey(name)) {
                throw new IllegalStateException("variable " + name
                        + " already defined!");
            } else {
                values.put(name, null);
            }
        }

    }

    @Override
    public void enterAssign(AssignContext ctx) {
        String name = ctx.ID().getText();
        if (!values.containsKey(name)) {
            throw new IllegalStateException("variable " + name
                    + " not defined!");
        } else {
            Integer value = val(ctx.expr());
            values.put(name, value);
            trigger(name);
        }
    }

    private void trigger(String name) {
        if (dependencyGraph.containsVertex(name)) {
            for (DependencyEdge edge : dependencyGraph.outgoingEdgesOf(name)) {
                String target = dependencyGraph.getEdgeTarget(edge);
                values.put(target, val(edge.exprContext));
                trigger(target);
            }
        }
    }

    private Set<String> referredVariables = new HashSet<>();

    private Integer val(ExprContext value) {
        if (value instanceof ValueExprContext) {
            ValueExprContext valueExprContext = (ValueExprContext) value;
            if (valueExprContext.ID() != null) {
                String name = valueExprContext.ID().getText();
                if (!values.containsKey(name)) {
                    throw new IllegalStateException("variable " + name
                            + " not defined!");
                } else {
                    Integer result = values.get(name);
                    if (result == null) {
                        throw new IllegalStateException("variable " + name
                                + " is not initialized!");
                    } else {
                        referredVariables.add(name);
                        return result;
                    }
                }
            } else {
                return new Integer(valueExprContext.INT().getText());
            }
        } else if (value instanceof NegExprContext) {
            NegExprContext negExprContext = (NegExprContext) value;
            return -val(negExprContext.expr());
        } else if (value instanceof ParenExprContext) {
            ParenExprContext parenExprContext = (ParenExprContext) value;
            return val(parenExprContext.expr());
        } else if (value instanceof OpExprContext) {
            OpExprContext opExprContext = (OpExprContext) value;
            String op = opExprContext.op.getText();
            switch (op) {
            case "+":
                return val(opExprContext.left) + val(opExprContext.right);
            case "-":
                return val(opExprContext.left) - val(opExprContext.right);
            case "*":
                return val(opExprContext.left) * val(opExprContext.right);
            case "/":
                return val(opExprContext.left) / val(opExprContext.right);
            default:
                throw new IllegalStateException("Unexpected!");
            }
        } else {
            throw new IllegalStateException("Unexpected!");
        }
    }

    @Override
    public void enterExtend(ExtendContext ctx) {
        String name = ctx.ID().getText();
        if (dependencyGraph.containsVertex(name)) {
            for (DependencyEdge edge : dependencyGraph
                    .incomingEdgesOf(name)) {
                dependencyGraph.removeEdge(edge);
            }
            if (dependencyGraph.edgesOf(name).isEmpty()) {
                dependencyGraph.removeVertex(name);
            }
        }
        ExprContext expr = ctx.expr();
        referredVariables = new HashSet<>();
        Integer result = val(expr);
        if (!referredVariables.isEmpty()) {
            dependencyGraph.addVertex(name);
            for (String from : referredVariables) {
                dependencyGraph.addVertex(from);
                dependencyGraph.addEdge(from, name, new DependencyEdge(expr));
            }
            CycleDetector<String, DependencyEdge> cycleDetector = new CycleDetector<>(
                    dependencyGraph);
            if (cycleDetector.detectCycles()) {
                throw new IllegalStateException("cycle dependency found!");
            }
        }
        values.put(name, result);
    }

}
