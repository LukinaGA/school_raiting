package org.example;

import java.io.*;
import java.util.*;

public class GradeAnalyzer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите путь к папке с файлами: ");
        String folderPath = scanner.nextLine();

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Указанная папка не существует или не является директорией.");
            return;
        }

        Map<String, Map<String, Integer>> students = new HashMap<>();
        Set<String> subjects = new HashSet<>();

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                String studentName = file.getName().replace(".txt", "");
                students.put(studentName, new HashMap<>());

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(" - ");
                        if (parts.length == 2) {
                            String subject = parts[0].trim();
                            int grade = Integer.parseInt(parts[1].trim());
                            students.get(studentName).put(subject, grade);
                            subjects.add(subject);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка при чтении файла: " + file.getName());
                }
            }
        }

        Map<String, Double> subjectAverages = new HashMap<>();
        Map<String, Double> studentAverages = new HashMap<>();

        for (String subject : subjects) {
            double sum = 0;
            int count = 0;
            for (Map.Entry<String, Map<String, Integer>> entry : students.entrySet()) {
                if (entry.getValue().containsKey(subject)) {
                    sum += entry.getValue().get(subject);
                    count++;
                }
            }
            if (count > 0) {
                subjectAverages.put(subject, sum / count);
            }
        }

        for (Map.Entry<String, Map<String, Integer>> entry : students.entrySet()) {
            double sum = 0;
            int count = 0;
            for (int grade : entry.getValue().values()) {
                sum += grade;
                count++;
            }
            if (count > 0) {
                studentAverages.put(entry.getKey(), sum / count);
            }
        }

        String bestStudent = "";
        String worstStudent = "";
        double maxAverage = -1;
        double minAverage = Double.MAX_VALUE;

        for (Map.Entry<String, Double> entry : studentAverages.entrySet()) {
            if (entry.getValue() > maxAverage) {
                maxAverage = entry.getValue();
                bestStudent = entry.getKey();
            }
            if (entry.getValue() < minAverage) {
                minAverage = entry.getValue();
                worstStudent = entry.getKey();
            }
        }

        for (Map.Entry<String, Double> entry : subjectAverages.entrySet()) {
            System.out.printf("%s: %.2f%n", entry.getKey(), entry.getValue());
        }
        System.out.printf("\nЛучший ученик:\n%s (средний балл - %.2f)%n\n", bestStudent, maxAverage);
        System.out.printf("Худший ученик:\n%s (средний балл - %.2f)%n\n", worstStudent, minAverage);
        System.out.println("Количество учеников: " + students.size());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("отчет.txt"))) {
            for (Map.Entry<String, Double> entry : subjectAverages.entrySet()) {
                writer.write(String.format("%s: %.2f%n", entry.getKey(), entry.getValue()));
            }
            writer.write(String.format("\nЛучший ученик:\n%s (средний балл - %.2f)%n\n", bestStudent, maxAverage));
            writer.write(String.format("Худший ученик:\n%s (средний балл - %.2f)%n\n", worstStudent, minAverage));
            writer.write("Количество учеников: " + students.size() + "\n");
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл.");
        }
    }
}