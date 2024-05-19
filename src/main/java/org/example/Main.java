package org.example;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
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

        List<String> fileLines = null;
        try {
            fileLines = Files.readAllLines(Paths.get(javaPathsFile));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        var counter = 0;
        ProgressBar pb = new ProgressBar("Analyzing files", fileLines.size());
        for (String javaFilepath : fileLines) {
            var compilationErrors = FileAnalyzer.analyzeGivenJavaFile(javaFilepath, outputRaport, outputCodeDir);
            for (var err : compilationErrors) {
                if (!(err instanceof CompilationUnitException || err instanceof MethodBodyNotFoundException)) {
//                    System.out.println("Analyzing file " + javaFilepath);
//                    err.printStackTrace();
                    counter += 1;
                }
            }
            pb.step();
        }
        pb.close();
        System.out.println("" + counter + " failed");
    }
}