package com.example.library;

import com.example.library.exception.BooksRepoException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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

    public void saveAll(List<Book> books) {
        try (val connection = dataSource.getConnection();
             val stmt = connection.prepareStatement(INSERT_BOOKS_QUERY)) {
            connection.setAutoCommit(false);
            for (val book: books) {
                stmt.setLong(1, book.getBookId());
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getAuthor());
                stmt.setString(4, book.getGenre());
                stmt.setBigDecimal(5, book.getPrice());
                stmt.setDouble(6, book.getRating());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new BooksRepoException("Failed to save " + books.size() + " books");
        }
    }

    public void save(Book book) {
        try (val connection = dataSource.getConnection();
             val stmt = connection.prepareStatement(INSERT_BOOKS_QUERY)) {
            stmt.setLong(1, book.getBookId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getGenre());
            stmt.setBigDecimal(5, book.getPrice());
            stmt.setDouble(6, book.getRating());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksRepoException("Failed to save " + books.size() + " books");
        }
    }
}
