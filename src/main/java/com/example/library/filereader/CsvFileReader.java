package com.example.library.filereader;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Component
public class CsvFileReader implements FileReader {

    @Override
    public boolean supports(Path file) {
        return file.toString().endsWith(".csv");
    }

    @Override
    public List<Map<String, String>> read(Path file) {
        val records = new ArrayList<Map<String, String>>();

        try (Stream<String> lines = Files.lines(file)) {
            Iterator<String> it = lines.iterator();

            if (!it.hasNext())
                throw new IllegalArgumentException("File is empty " + file);

            var headerLine = it.next();
            String[] headers = Arrays.stream(headerLine.split(",", -1))
                    .map(String::trim).toArray(String[]::new);

            it.forEachRemaining(line -> {
                val columns = line.split(",", -1);

                if (headers.length != columns.length)
                    log.warn("File: {}, header/column mismatch: {}", file, line);

                val record = new HashMap<String, String>();
                for (int i = 0; i < headers.length; i++)
                    record.put(headers[i],  i >= columns.length ? null : columns[i].trim());

                records.add(record);
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read CSV: " + file, e);
        }

        return records;
    }
}
