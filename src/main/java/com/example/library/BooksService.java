package com.example.library;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
public class BooksService {
    private final BooksRepo booksRepo;

    public BooksService(BooksRepo booksRepo) {
        this.booksRepo = booksRepo;
    }

    public void bulkImport(Path folderPath) throws IOException {
        if (Files.isDirectory(folderPath)) {
            try (Stream<Path> filePaths = Files.list(folderPath)) {
                filePaths.forEach(filePath -> {
                    var fileName = filePath.getFileName().toString();
                    try {
                        List<Map<String, String>> rawBooksData = CustomFileReader.readFile(filePath);
                        List<Book> books = new ArrayList<>();
                        rawBooksData.forEach(rawBookData -> {
                            try {
                                Book book = BookFactory.validateAndBuild(rawBookData);
                                books.add(book);
                            } catch (BookInputDataException e) {
                                log.error("Book Input Data Exception in File: {}, Errors: {}", fileName, e.getErrors().toString());
                            }
                        });
                        booksRepo.saveAll(books);
                    } catch (UncheckedIOException | SQLException e) {
                        log.error(e.getMessage());
                    }
                });
            }
        }
    }
}
