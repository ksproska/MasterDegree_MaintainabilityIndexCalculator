package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.example.calculators.HalsteadVolumeCalculator;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HalsteadVolumeCalculatorTest {
    record TestData(String pathToFile, String className, String methodName, double expectedHalsteadVolume) {}

    @TestFactory
    Stream<DynamicTest> dynamicHalsteadVolumeTests() {
        String pathToFile = "src/test/resources/ComplexityExamples.java";
        List<TestData> testDataList = Arrays.asList(
                new TestData(pathToFile, "ComplexityExamples", "calculateScore", 261.52),
                new TestData(pathToFile, "ComplexityExamples", "isHighScore", 6.34),
                new TestData(pathToFile, "ComplexityExamples", "binSearch", 273.99)
        );

        return testDataList.stream().map(data ->
                DynamicTest.dynamicTest("Test Halstead Volume for " + data.className + "." + data.methodName,
                        () -> {
                            FileInputStream in = new FileInputStream(data.pathToFile);
                            ParseResult<CompilationUnit> compilationUnit = new JavaParser().parse(in);

                            if (compilationUnit.getResult().isEmpty()) {
                                throw new AssertionError("Failed to parse the file.");
                            }

                            double actualHalsteadVolume = HalsteadVolumeCalculator.getHalsteadVolumeForClassMethod(compilationUnit, data.className, data.methodName);

                            assertEquals(data.expectedHalsteadVolume, actualHalsteadVolume, 0.1, "Halstead volume do not match the expected value.");
                        })
        );
    }
}