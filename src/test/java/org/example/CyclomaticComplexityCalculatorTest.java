package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CyclomaticComplexityCalculatorTest {
    record TestData(String pathToFile, String className, String methodName, int expectedComplexity) {}

    @TestFactory
    Stream<DynamicTest> dynamicCyclomaticComplexityTests() {
        String pathToFile = "src/test/resources/ComplexityExamples.java";
        List<TestData> testDataList = Arrays.asList(
                new TestData(pathToFile, "ComplexityExamples", "complexity1", 1),
                new TestData(pathToFile, "ComplexityExamples", "complexity2", 3),
                new TestData(pathToFile, "ComplexityExamples", "complexity3", 6),
                new TestData(pathToFile, "ComplexityExamples", "complexity4", 5),
                new TestData(pathToFile, "ComplexityExamples", "complexity5", 6),
                new TestData(pathToFile, "ComplexityExamples", "complexity6", 7),
                new TestData(pathToFile, "ComplexityExamples", "complexity7", 11),
                new TestData(pathToFile, "ComplexityExamples", "example1", 3),
                new TestData(pathToFile, "ComplexityExamples", "example2", 3),
                new TestData(pathToFile, "ComplexityExamples", "example3", 4),
                new TestData(pathToFile, "ComplexityExamples", "example4", 5),
                new TestData(pathToFile, "ComplexityExamples", "example5", 4),
                new TestData(pathToFile, "ComplexityExamples", "example6", 1)
        );

        return testDataList.stream().map(data ->
                DynamicTest.dynamicTest("Test Cyclomatic Complexity for " + data.className + "." + data.methodName,
                        () -> {
                            FileInputStream in = new FileInputStream(data.pathToFile);
                            ParseResult<CompilationUnit> compilationUnit = new JavaParser().parse(in);

                            if (compilationUnit.getResult().isEmpty()) throw new AssertionError("Failed to parse the file.");

                            int calculatedComplexity = CyclomaticComplexityCalculator.calculateCyclomaticComplexityForClassMethod(compilationUnit, data.className, data.methodName);

                            assertEquals(data.expectedComplexity, calculatedComplexity, "Cyclomatic complexity does not match the expected value.");
                        })
        );
    }
}
