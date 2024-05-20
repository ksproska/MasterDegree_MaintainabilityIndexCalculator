package org.example;

import me.tongfei.progressbar.ProgressBar;
import org.example.exceptions.CompilationUnitException;
import org.example.exceptions.MethodBodyNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        String javaPathsFile = "java_files_list.txt";
        String outputRaport = "raport.csv";
        String outputCodeDir = "raportCode";

        processFiles(javaPathsFile, outputRaport, outputCodeDir);
        visualizeInHtml(outputRaport, outputCodeDir);
    }

    private static void processFiles(String javaPathsFile, String outputRaport, String outputCodeDir) {
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

    private static void visualizeInHtml(String outputRaport, String outputCodeDir) {
        BufferedReader br = null;
        BufferedWriter bw = null;
        String line = "";
        String csvSeparator = ",";

        try {
            br = new BufferedReader(new FileReader(outputRaport));
            bw = new BufferedWriter(new FileWriter("index.html"));
            bw.write("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>MI Score and Java Method Table with Dynamic Coloring</title>
                        <link rel="stylesheet" href="styles.css">
                    </head>
                    <body>
                    <table id="methodTable">
                        <thead>
                        <tr>
                            <th>MI</th>
                            <th>HV</th>
                            <th>CC</th>
                            <th>LOC</th>
                            <th>Method Code</th>
                        </tr>
                        </thead>
                        <tbody>
                    """);

            // Initialize a TreeMap to sort MI values automatically
            TreeMap<Integer, String> maintainabilityIndex = new TreeMap<>();

            // Skip the header row
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSeparator);

                double hv = Double.parseDouble(data[2].trim());
                int cc = Integer.parseInt(data[3].trim());
                int loc = Integer.parseInt(data[4].trim());
                int mi = Integer.parseInt(data[5].trim());
                String codeFilename = data[7];

                File codeFile = new File(outputCodeDir, codeFilename);

                if (codeFile.exists() && codeFile.isFile()) {
                    StringBuilder fileContent = new StringBuilder();
                    try (BufferedReader codeReader = new BufferedReader(new FileReader(codeFile))) {
                        String codeLine;
                        while ((codeLine = codeReader.readLine()) != null) {
                            fileContent.append(codeLine).append(System.lineSeparator());
                        }
                    }

                    maintainabilityIndex.put(mi, "<tr>\n" +
                            "    <td>" + mi + "</td><td>" + hv + "</td><td>" + cc + "</td><td>" + loc + "</td>\n" +
                            "    <td><div class=\"code-container\"><pre><code class=\"language-java\">" + escapeHtml(fileContent.toString()) + "</code></pre></div></td>\n" +
                            "</tr>\n");
                }
            }

            for (Map.Entry<Integer, String> entry : maintainabilityIndex.entrySet()) {
                bw.write(entry.getValue());
            }

            bw.write("""
                        </tbody>
                    </table>
                                        
                    <script src="script.js"></script>
                    </body>
                    </html>
                    """);

        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            try {
                if (br != null) br.close();
                if (bw != null) {
                    bw.close();
                    System.out.println("Successfully saved to index.html");
                }
            } catch (IOException ex) {
                System.out.println("Error closing the file: " + ex.getMessage());
            }
        }
    }

    private static String escapeHtml(String str) {
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\'", "&apos;");
    }
}