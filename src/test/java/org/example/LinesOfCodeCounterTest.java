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

import static org.junit.jupiter.api.Assertions.*;

class LinesOfCodeCounterTest {

    record TestData(String pathToFile, String className, String methodName, int expectedLinesOfCode) {}

    @TestFactory
    Stream<DynamicTest> dynamicLOCCountTests() {
        String pathToFile = "src/test/resources/ComplexityExamples.java";
        List<TestData> testDataList = Arrays.asList(
                new TestData(pathToFile, "ComplexityExamples", "complexity1", 3),
                new TestData(pathToFile, "ComplexityExamples", "complexity2", 9),
                new TestData(pathToFile, "ComplexityExamples", "complexity3", 15),
                new TestData(pathToFile, "ComplexityExamples", "complexity4", 13),
                new TestData(pathToFile, "ComplexityExamples", "complexity5", 16),
                new TestData(pathToFile, "ComplexityExamples", "complexity6", 19),
                new TestData(pathToFile, "ComplexityExamples", "complexity7", 34),
                new TestData(pathToFile, "ComplexityExamples", "example1", 9),
                new TestData(pathToFile, "ComplexityExamples", "example2", 9),
                new TestData(pathToFile, "ComplexityExamples", "example3", 12),
                new TestData(pathToFile, "ComplexityExamples", "example4", 9),
                new TestData(pathToFile, "ComplexityExamples", "example5", 11),
                new TestData(pathToFile, "ComplexityExamples", "example6", 9)
        );

        return testDataList.stream().map(data ->
                DynamicTest.dynamicTest("Test LOC for " + data.className + "." + data.methodName,
                        () -> {
                            FileInputStream in = new FileInputStream(data.pathToFile);
                            ParseResult<CompilationUnit> compilationUnit = new JavaParser().parse(in);

                            if (compilationUnit.getResult().isEmpty()) {
                                throw new AssertionError("Failed to parse the file.");
                            }

                            int actualLinesOfCode = LinesOfCodeCounter.getLOCForClassMethod(compilationUnit, data.className, data.methodName);

                            assertEquals(data.expectedLinesOfCode, actualLinesOfCode, "Lines of code do not match the expected value.");
                        })
        );
    }

}