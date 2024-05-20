package org.example;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class CsvVisualizer {
    private static final String csvSeparator = ",";
    private static final String HTML_BEGINNING = """
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
                    """;
    public static final String HTML_ENDING = """
                        </tbody>
                    </table>
                                        
                    <script src="script.js"></script>
                    </body>
                    </html>
                    """;

    static void visualizeInHtml(String outputRaport, String outputCodeDir, String indexHtmlFile) {
        String line;
        TreeMap<Integer, String> maintainabilityIndex = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(outputRaport))) {
            br.readLine(); // skipping line with headers
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
                            "    <td><div class=\"code-container\"><pre><code class=\"language-java\">" +
                            escapeHtml(fileContent.toString()) + "</code></pre></div></td>\n" +
                            "</tr>\n");
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(indexHtmlFile))) {
            bw.write(HTML_BEGINNING);
            for (Map.Entry<Integer, String> entry : maintainabilityIndex.entrySet()) {
                bw.write(entry.getValue());
            }
            bw.write(HTML_ENDING);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
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
