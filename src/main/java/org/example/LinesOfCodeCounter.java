package org.example;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class LinesOfCodeCounter {

    public static int getLOCForClassMethod(ParseResult<CompilationUnit> cu, String className, String methodName) {
        ClassOrInterfaceDeclaration classDec = cu.getResult().orElseThrow().getClassByName(className).orElseThrow();
        MethodDeclaration method = classDec.getMethodsByName(methodName).stream().findFirst().orElseThrow();
        int countComments = method
                .getAllContainedComments()
                .stream()
                .mapToInt(comment -> comment.toString().split("\n").length)
                .sum();
        return method.getEnd().get().line - method.getBegin().get().line + 1 - countComments;
    }
}
