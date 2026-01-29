package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    private final BooksService booksService;

    public DemoApplication(BooksService booksService) {
        this.booksService = booksService;
    }

	public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
	}

    @Override
    public void run(String... args) throws IOException, SQLException {
        booksService.bulkImport("src/main/java/com/example/demo/books");
    }
}
