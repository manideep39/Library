package com.example.library.filereader;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface FileReader {
    boolean supports(Path path);
    List<Map<String, String>> read(Path path);
}
