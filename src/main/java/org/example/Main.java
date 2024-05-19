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
        String outputCodeDir = "raportCode";
        removeEmptyLines(javaFilepath);

        try (FileInputStream in = new FileInputStream(javaFilepath)) {
            var jp = new JavaParser();
            ParseResult<CompilationUnit> cu = jp.parse(in);

            Map<String, List<MethodDetails>> classesAndMethods = new HashMap<>();
            cu.getResult().orElseThrow().accept(new ClassVisitor(), classesAndMethods);

            List<MaintainabilityIndexCalculator.MaintainabilityIndexResult> miResults = new ArrayList<>();
            for(var className : classesAndMethods.keySet()) {
                var methods = classesAndMethods.get(className);
                for (var method: methods) {
                    var res = MaintainabilityIndexCalculator.calculateMI(cu, className, method);
                    miResults.add(res);
                }
            }
            miResults.sort(Comparator.comparingInt(MaintainabilityIndexCalculator.MaintainabilityIndexResult::microsoftMi).reversed());
            for (var res: miResults) {
                res.saveToFile(outputRaport, outputCodeDir);
                res.print();
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