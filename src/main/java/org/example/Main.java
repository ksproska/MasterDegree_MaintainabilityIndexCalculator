package org.example;

import static org.example.CsvVisualizer.visualizeInHtml;
import static org.example.FileAnalyzer.processFiles;

public class Main {
    public static void main(String[] args) {
        String javaPathsFile = "java_files_list.txt";
        String outputRaport = "raport.csv";
        String outputCodeDir = "raportCode";
        String indexHtmlFile = "index.html";

        processFiles(javaPathsFile, outputRaport, outputCodeDir);
        visualizeInHtml(outputRaport, outputCodeDir, indexHtmlFile);
    }
}
