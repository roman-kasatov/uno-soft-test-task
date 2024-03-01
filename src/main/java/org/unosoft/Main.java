package org.unosoft;


import java.io.*;
import java.time.Instant;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        long startTime = Instant.now().toEpochMilli();
        if (args.length == 0) {
            System.out.println("Ожидалось имя файла");
            return;
        }

        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            lines = reader.lines().toList();
        } catch (FileNotFoundException e) {
            System.out.println("Нет файла: " + e);
            return;
        } catch (IOException e) {
            System.out.println("Произошла ошибка при чтении файла: " + e);
            return;
        }

        List<List<String>> solution = new Solver().solve(lines);
        long groupsWithMoreThanOne = solution.stream().filter(it -> it.size() > 1).count();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write("Количество групп с более чем одним элементом: %d\n".formatted(groupsWithMoreThanOne));
            for (int i = 0; i < solution.size(); i++) {
                writer.write("Группа %d\n".formatted(i + 1));
                for (String line : solution.get(i)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Не получилось создать (открыть) файл: " + e);
            return;
        } catch (IOException e) {
            System.out.println("Произошла ошибка при записи в файл: " + e);
            return;
        }

        System.out.printf("Время выполнения: %d мс\n", Instant.now().toEpochMilli() - startTime);
    }
}