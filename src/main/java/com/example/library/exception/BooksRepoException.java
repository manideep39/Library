package com.example.library.exception;

public class BooksRepoException extends RuntimeException {
    public BooksRepoException(String message) {
        super(message);
    }
}
