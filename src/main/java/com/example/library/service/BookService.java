package com.example.library.service;

import com.example.library.Book;
import com.example.library.BookFactory;
import com.example.library.exception.BooksRepoException;
import com.example.library.filereader.FileReader;
import com.example.library.filereader.FileReaderResolver;
import com.example.library.repository.BookRepo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
public class BookService {
    private final BookRepo bookRepo;
    private final FileReaderResolver readerResolver;
    private final int BOOKS_BUFFER_SIZE = 5;

    public BookService(BookRepo bookRepo, FileReaderResolver readerResolver) {
        this.bookRepo = bookRepo;
        this.readerResolver = readerResolver;
    }

    public Book getBookById(long bookId) {
        return bookRepo.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Not a valid bookId"));
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
        FileReader reader = readerResolver.resolve(filePath);
        val batch = new ArrayList<Book>(BOOKS_BUFFER_SIZE);

        try (Stream<Map<String, String>> stream = reader.read(filePath)) {
            stream.map(BookFactory::validateAndBuild)
                    .filter(Objects::nonNull)
                    .forEach(book -> {
                        batch.add(book);
                        if (batch.size() == BOOKS_BUFFER_SIZE)
                            flushBatch(batch);
                    });

            flushBatch(batch);

        } catch (UncheckedIOException | IllegalArgumentException | BooksRepoException  e) {
            log.error(e.getMessage());
        }
    }

    private void flushBatch(List<Book> batch) {
        if (batch.isEmpty())
            return;

        bookRepo.saveAll(batch);
        batch.clear();
    }
}
