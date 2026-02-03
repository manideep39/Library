package com.example.library;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

@SpringBootApplication
public class LibraryApplication implements CommandLineRunner {
    private final BooksService booksService;

    public LibraryApplication(BooksService booksService) {
        this.booksService = booksService;
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        var path = Paths.get("src/main/resources/books");
        booksService.bulkImport(path);
    }
}
