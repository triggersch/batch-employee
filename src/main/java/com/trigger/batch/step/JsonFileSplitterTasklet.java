package com.trigger.batch.step;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trigger.batch.model.Company;

@Component
public class JsonFileSplitterTasklet implements Tasklet {

    @Value("${batch.files.paths.input.json}")
    private String inputJsonPath;

    @Value("${batch.files.paths.split.dir}")
    private String outputDir;

    @Value("${batch.files.split.size}")
    private long maxChunkSize;

    private final ObjectMapper objectMapper;

    public JsonFileSplitterTasklet(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution,@NonNull ChunkContext chunkContext) throws Exception {
        Path inputPath = Paths.get(inputJsonPath);
        if (!Files.exists(inputPath)) {
            throw new IllegalArgumentException("Input JSON file not found: " + inputJsonPath);
        }

        // Préparer répertoire de split
        Path splitDir = Paths.get(outputDir);
        if (!Files.exists(splitDir)) {
            Files.createDirectories(splitDir);
        }

        // Streaming JSON parsing
        try (InputStream in = Files.newInputStream(inputPath);
                JsonParser parser = objectMapper.getFactory().createParser(in)) {

            if (parser.nextToken() != JsonToken.START_OBJECT || !parser.nextFieldName().equals("companies")) {
                throw new IllegalStateException("Invalid JSON format: expected { \"companies\": [ ... ] }");
            }

            parser.nextToken(); // START_ARRAY

            int fileIndex = 1;
            List<Company> buffer = new ArrayList<>();
            long currentSize = 0;

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                Company company = objectMapper.readValue(parser, Company.class);
                currentSize += objectMapper.writeValueAsBytes(company).length;

                if (currentSize >= maxChunkSize) {
                    writeSplitFile(buffer, fileIndex++, splitDir);
                    buffer.clear();
                    buffer.add(company);
                    currentSize = objectMapper.writeValueAsBytes(company).length;
                } else {
                     buffer.add(company);
                }
            }

            if (!buffer.isEmpty()) {
                writeSplitFile(buffer, fileIndex, splitDir);
            }
        }

        return RepeatStatus.FINISHED;
    }

    private void writeSplitFile(List<Company> companies, int index, Path dir) throws IOException {
        Map<String, List<Company>> wrapper = Map.of("companies", companies);
        Path filePath = dir.resolve("companies_" + index + ".json");
        objectMapper.writeValue(filePath.toFile(), wrapper);
    }
}
