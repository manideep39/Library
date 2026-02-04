package com.example.library.filereader;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
@AllArgsConstructor
public class FileReaderResolver {
    private final List<FileReader> readers;

    public FileReader resolve(Path path) {
        return readers.stream()
                .filter(r -> r.supports(path))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file format"));
    }
}
