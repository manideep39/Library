package com.example.library.filereader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class JsonFileReader implements FileReader {
    @Override
    public boolean supports(Path file) {
        return file.toString().endsWith(".json");
    }

    @Override
    public Stream<Map<String, String>> read(Path file) {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();

        try {
            InputStream in = Files.newInputStream(file);
            JsonParser parser = factory.createParser(in);

            if (parser.nextToken() == JsonToken.START_ARRAY)
                parser.nextToken();

            MappingIterator<Map<String, String>> iterator = mapper.readValues(parser, new TypeReference<>() {});
            Stream<Map<String, String>> stream = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                    false
            );

            return stream.onClose(() -> {
                try {
                    iterator.close();
                    parser.close();
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read JSON: " + file, e);
        }
    }
}
