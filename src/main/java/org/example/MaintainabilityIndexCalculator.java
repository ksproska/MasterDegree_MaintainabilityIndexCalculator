package org.example;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MaintainabilityIndexCalculator {
    public static MaintainabilityIndexResult calculateMI(ParseResult<CompilationUnit> cu, String className, String methodName) throws IOException {
        var halsteadVolume = HalsteadVolumeCalculator.getHalsteadVolumeForClassMethod(cu, className, methodName);
        var loc = LinesOfCodeCounter.getLOCForClassMethod(cu, className, methodName);
        var cc = CyclomaticComplexityCalculator.calculateCyclomaticComplexityForClassMethod(cu, className, methodName);
        var microsoftMi = (int) ((171.0 - 5.2 * Math.log(halsteadVolume) - 0.23 * cc - 16.2 * Math.log(loc)) * (100.0 / 171.0));
        var grade = "G";
        if (microsoftMi <= 10) {
            grade = "R";
        } else if (microsoftMi >= 10 && microsoftMi <= 20) {
            grade = "Y";
        }

        return new MaintainabilityIndexResult(className, methodName, halsteadVolume, cc, loc, microsoftMi, grade);
    }

    record MaintainabilityIndexResult(String className, String methodName, double halsteadVolume, int cc, int loc, int microsoftMi, String grade) {
        void print() {
            System.out.println(className + ":" + methodName);
            System.out.println(String.format("%-20s", "\thalsteadVolume:") + halsteadVolume);
            System.out.println(String.format("%-20s", "\tLOC:") + loc);
            System.out.println(String.format("%-20s", "\tCC:") + cc);
            System.out.println(String.format("%-20s", "\tmicrosoft MI:") + microsoftMi);
            System.out.println(String.format("%-20s", "\tgrade:") + grade);
            System.out.println();
        }

        void saveToFile(String path) {
            File file = new File(path);
            boolean fileExists = file.exists();

            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                if (!fileExists) {
                    writer.println("ClassName,MethodName,HalsteadVolume,CC,LOC,MicrosoftMI,Grade");
                }
                writer.printf("%s,%s,%.2f,%d,%d,%d,%s%n", className, methodName, halsteadVolume, cc, loc, microsoftMi, grade);
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        }
    }
}
