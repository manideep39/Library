package com.example.library.filereader;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JsonFileReader implements FileReader {
    @Override
    public boolean supports(Path file) {
        return file.toString().endsWith(".json");
    }

    @Override
    public List<Map<String, String>> read(Path file) {
        val objectMapper = new ObjectMapper();
        List<Map<String, String>> records = objectMapper.readValue(file, new TypeReference<>() {});

        long distinctRecordSizes = records.stream().mapToInt(Map::size).distinct().count();
        if (distinctRecordSizes > 1)
            log.warn("File: {}, Distinct JSON Lengths found", file.toString());

        return records;
    }
}
