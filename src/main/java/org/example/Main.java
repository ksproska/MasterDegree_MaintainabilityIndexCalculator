package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.example.exceptions.CompilationUnitException;
import org.example.exceptions.MethodBodyNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String javaPathsFile = "java_files_list.txt";
        String outputRaport = "raport.csv";
        String outputCodeDir = "raportCode";

        var counter = 0;
        try {
            List<String> fileLines = Files.readAllLines(Paths.get(javaPathsFile));
            for (String javaFilepath : fileLines) {
                var compilationErrors = analyzeGivenJavaFile(javaFilepath, outputRaport, outputCodeDir);
                for(var err : compilationErrors) {
                    if (err instanceof CompilationUnitException || err instanceof MethodBodyNotFoundException) {

                    } else {
                        System.out.println("Analyzing file " + javaFilepath);
                        err.printStackTrace();
                        counter += 1;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        System.out.println("" + counter + " failed");
    }

    private static List<Exception> analyzeGivenJavaFile(String javaFilepath, String outputRaport, String outputCodeDir) {
        removeEmptyLines(javaFilepath);

        List<Exception> compilationErrors = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(javaFilepath)) {
            var jp = new JavaParser();
            ParseResult<CompilationUnit> cu = jp.parse(in);

            if (!cu.isSuccessful()) {
                throw new CompilationUnitException("unable to analyze file " + javaFilepath + "CompilationUnit did not finish successfully");
            }

            Map<String, List<MethodDetails>> classesAndMethods = new HashMap<>();
            try {
                cu.getResult().orElseThrow().accept(new ClassVisitor(), classesAndMethods);
            } catch (NoSuchElementException e) {
                throw new MethodBodyNotFoundException("unable to find method names in file " + javaFilepath);
            }

            List<MaintainabilityIndexCalculator.MaintainabilityIndexResult> miResults = new ArrayList<>();
            for (var className : classesAndMethods.keySet()) {
                var methods = classesAndMethods.get(className);
                for (var method : methods) {
                    try {
                        var res = MaintainabilityIndexCalculator.calculateMI(cu, className, method);
                        miResults.add(res);
                    } catch (NoSuchElementException e) {
                        throw new MethodBodyNotFoundException("unable to find method " + method.methodName() + "in file" + javaFilepath);
                    }
                }
            }
            miResults.sort(Comparator.comparingInt(MaintainabilityIndexCalculator.MaintainabilityIndexResult::microsoftMi).reversed());
            for (var res : miResults) {
                res.saveToFile(outputRaport, outputCodeDir);
            }
        } catch (Exception e) {
            compilationErrors.add(e);
        }
        return compilationErrors;
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