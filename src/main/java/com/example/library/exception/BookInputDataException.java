package com.example.library.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class BookInputDataException extends RuntimeException {
    public BookInputDataException(Map<String, List<String>> errors) {
        super(errors.toString());
    }
}
