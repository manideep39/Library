package com.example.demo;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
@ToString
public class Book {
    private Long bookId;
    private String title;
    private String author;
    private String genre;
    private BigDecimal price;
    private double rating;
}
