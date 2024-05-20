package org.example.calculators;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.example.records.MaintainabilityIndexResult;
import org.example.records.MethodDetails;

import java.io.*;

public class MaintainabilityIndexCalculator {
    public static MaintainabilityIndexResult calculateMI(ParseResult<CompilationUnit> cu, String className, MethodDetails method) throws IOException {
        double halsteadVolume;
        try {
            halsteadVolume = HalsteadVolumeCalculator.getHalsteadVolumeForClassMethod(cu, className, method.methodName());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("For\n" + method.methodContent() + "\n" + e.getMessage());
        }
        var loc = LinesOfCodeCalculator.getLOCForClassMethod(cu, className, method.methodName());
        var cc = CyclomaticComplexityCalculator.calculateCyclomaticComplexityForClassMethod(cu, className, method.methodName());
        var microsoftMi = (int) ((171.0 - 5.2 * Math.log(halsteadVolume) - 0.23 * cc - 16.2 * Math.log(loc)) * (100.0 / 171.0));
        if (microsoftMi < 0) {
            throw new IllegalStateException("microsoftMi is equal or less than 0");
        }
        var grade = "G";
        if (microsoftMi <= 10) {
            grade = "R";
        } else if (microsoftMi >= 10 && microsoftMi <= 20) {
            grade = "Y";
        }

        return new MaintainabilityIndexResult(className, method, halsteadVolume, cc, loc, microsoftMi, grade);
    }
}
