package org.example;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OperatorVisitor extends VoidVisitorAdapter<Void> {

    public ArrayList<String> getOperatorsForClassMethod(String methodName) {
        return operators.getOrDefault(methodName, new ArrayList<>());
    }

    private final Map<String, ArrayList<String>> operators = new HashMap<>();

    public int getAllOperatorsCount(String methodName) {
        return operators.getOrDefault(methodName, new ArrayList<>()).size();
    }

    public int getDistinctOperatorsCount(String methodName) {
        return operators.getOrDefault(methodName, new ArrayList<>()).stream().distinct().toList().size();
    }

    public List<String> getDistinctOperators(String methodName) {
        return operators.getOrDefault(methodName, new ArrayList<>()).stream().distinct().sorted().toList();
    }

    private String currentMethodName = "";

    private void addToMap(Map<String, ArrayList<String>> operators, String currentOperator, String className) {
        var key = currentMethodName;
        var newOperators = operators.getOrDefault(key, new ArrayList<>());
        newOperators.add(currentOperator);
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
    public void visit(AssignExpr n, Void arg) {
        addToMap(operators, n.getOperator().asString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Void arg) {
        addToMap(operators, "=", n.getClass().getSimpleName());
        super.visit(n, arg);
    }
}
