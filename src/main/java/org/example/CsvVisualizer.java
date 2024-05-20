package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
                            <th>Filepath</th>
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
        TreeMap<Integer, List<String>> maintainabilityIndex = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(outputRaport))) {
            br.readLine(); // skipping line with headers
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSeparator);

                double hv = Double.parseDouble(data[2].trim());
                int cc = Integer.parseInt(data[3].trim());
                int loc = Integer.parseInt(data[4].trim());
                int mi = Integer.parseInt(data[5].trim());
                String originalFilepath = data[8].trim();
                String codeFilename = data[7];

                File codeFile = new File(outputCodeDir, codeFilename);

                originalFilepath = getUrl(originalFilepath);

                if (codeFile.exists() && codeFile.isFile()) {
                    StringBuilder fileContent = new StringBuilder();
                    try (BufferedReader codeReader = new BufferedReader(new FileReader(codeFile))) {
                        String codeLine;
                        while ((codeLine = codeReader.readLine()) != null) {
                            fileContent.append(codeLine).append(System.lineSeparator());
                        }
                    }

                    String htmlFragment = "<tr>\n" +
                            "    <td>" + mi + "</td><td>" + hv + "</td><td>" + cc + "</td><td>" + loc + "</td><td>" + originalFilepath + "</td>\n" +
                            "    <td><div class=\"code-container\"><pre class=\"copyText\"><code class=\"language-java\">" +
                            escapeHtml(fileContent.toString()) + "</code></pre></div></td>\n" +
                            "</tr>\n";
                    var allFragments = maintainabilityIndex.getOrDefault(mi, new ArrayList<>());
                    allFragments.add(htmlFragment);
                    maintainabilityIndex.put(mi, allFragments);
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(indexHtmlFile))) {
            bw.write(HTML_BEGINNING);
            for (Map.Entry<Integer, List<String>> entry : maintainabilityIndex.entrySet()) {
                if (entry.getKey() <= 30) {
                    for (var fragment: entry.getValue()) {
                        bw.write(fragment);
                    }
                } else {
                    bw.write(entry.getValue().get(0));
                }
            }
            bw.write(HTML_ENDING);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    private static String getUrl(String originalFilepath) {
        if (originalFilepath.contains("/home/kamilasproska/IdeaProjects/hadoop")) {
            originalFilepath = originalFilepath.replace("/home/kamilasproska/IdeaProjects/hadoop", "https://github.com/apache/hadoop/tree/trunk");
            return "<a href=" + originalFilepath + ">hadoop</a>";
        }
        else if (originalFilepath.contains("/home/kamilasproska/IdeaProjects/elasticsearch")) {
            originalFilepath = originalFilepath.replace("/home/kamilasproska/IdeaProjects/elasticsearch", "https://github.com/elastic/elasticsearch/tree/main");
            return "<a href=" + originalFilepath + ">elasticsearch</a>";
        }
        else if (originalFilepath.contains("/home/kamilasproska/IdeaProjects/wildfly")) {
            originalFilepath = originalFilepath.replace("/home/kamilasproska/IdeaProjects/wildfly", "https://github.com/wildfly/wildfly/tree/main");
            return "<a href=" + originalFilepath + ">wildfly</a>";
        }
        else if (originalFilepath.contains("/home/kamilasproska/IdeaProjects/gwt")) {
            originalFilepath = originalFilepath.replace("/home/kamilasproska/IdeaProjects/gwt", "https://github.com/gwtproject/gwt/tree/main");
            return "<a href=" + originalFilepath + ">gwt</a>";
        }
        else if (originalFilepath.contains("/home/kamilasproska/IdeaProjects/cassandra")) {
            originalFilepath = originalFilepath.replace("/home/kamilasproska/IdeaProjects/cassandra", "https://github.com/apache/cassandra/tree/trunk");
            return "<a href=" + originalFilepath + ">cassandra</a>";
        }
        else if (originalFilepath.contains("/home/kamilasproska/IdeaProjects/kafka")) {
            originalFilepath = originalFilepath.replace("/home/kamilasproska/IdeaProjects/kafka", "https://github.com/apache/kafka/tree/trunk/bin");
            return "<a href=" + originalFilepath + ">kafka</a>";
        }
        else if (originalFilepath.contains("/home/kamilasproska/IdeaProjects/spring-framework")) {
            originalFilepath = originalFilepath.replace("/home/kamilasproska/IdeaProjects/spring-framework", "https://github.com/spring-projects/spring-framework/tree/main");
            return "<a href=" + originalFilepath + ">spring-framework</a>";
        }
        return originalFilepath;
    }

    private static String escapeHtml(String str) {
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\'", "&apos;");
    }
}
