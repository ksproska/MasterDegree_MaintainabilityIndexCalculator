package org.example;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OperandVisitor extends VoidVisitorAdapter<Void> {
    public List<String> getOperandsForClassMethod(String methodName) {
        return operands.getOrDefault(methodName, new ArrayList<>()).stream().sorted().toList();
    }

    private final Map<String, ArrayList<String>> operands = new HashMap<>();

    public int getAllOperandsCount(String methodName) {
        return operands.getOrDefault(methodName, new ArrayList<>()).size();
    }
    public int getDistinctOperandsCount(String methodName) {
        return operands.getOrDefault(methodName, new ArrayList<>()).stream().distinct().toList().size();
    }
    public List<String> getDistinctOperands(String methodName) {
        return operands.getOrDefault(methodName, new ArrayList<>()).stream().distinct().sorted().toList();
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
    public void visit(NameExpr n, Void arg) {
        addToMap(operands, n.getNameAsString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(IntegerLiteralExpr n, Void arg) {
        addToMap(operands, n.getValue(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(DoubleLiteralExpr n, Void arg) {
        addToMap(operands, n.getValue(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(StringLiteralExpr n, Void arg) {
        addToMap(operands, n.getValue(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(BooleanLiteralExpr n, Void arg) {
        addToMap(operands, n.toString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(CharLiteralExpr n, Void arg) {
        addToMap(operands, n.getValue(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(NullLiteralExpr n, Void arg) {
        addToMap(operands, "null", n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceType n, Void arg) {
        addToMap(operands, n.asString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(LongLiteralExpr n, Void arg) {
        addToMap(operands, n.getValue(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarator n, Void arg) {
        String varName = n.getNameAsString();
        if (!currentMethodName.isEmpty()) {  // Ensure we are within a method context
            addToMap(operands, varName, n.getClass().getSimpleName());
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodCallExpr n, Void arg) {
        if (!currentMethodName.isEmpty()) {  // Ensure we are within a method context
            String methodCall = n.getNameAsString();  // Get the method name
            addToMap(operands, methodCall, n.getClass().getSimpleName());
            // Also consider capturing the scope of the method call (like System.out)
            if (n.getScope().isPresent()) {
                var nodes = n.getScope().get().getChildNodes();
                if (nodes.size() > 1) {
                    addToMap(operands, nodes.get(1).toString(), n.getClass().getSimpleName());
                }
            }
        }
        super.visit(n, arg);
    }
}
