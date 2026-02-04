package com.example.library;

import com.example.library.exception.BookInputDataException;
import com.example.library.exception.BooksRepoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            List<Map<String, String>> rawBooksData = CustomFileReader.readFile(filePath);
            List<Book> books = rawBooksData.stream().map(BookFactory::validateAndBuild).toList();
            booksRepo.saveAll(books);
        } catch (UncheckedIOException | IllegalArgumentException | BookInputDataException  | BooksRepoException  e) {
            log.error(e.getMessage());
        }
    }
}
