package com.example.library;

import lombok.val;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;

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
    public void run(String... args) {
        val path = Paths.get("src/main/resources/books");
        booksService.bulkImport(path);
    }
}
