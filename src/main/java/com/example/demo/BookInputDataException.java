package com.example.demo;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class BookInputDataException extends RuntimeException {
    private final Map<String, List<String>> errors;

    public BookInputDataException(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}
