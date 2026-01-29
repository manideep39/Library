package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class BooksRepo {
    private final DataSource dataSource;

    private final String INSERT_BOOKS_QUERY = "INSERT INTO books (book_id,title,author,genre,price,rating) VALUES (?, ?, ?, ?, ?, ?)";

    @Getter
    private final List<Book> books = new ArrayList<>();

    public void saveAll(List<Book> books) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(INSERT_BOOKS_QUERY)) {
            connection.setAutoCommit(false);
            for (Book book: books) {
                pstmt.setLong(1, book.getBookId());
                pstmt.setString(2, book.getTitle());
                pstmt.setString(3, book.getAuthor());
                pstmt.setString(4, book.getGenre());
                pstmt.setBigDecimal(5, book.getPrice());
                pstmt.setDouble(6, book.getRating());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        }
    }
}
