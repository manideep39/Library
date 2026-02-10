package com.example.library.service;

import com.example.library.Book;
import com.example.library.repository.BookRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldReturnBookWhenIdExists() {
        Book book = new Book(1L, "my room", "pmd", "sci-fi", new BigDecimal(20), 4.5);
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertNotNull(result);
        assertThat(result).isEqualTo(book);
        verify(bookRepo, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not a valid bookId");

    }
}