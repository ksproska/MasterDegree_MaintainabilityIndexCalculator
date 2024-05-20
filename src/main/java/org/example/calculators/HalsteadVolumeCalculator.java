package org.example.calculators;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.example.visitors.OperandVisitor;
import org.example.visitors.OperatorVisitor;

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
        if (programVocabulary == 0) {
            throw new IllegalStateException("programVocabulary is empty");
        }
        var programLength = numOfOperators + numOfOperands;

        double halsteadVolume = programLength * (Math.log(programVocabulary) / Math.log(2));
        if (0 == (int) halsteadVolume) {
            throw new IllegalStateException("halsteadVolume is 0");
        }
        return halsteadVolume;
    }
}
