package liuyang.druid;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import liuyang.druid.DruidParser.AssignContext;
import liuyang.druid.DruidParser.DefineContext;
import liuyang.druid.DruidParser.ExprContext;
import liuyang.druid.DruidParser.ExtendContext;
import liuyang.druid.DruidParser.FunctionCallExprContext;
import liuyang.druid.DruidParser.FunctionContext;
import liuyang.druid.DruidParser.NegExprContext;
import liuyang.druid.DruidParser.OpExprContext;
import liuyang.druid.DruidParser.ParenExprContext;
import liuyang.druid.DruidParser.ReturnstContext;
import liuyang.druid.DruidParser.StatementContext;
import liuyang.druid.DruidParser.ValueExprContext;
import liuyang.druid.Scope.DependencyEdge;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;

public class DruidInterpreter extends DruidBaseVisitor<Object> {

    private Map<String, FunctionContext> functions;

    public DruidInterpreter(Map<String, FunctionContext> functions) {
        this.functions = functions;
        scopeStack.push(new Scope());
    }

    private Stack<Scope> scopeStack = new Stack<>();

    public Map<String, Object> getValues() {
        return scopeStack.peek().getValues();
    }

    @Override
    public Object visitDefine(DefineContext ctx) {
        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            if (scopeStack.peek().contains(name)) {
                throw new IllegalStateException("variable "
                        + name
                        + " already defined!");
            } else {
                scopeStack.peek().addVariable(name);
            }
        }
        return null;
    }

    @Override
    public Object visitFunction(FunctionContext ctx) {
        return null;
    }

    @Override
    public Object visitAssign(AssignContext ctx) {
        String name = ctx.ID().getText();
        clearDependency(name);
        if (!scopeStack.peek().contains(name)) {
            throw new IllegalStateException("variable "
                    + name
                    + " not defined!");
        } else {
            Object originalValue = scopeStack.peek().getValue(name);
            Object value = visit(ctx.expr());
            if (!value.equals(originalValue)) {
                scopeStack.peek().setValue(name, value);
                trigger(name);
            }
        }
        return null;
    }

    @Override
    public Object visitOpExpr(OpExprContext ctx) {
        String op = ctx.op.getText();
        switch (op) {
            case "+":
                return (Integer) visit(ctx.left)
                        + (Integer) visit(ctx.right);
            case "-":
                return (Integer) visit(ctx.left)
                        - (Integer) visit(ctx.right);
            case "*":
                return (Integer) visit(ctx.left)
                        * (Integer) visit(ctx.right);
            case "/":
                return (Integer) visit(ctx.left)
                        / (Integer) visit(ctx.right);
            default:
                throw new IllegalStateException("Unexpected!");
        }
    }

    @Override
    public Object visitParenExpr(ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Object visitNegExpr(NegExprContext ctx) {
        return -(Integer) visit(ctx.expr());
    }

    @Override
    public Object visitFunctionCallExpr(FunctionCallExprContext ctx) {
        String name = ctx.ID().getText();
        if (!functions.containsKey(name)) {
            throw new IllegalStateException("function "
                    + name
                    + " is not defined!");
        }
        FunctionContext function = functions.get(name);
        List<Object> argValues = ctx.args.stream().map(arg -> visit(arg)).collect(Collectors.toList());
        return valFunction(function, argValues);
    }

    @Override
    public Object visitValueExpr(ValueExprContext ctx) {
        if (ctx.ID() != null) {
            String name = ctx.ID().getText();
            if (!scopeStack.peek().contains(name)) {
                throw new IllegalStateException("variable "
                        + name
                        + " not defined!");
            } else {
                Object result = scopeStack.peek().getValue(name);
                if (result == null) {
                    throw new IllegalStateException("variable "
                            + name
                            + " is not initialized!");
                } else {
                    referredVariables.add(name);
                    return result;
                }
            }
        } else {
            return new Integer(ctx.INT().getText());
        }
    }

    private void trigger(String name) {
        Scope scope = scopeStack.peek();
        DirectedGraph<String, DependencyEdge> dependencyGraph = scope.getDependencyGraph();
        if (dependencyGraph.containsVertex(name)) {
            for (DependencyEdge edge : dependencyGraph.outgoingEdgesOf(name)) {
                String target = dependencyGraph.getEdgeTarget(edge);
                Object originalValue = scope.getValue(target);
                Object value = visit(edge.exprContext);
                if (!value.equals(originalValue)) {
                    scope.setValue(target, value);
                    trigger(target);
                }
            }
        }
    }

    private Set<String> referredVariables = new HashSet<>();

    private Object valFunction(FunctionContext function, List<Object> argValues) {
        Scope scope = new Scope();
        if (function.params.size() != argValues.size()) {
            throw new IllegalStateException("function params number not matched, expected "
                    + function.params.size()
                    + " but was "
                    + argValues.size()
                    + "!");
        }
        for (int i = 0; i < function.params.size(); i++) {
            scope.setValue(function.params.get(i).getText(), argValues.get(i));
        }
        scopeStack.push(scope);
        for (StatementContext statement : function.statement()) {
            if (statement.returnst() != null) {
                Object result = visit(statement);
                scopeStack.pop();
                return result;
            } else {
                visit(statement);
            }
        }
        throw new IllegalStateException("No return statement in function "
                + function.name.getText());
    }

    @Override
    public Object visitReturnst(ReturnstContext ctx) {
        return visit(ctx.expr());
    }

    private void clearDependency(String name) {
        Scope scope = scopeStack.peek();
        DirectedGraph<String, DependencyEdge> dependencyGraph = scope.getDependencyGraph();
        if (dependencyGraph.containsVertex(name)) {
            for (DependencyEdge edge : dependencyGraph.incomingEdgesOf(name)) {
                dependencyGraph.removeEdge(edge);
            }
            if (dependencyGraph.edgesOf(name).isEmpty()) {
                dependencyGraph.removeVertex(name);
            }
        }
    }

    @Override
    public Object visitExtend(ExtendContext ctx) {
        Scope scope = scopeStack.peek();
        DirectedGraph<String, DependencyEdge> dependencyGraph = scope.getDependencyGraph();
        String name = ctx.ID().getText();
        clearDependency(name);
        ExprContext expr = ctx.expr();
        referredVariables = new HashSet<>();
        Object result = visit(expr);
        if (!referredVariables.isEmpty()) {
            dependencyGraph.addVertex(name);
            for (String from : referredVariables) {
                dependencyGraph.addVertex(from);
                dependencyGraph.addEdge(from, name, new DependencyEdge(expr));
            }
            CycleDetector<String, DependencyEdge> cycleDetector = new CycleDetector<>(dependencyGraph);
            if (cycleDetector.detectCycles()) {
                throw new IllegalStateException("cycle dependency found!");
            }
        }
        scope.setValue(name, result);
        return null;
    }

}
