package org.example;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class OperandVisitor extends VoidVisitorAdapter<Void> {
    public ArrayList<String> getOperandsForClassMethod(String methodName) {
        return operands.getOrDefault(methodName, new ArrayList<>());
    }

    private final Map<String, ArrayList<String>> operands = new HashMap<>();

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

//        @Override
//        public void visit(MethodCallExpr n, Void arg) {
//            addToMap(operators, String.format("%-25s", "MethodCallExpr") + n.getNameAsString());
//            super.visit(n, arg);
//        }

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
    public void visit(PrimitiveType n, Void arg) {
        addToMap(operands, n.asString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceType n, Void arg) {
        addToMap(operands, n.asString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(ArrayType n, Void arg) { // ArrayType
        addToMap(operands, n.asString(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

    @Override
    public void visit(LongLiteralExpr n, Void arg) {
        addToMap(operands, n.getValue(), n.getClass().getSimpleName());
        super.visit(n, arg);
    }

//        @Override
//        public void visit(FieldDeclaration n, Void arg) {
//            System.out.println(n.toString());
//            n.getVariables().forEach(
//                    variable -> addToMap(
//                            operands,
//                            String.format("%-25s", "FieldDeclaration") + variable.getNameAsString()
//                    )
//            );
//
//            super.visit(n, arg);
//        }

    public void printCounts() {
        System.out.println("\n> Operands:");
        for (String method : operands.keySet()) {
            ArrayList<String> literals = operands.get(method);
            System.out.println(
                    String.format("%-35s", method)
                            + " (" + literals.size() + ", " + literals.stream().distinct().count() + ")\n"
                            + literals.stream().distinct().sorted().collect(Collectors.joining("\n")) + "\n"
            );
        }
    }
}
