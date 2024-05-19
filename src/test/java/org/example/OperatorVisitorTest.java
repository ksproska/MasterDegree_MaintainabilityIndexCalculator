package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OperatorVisitorTest {
    record TestData(String pathToFile, String className, String methodName) {}

    @TestFactory
    Stream<DynamicTest> getOperatorsForClassMethod() {
        List<TestData> testDataList = Arrays.asList(
                new TestData("src/test/resources/OldProjectExample.java", "Example", "binSearch"),
                new TestData("src/test/resources/Example1.java", "Example", "example"),
                new TestData("src/test/resources/Example2.java", "Example", "example"),
                new TestData("src/test/resources/Example3.java", "Example", "example"),
                new TestData("src/test/resources/Example4.java", "Example", "example"),
                new TestData("src/test/resources/Example5.java", "Example", "example")
        );

        return testDataList.stream().map(data ->
                DynamicTest.dynamicTest("Test Operator visitor for " + data.pathToFile + ":" + data.className + "." + data.methodName,
                        () -> {
                            FileInputStream in = new FileInputStream(data.pathToFile);
                            ParseResult<CompilationUnit> compilationUnit = new JavaParser().parse(in);

                            if (compilationUnit.getResult().isEmpty()) {
                                throw new AssertionError("Failed to parse the file.");
                            }

                            ClassOrInterfaceDeclaration classDec = compilationUnit.getResult().orElseThrow().getClassByName(data.className).orElseThrow();
                            MethodDeclaration method = classDec.getMethodsByName(data.methodName).stream().findFirst().orElseThrow();

                            OperatorVisitor operatorVisitor = new OperatorVisitor();
                            method.accept(operatorVisitor, null);
//                            operatorVisitor.printCounts();

                            var operatorsDistinct = operatorVisitor.getDistinctOperators(data.methodName);

                            var getDistinctOperatorsFromOldMethod = getDistinctOperators(data.pathToFile)
                                    .stream()
                                    .filter(x -> !x.equals(data.className))
                                    .filter(x -> !x.equals(data.methodName))
                                    .distinct()
                                    .toList();
                            assertEquals(getDistinctOperatorsFromOldMethod.toString(), operatorsDistinct.toString());
                            assertEquals(getDistinctOperatorsFromOldMethod.size(), operatorVisitor.getDistinctOperatorsCount(data.methodName));
                        })
        );
    }

    private static List<String> getDistinctOperators(String pathToFile) throws IOException {
        var content = Files.readString(Path.of(pathToFile));
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(content.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        final org.eclipse.jdt.core.dom.CompilationUnit cu = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null);

        var visitor = new ASTVisitorMod();
        cu.accept(visitor);
        return visitor.getDistinctOperators();
    }
}