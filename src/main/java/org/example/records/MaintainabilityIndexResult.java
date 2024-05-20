package org.example.records;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record MaintainabilityIndexResult(String className, MethodDetails method, double halsteadVolume, int cc, int loc,
                                         int microsoftMi, String grade) {
    void print() {
        System.out.println(className + ":" + method.methodName());
        System.out.println(method.methodContent());
        System.out.println(String.format("%-20s", "halsteadVolume:") + halsteadVolume);
        System.out.println(String.format("%-20s", "LOC:") + loc);
        System.out.println(String.format("%-20s", "CC:") + cc);
        System.out.println(String.format("%-20s", "microsoft MI:") + microsoftMi);
        System.out.println(String.format("%-20s", "grade:") + grade);
        System.out.println("-----------------------------------------------------------");
    }

    public void saveToFile(String path, String outputCodeDir) {
        File directory = new File(outputCodeDir);
        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            if (!dirCreated) {
                throw new IllegalStateException("unable to create " + directory.getAbsolutePath());
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(dtf);

        File codeFile = createFileWithMethodCode(timestamp, directory);

        File file = new File(path);
        boolean fileExists = file.exists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                writer.println("ClassName,MethodName,Halstead Volume,Cyclomatic Complexity (CC),Lines Of Code (LOC),Microsoft Maintainability Index (MI),Grade,CodeFilename");
            }
            writer.printf("%s,%s,%.2f,%d,%d,%d,%s,%s%n", className, method.methodName(), halsteadVolume, cc, loc, microsoftMi, grade, codeFile.getName());
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    private File createFileWithMethodCode(String timestamp, File directory) {
        String formattedMicrosoftMi = String.format("%03d", microsoftMi);
        String codeFileName = "code_" + formattedMicrosoftMi + "_" + grade + "_" + className + "_" + method.methodName() + "_" + timestamp + ".txt";
        File codeFile = new File(directory, codeFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(codeFile))) {
            writer.write(method.methodContent());
        } catch (IOException e) {
            throw new IllegalStateException();
        }
        return codeFile;
    }
}
