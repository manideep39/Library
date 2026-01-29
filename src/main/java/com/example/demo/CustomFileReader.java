package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Slf4j
public class CustomFileReader {
    private static List<Map<String, String>> readJsonFile(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> records = objectMapper.readValue(file, new TypeReference<>() {});

        long distinctRecordSizes = records.stream().mapToInt(Map::size).distinct().count();
        if (distinctRecordSizes > 1)
            log.warn("File: {}, Distinct JSON Lengths found", file.getName());

        return records;
    }

    private static List<Map<String, String>> readCsvFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        List<Map<String, String>> records = new ArrayList<>();
        if (!sc.hasNextLine()) return records;

        String[] headers = sc.nextLine().split(",");

        while (sc.hasNextLine()) {
            String row = sc.nextLine();
            String[] columns = row.split(",");

            if (headers.length != columns.length)
                log.warn("File: {}, Headers-Columns Length Mismatch for the row: {}", file.getName(), row);

            Map<String, String> record = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String columnValue = i >= columns.length ? null : columns[i];
                record.put(headers[i], columnValue);
            }
            records.add(record);
        }
        return records;
    }

    public static List<Map<String, String>> readFile(File file) throws FileNotFoundException {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        return switch (extension) {
            case ".json" -> readJsonFile(file);
            case ".csv" -> readCsvFile(file);
            default -> {
                log.warn("File: {} doesn't have a parser defined", fileName);
                yield null;
            }
        };
    }
}
