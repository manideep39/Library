package com.example.library;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class CustomFileReader {
    private static List<Map<String, String>> readJsonFile(Path path) {
        val objectMapper = new ObjectMapper();
        List<Map<String, String>> records = objectMapper.readValue(path, new TypeReference<>() {});

        long distinctRecordSizes = records.stream().mapToInt(Map::size).distinct().count();
        if (distinctRecordSizes > 1)
            log.warn("File: {}, Distinct JSON Lengths found", path.getFileName().toString());

        return records;
    }

    private static List<Map<String, String>> readCsvFile(Path path) {
        val fileName = path.getFileName().toString();
        val records = new ArrayList<Map<String, String>>();

        try (Stream<String> lines = Files.lines(path)) {
            Iterator<String> it = lines.iterator();

            if (!it.hasNext())
                throw new IllegalArgumentException("File is empty " + fileName);

            var headerLine = it.next();
            String[] headers = Arrays.stream(headerLine.split(",", -1))
                    .map(String::trim).toArray(String[]::new);

            it.forEachRemaining(line -> {
                val columns = line.split(",", -1);

                if (headers.length != columns.length)
                    log.warn("File: {}, header/column mismatch: {}", fileName, line);

                val record = new HashMap<String, String>();
                for (int i = 0; i < headers.length; i++)
                    record.put(headers[i],  i >= columns.length ? null : columns[i].trim());

                records.add(record);
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read CSV: " + fileName, e);
        }

        return records;
    }

    public static List<Map<String, String>> readFile(Path path) {
        val fileName = path.getFileName().toString();
        val extension = fileName.substring(fileName.lastIndexOf('.'));
        return switch (extension) {
            case ".json" -> readJsonFile(path);
            case ".csv" -> readCsvFile(path);
            default -> {
                log.warn("File: {} doesn't have a parser defined", fileName);
                yield List.of();
            }
        };
    }
}
