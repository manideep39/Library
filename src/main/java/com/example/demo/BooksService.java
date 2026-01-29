package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BooksService {
    private final BooksRepo booksRepo;

    public BooksService(BooksRepo booksRepo) {
        this.booksRepo = booksRepo;
    }

    public void bulkImport(String folderPath) throws IOException, SQLException {
        File inputFile = new File(folderPath);
        if (inputFile.isDirectory()) {
            File[] files = inputFile.listFiles();
            if (files == null)
                throw new IOException();
            for (File file: files) {
                List<Map<String, String>> rawBooksData = CustomFileReader.readFile(file);
                if (rawBooksData == null) continue;
                List<Book> books = new ArrayList<>();
                rawBooksData.forEach(rawBookData -> {
                    try {
                        Book book = BookFactory.validateAndBuild(rawBookData);
                        books.add(book);
                    } catch (BookInputDataException e) {
                        log.error("Book Input Data Exception in File: {}, Errors: {}", file.getName(), e.getErrors().toString());
                    }
                });
                booksRepo.saveAll(books);
            }
        }
    }
}
