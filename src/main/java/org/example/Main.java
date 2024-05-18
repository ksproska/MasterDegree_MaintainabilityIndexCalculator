package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        String javaFilepath = "/home/kamilasproska/IdeaProjects/javaParser/src/main/java/org/example/Example.java";
        removeEmptyLines(javaFilepath);

        try {
            FileInputStream in = new FileInputStream(javaFilepath);
            var jp = new JavaParser();
            ParseResult<CompilationUnit> cu = jp.parse(in);

            OperatorVisitor operatorVisitor = new OperatorVisitor();
            cu.getResult().orElseThrow().accept(operatorVisitor, null);
            operatorVisitor.printCounts();


            OperandVisitor operandVisitor = new OperandVisitor();
            cu.getResult().orElseThrow().accept(operandVisitor, null);
            operandVisitor.printCounts();

            var operatorsMap = operatorVisitor.getOperators();
            var operandsMap = operandVisitor.getOperands();

            Set<String> methods = Stream.concat(operatorsMap.keySet().stream(), operandsMap.keySet().stream())
                    .collect(Collectors.toSet());

            for (String method : methods) {
                var operators = operatorsMap.get(method);
                var operands = operandsMap.get(method);
                var elems = method.split(":");
                var className = elems[0];
                var methodName = elems[1];
                var res = calculateMI(className, methodName, operators, operands, cu);
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

    record MIResult(String className, String methodName, int halsteadVolume, int cc, int loc, int microsoftMi) {
        void print() {
            System.out.println(className + ":" + methodName);
            System.out.println(String.format("%-20s", "\thalsteadVolume:") + halsteadVolume);
            System.out.println(String.format("%-20s", "\tLOC:") + loc);
            System.out.println(String.format("%-20s", "\tCC:") + cc);
            System.out.println(String.format("%-20s", "\tmicrosoft MI:") + microsoftMi);
            System.out.println();
        }
    }

    private static MIResult calculateMI(String className, String methodName, ArrayList<String> operators, ArrayList<String> operands, ParseResult<CompilationUnit> cu) throws IOException {
        var numOfOperators = operators.size();
        var numOfOperatorsUnique = operators.stream().distinct().count();
        var numOfOperands = operands.size();
        var numOfOperandsUnique = operands.stream().distinct().count();

        var programVocabulary = numOfOperatorsUnique + numOfOperandsUnique;
        var programLength = numOfOperators + numOfOperands;

        var halsteadVolume = (int) (programLength * (Math.log(programVocabulary) / Math.log(2)));

        var loc = LinesOfCodeCounter.getLOCForClassMethod(cu, className, methodName);
        var cc = CyclomaticComplexityCalculator.calculateCyclomaticComplexityForClassMethod(cu, className, methodName);
        var microsoftMi = (int) ((171.0 - 5.2 * Math.log(halsteadVolume) - 0.23 * cc - 16.2 * Math.log(loc)) * (100.0 / 171.0));
        return new MIResult(className, methodName, halsteadVolume, cc, loc, microsoftMi);
    }
}