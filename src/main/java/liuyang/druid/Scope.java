package liuyang.druid;

import java.util.HashMap;
import java.util.Map;

import liuyang.druid.DruidParser.ExprContext;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class Scope {
    public static class DependencyEdge {
        ExprContext exprContext;

        public DependencyEdge(ExprContext exprContext) {
            this.exprContext = exprContext;
        }
    }

    private Map<String, Object> values = new HashMap<>();

    private DirectedGraph<String, DependencyEdge> dependencyGraph = new DefaultDirectedGraph<>(DependencyEdge.class);

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public DirectedGraph<String, DependencyEdge> getDependencyGraph() {
        return dependencyGraph;
    }

    public void setDependencyGraph(DirectedGraph<String, DependencyEdge> dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public void addVariable(String name) {
        values.put(name, null);
    }

    public Object getValue(String name) {
        return values.get(name);
    }

    public void setValue(String name, Object value) {
        values.put(name, value);
    }

}
