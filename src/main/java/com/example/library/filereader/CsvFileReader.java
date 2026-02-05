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
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class CsvFileReader implements FileReader {

    @Override
    public boolean supports(Path file) {
        return file.toString().endsWith(".csv");
    }

    @Override
    public Stream<Map<String, String>> read(Path file) {
        try {
            Stream<String> lines = Files.lines(file);
            Iterator<String> it = lines.iterator();

            if (!it.hasNext()) {
                lines.close();
                throw new IllegalArgumentException("File is empty " + file);
            }

            String headerLine = it.next();
            String[] headers = Arrays.stream(headerLine.split(",", -1))
                    .map(String::trim).toArray(String[]::new);

            Stream<Map<String, String>> output = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED), false
            ).map(line -> {
                val columns = line.split(",", -1);

                if (headers.length != columns.length)
                    log.warn("File: {}, header/column mismatch: {}", file, line);

                val record = new HashMap<String, String>();
                for (int i = 0; i < headers.length; i++)
                    record.put(headers[i],  i >= columns.length ? null : columns[i].trim());

                return record;
            });

            return output.onClose(lines::close);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read CSV: " + file, e);
        }
    }
}
