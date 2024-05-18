package org.example;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class CyclomaticComplexityCalculator {

    public static int calculateCyclomaticComplexityForClassMethod(ParseResult<CompilationUnit> cu, String className, String methodName) throws IOException {
        ClassOrInterfaceDeclaration classDec = cu.getResult().orElseThrow().getClassByName(className).orElseThrow();

        MethodDeclaration method = classDec.getMethodsByName(methodName).stream().findFirst().orElseThrow();
        AtomicInteger cc = new AtomicInteger(1); // Start with 1 for the method entry

        method.walk(node -> {
            if (node instanceof IfStmt || // if conditions
                    node instanceof ForStmt || // for loops
                    node instanceof ForEachStmt || // for loops
                    node instanceof WhileStmt || // while loops
                    node instanceof DoStmt || // do-while loops
                    node instanceof SwitchStmt || // case in switch (each case is a path)
                    node instanceof CatchClause || // catch blocks
                    node instanceof ConditionalExpr || // ternary operators
                    (node instanceof BinaryExpr && // binary expressions for AND/OR which are not handled here
                            (((BinaryExpr) node).getOperator() == BinaryExpr.Operator.AND ||
                                    ((BinaryExpr) node).getOperator() == BinaryExpr.Operator.OR))) {
                cc.incrementAndGet();

                if (node instanceof SwitchStmt) { // Correctly handle switch case entries
                    node.getChildNodes().stream().filter(n -> n.toString().startsWith("case")).skip(1).forEach(n -> cc.incrementAndGet());
                }
            }
        });

        return cc.get();
    }
}
