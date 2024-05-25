package org.example;

import org.example.records.MaintainabilityIndexHtml;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class CsvVisualizer {
    private static final String CSV_SEPARATOR = ",";
    public static final List<Integer> MIS_TO_DISPLAY = List.of(9, 10, 11, 19, 20, 21, 25, 30, 35, 40, 45, 50, 55, 60);
    public static final int MAX_RECORDS_FOR_MI = 1;
    public static final Comparator<MaintainabilityIndexHtml> MAINTAINABILITY_INDEX_COMPARATOR = Comparator.comparingInt(MaintainabilityIndexHtml::loc);
    public static final Predicate<MaintainabilityIndexHtml> MAINTAINABILITY_INDEX_CC_PREDICATE = mih -> mih.cc() > 1;

    static void visualizeInHtml(String outputRaport, String outputCodeDir, String indexHtmlFile) {
        TreeMap<Integer, List<MaintainabilityIndexHtml>> maintainabilityIndexTree = readMisFromCsvFile(outputRaport, outputCodeDir);

        List<String> orderedFragments = new ArrayList<>();
        orderedFragments.add(MaintainabilityIndexHtml.HTML_BEGINNING);
        for (Map.Entry<Integer, List<MaintainabilityIndexHtml>> entry : maintainabilityIndexTree.entrySet()) {
            Integer mi = entry.getKey();
            if (MIS_TO_DISPLAY.contains(mi)) {
                orderedFragments.addAll(
                        entry.getValue()
                                .stream()
                                .sorted(MAINTAINABILITY_INDEX_COMPARATOR)
                                .filter(MAINTAINABILITY_INDEX_CC_PREDICATE)
                                .limit(MAX_RECORDS_FOR_MI)
                                .map(MaintainabilityIndexHtml::getHtmlFragment)
                                .toList()
                );
            }
        }
        orderedFragments.add(MaintainabilityIndexHtml.HTML_ENDING);

        writeLinesToFile(indexHtmlFile, orderedFragments);
    }

    private static TreeMap<Integer, List<MaintainabilityIndexHtml>> readMisFromCsvFile(String outputRaport, String outputCodeDir) {
        TreeMap<Integer, List<MaintainabilityIndexHtml>> maintainabilityIndexTree = new TreeMap<>();

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(outputRaport))) {
            br.readLine(); // skipping line with headers
            while ((line = br.readLine()) != null) {
                String[] data = line.split(CSV_SEPARATOR);

                double hv = Double.parseDouble(data[2].trim());
                int cc = Integer.parseInt(data[3].trim());
                int loc = Integer.parseInt(data[4].trim());
                int mi = Integer.parseInt(data[5].trim());
                String originalFilepath = data[8].trim();
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

                    var maintainabilityIndexHtml = new MaintainabilityIndexHtml(hv, cc, loc, mi, originalFilepath, fileContent.toString());
                    var allFragments = maintainabilityIndexTree.getOrDefault(mi, new ArrayList<>());
                    allFragments.add(maintainabilityIndexHtml);
                    maintainabilityIndexTree.put(mi, allFragments);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return maintainabilityIndexTree;
    }

    private static void writeLinesToFile(String indexHtmlFile, List<String> orderedFragments) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(indexHtmlFile))) {
            for (String fragment : orderedFragments) {
                bw.write(fragment);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
