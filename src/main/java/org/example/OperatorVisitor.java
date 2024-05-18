package org.example;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class OperatorVisitor extends VoidVisitorAdapter<Void> {

    public ArrayList<String> getOperatorsForClassMethod(String methodName) {
        return operators.getOrDefault(methodName, new ArrayList<>());
    }

    private final Map<String, ArrayList<String>> operators = new HashMap<>();

    private String currentMethodName = "";

    private void addToMap(Map<String, ArrayList<String>> operators, String currentOperator, String className) {
        var key = currentMethodName;
        var newOperators = operators.getOrDefault(key, new ArrayList<>());
        newOperators.add(String.format("%-25s", className) + currentOperator);
        operators.put(key, newOperators);
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        currentMethodName = n.getNameAsString();
        super.visit(n, arg);
        currentMethodName = "";
    }

    @Override
    public void visit(BinaryExpr n, Void arg) {
        addToMap(operators, n.getOperator().asString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(UnaryExpr n, Void arg) {
        addToMap(operators, n.getOperator().asString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(ConditionalExpr n, Void arg) {
        addToMap(operators, "?", n.getClass().getSimpleName());
        addToMap(operators, ":", n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(InstanceOfExpr n, Void arg) {
        addToMap(operators, "instanceof", n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(AssignExpr n, Void arg) {
        addToMap(operators, n.getOperator().asString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

//        @Override
//        public void visit(MethodCallExpr n, Void arg) {
//            addToMap(operators, String.format("%-25s", "MethodCallExpr") + n.getNameAsString());
//            super.visit(n, arg);
//        }

    @Override
    public void visit(IfStmt n, Void arg) {
        addToMap(operators, "if", n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(TryStmt n, Void arg) {
        addToMap(operators, "try", n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    public void printCounts() {
        System.out.println("> Operators:");
        for (String method : operators.keySet()) {
            ArrayList<String> literals = operators.get(method);
            System.out.println(
                    String.format("%-35s", method)
                            + " (" + literals.size() + ", " + literals.stream().distinct().count() + ")\n"
                            + literals.stream().distinct().sorted().collect(Collectors.joining("\n")) + "\n"
            );
        }
    }
}
