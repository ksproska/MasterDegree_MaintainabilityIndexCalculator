package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String javaFilepath = "src/test/resources/ComplexityExamples.java";
        String outputRaport = "raport.csv";
        removeEmptyLines(javaFilepath);

        try (FileInputStream in = new FileInputStream(javaFilepath)) {
            var jp = new JavaParser();
            ParseResult<CompilationUnit> cu = jp.parse(in);

            Map<String, List<String>> classesAndMethods = new HashMap<>();
            cu.getResult().orElseThrow().accept(new ClassVisitor(), classesAndMethods);

            for (var className : classesAndMethods.keySet()) {
                var methods = classesAndMethods.get(className);
                for (var methodName: methods) {
                    var res = MaintainabilityIndexCalculator.calculateMI(cu, className, methodName);
                    res.saveToFile(outputRaport);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeEmptyLines(String javaFilepath) {
        File file = new File(javaFilepath);
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.trim().isEmpty()) {
                    lines.add(currentLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}