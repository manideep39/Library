package com.example.library;

import com.example.library.exception.BooksRepoException;
import com.example.library.filereader.FileReader;
import com.example.library.filereader.FileReaderResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class BooksService {
    private final BooksRepo booksRepo;
    private final FileReaderResolver readerResolver;

    public BooksService(BooksRepo booksRepo, FileReaderResolver readerResolver) {
        this.booksRepo = booksRepo;
        this.readerResolver = readerResolver;
    }

    public void bulkImport(Path filePath) {
        if (Files.isDirectory(filePath)) {
            try (Stream<Path> filePaths = Files.list(filePath)) {
                filePaths.forEach(this::importBookFile);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            importBookFile(filePath);
        }
    }

    private void importBookFile(Path filePath) {
        try {
            FileReader reader = readerResolver.resolve(filePath);
            List<Map<String, String>> rawBooksData = reader.read(filePath);
            List<Book> books = rawBooksData.stream()
                    .map(BookFactory::validateAndBuild)
                    .filter(Objects::nonNull)
                    .toList();
            booksRepo.saveAll(books);
        } catch (UncheckedIOException | IllegalArgumentException | BooksRepoException  e) {
            log.error(e.getMessage());
        }
    }
}
