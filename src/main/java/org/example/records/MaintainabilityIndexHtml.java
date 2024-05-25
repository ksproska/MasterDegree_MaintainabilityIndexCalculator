package org.example.records;


public record MaintainabilityIndexHtml(
        double halsteadVolume, int cc, int loc, int microsoftMi, String originalFilepath, String methodContent
) {
    public static final String HTML_BEGINNING = """
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

    public String getHtmlFragment() {
        return "<tr>\n" +
                "    <td>" + microsoftMi + "</td><td>" + halsteadVolume + "</td><td>" + cc + "</td><td>" + loc + "</td><td>" + getUrl(originalFilepath) + "</td>\n" +
                "    <td><div class=\"code-container\"><pre class=\"copyText\"><code class=\"language-java\">" +
                escapeHtml(methodContent) + "</code></pre></div></td>\n" +
                "</tr>\n";
    }

    private static String escapeHtml(String str) {
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\'", "&apos;");
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
}
