package org.example;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class HalsteadVolumeCalculator {
    public static double getHalsteadVolumeForClassMethod(ParseResult<CompilationUnit> cu, String className, String methodName) {
        ClassOrInterfaceDeclaration classDec = cu.getResult().orElseThrow().getClassByName(className).orElseThrow();
        MethodDeclaration method = classDec.getMethodsByName(methodName).stream().findFirst().orElseThrow();

        OperatorVisitor operatorVisitor = new OperatorVisitor();
        method.accept(operatorVisitor, null);

        OperandVisitor operandVisitor = new OperandVisitor();
        method.accept(operandVisitor, null);

        var operators = operatorVisitor.getOperatorsForClassMethod(methodName);
        var operands = operandVisitor.getOperandsForClassMethod(methodName);

        var numOfOperators = operators.size();
        var numOfOperatorsUnique = operators.stream().distinct().count();
        var numOfOperands = operands.size();
        var numOfOperandsUnique = operands.stream().distinct().count();

        var programVocabulary = numOfOperatorsUnique + numOfOperandsUnique;
        var programLength = numOfOperators + numOfOperands;

        return (programLength * (Math.log(programVocabulary) / Math.log(2)));
    }
}
