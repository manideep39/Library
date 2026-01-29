package com.example.demo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class BookFactory {
    private static final String REQUIRED_FIELD_MISSING_ERROR = "REQUIRED_FIELD_MISSING_ERROR";
    private static final String FIELD_TYPE_PARSING_ERROR = "FIELD_TYPE_PARSING_ERROR";

    private static final List<String> REQUIRED_FIELDS = List.of("book_id", "title", "author", "genre", "price", "rating");
    private static final Map<String, Class<?>> FIELDS_DATA_TYPE = Map.of(
            "book_id", Long.class,
            "title", String.class,
            "author", String.class,
            "genre", String.class,
            "price", BigDecimal.class,
            "rating", Double.class
    );

    public static Book validateAndBuild(Map<String, String> input) {
        Map<String, List<String>> errors = new HashMap<>();

        requiredFieldsCheck(input, errors);
        fieldsDataTypeCheck(input, errors);

        if (!errors.isEmpty())
            throw new BookInputDataException(errors);

        return new Book(
                Long.parseLong(input.get("book_id")),
                input.get("title"),
                input.get("author"),
                input.get("genre"),
                new BigDecimal(input.get("price")),
                Double.parseDouble(input.get("rating"))
        );
    }


    private static void requiredFieldsCheck(Map<String, String> input, Map<String, List<String>> errors) {
        for (String field: REQUIRED_FIELDS) {
            if (!input.containsKey(field)) {
                errors.putIfAbsent(field, new ArrayList<>());
                errors.get(field).add(REQUIRED_FIELD_MISSING_ERROR);
            }
        }
    }

    private static void fieldsDataTypeCheck(Map<String, String> input, Map<String, List<String>> errors) {
        for (Map.Entry<String, String> entry: input.entrySet()) {
            String field = entry.getKey();
            Class<?> fieldType = FIELDS_DATA_TYPE.get(field);

            try {
                switch (fieldType.getSimpleName()) {
                    case "Long" -> Long.parseLong(input.get(field));
                    case "Double" -> Double.parseDouble(input.get(field));
                    case "BigDecimal" -> new BigDecimal(input.get(field));
                }
            } catch (NumberFormatException | NullPointerException e) {
                errors.computeIfAbsent(field, k -> new ArrayList<>())
                        .add(FIELD_TYPE_PARSING_ERROR);
            }
        }
    }
}
