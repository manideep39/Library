package com.example.library;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
@ToString
@Table("books")
public class Book {
    @Id
    @Column("id")
    private Long bookId;
    private String title;
    private String author;
    private String genre;
    private BigDecimal price;
    private double rating;

}
