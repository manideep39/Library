package com.example.library.filereader;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public interface FileReader {
    boolean supports(Path path);
    Stream<Map<String, String>> read(Path path);
}
