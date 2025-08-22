package com.trigger.batch.step;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CobolFlatFileSplitterTasklet implements Tasklet {

    @Value("${batch.files.paths.input.flat}")
    private String inputFlatPath;

    @Value("${batch.files.paths.split.dir}")
    private String outputDir;

    @Value("${batch.files.split.size}")
    private long maxChunkSize;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext)
            throws Exception {
        Path inputPath = Paths.get(inputFlatPath);
        if (!Files.exists(inputPath)) {
            throw new IllegalArgumentException("Fichier flat introuvable: " + inputFlatPath);
        }

        Path splitDir = Paths.get(outputDir);
        if (!Files.exists(splitDir)) {
            Files.createDirectories(splitDir);
        }

        List<String> buffer = new ArrayList<>();
        long currentSize = 0;
        int fileIndex = 1;

        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            String line;
            String currentCompanyId = null;
            String employeeCompanyId = null;
            List<String> companyBlock = new ArrayList<>();
            long companyBlockSize = 0;

            while ((line = reader.readLine()) != null) {
                char recType = line.charAt(0);

                if (recType == 'C') {
                    // Si l'ancien bloc dépasse la taille max -> écrire
                    if (!companyBlock.isEmpty() && (companyBlockSize + line.length() > maxChunkSize)) {
                        writeSplitFile(buffer, fileIndex++, splitDir);
                        buffer.clear();
                        currentSize = 0;
                    }
                    // Nouveau bloc de company
                    // Ajouter le bloc au buffer global
                    buffer.addAll(companyBlock);
                    currentSize += companyBlockSize;
                    companyBlock.clear();
                    companyBlock.add(line);
                    companyBlockSize = line.getBytes(StandardCharsets.UTF_8).length;
                    currentCompanyId = line.substring(1, 11).trim();
                } else if (recType == 'E') {
                    // Employee, vérifier qu'il appartient à la company actuelle
                    employeeCompanyId = line.substring(103, line.length()).trim();
                    if (!employeeCompanyId.equals(currentCompanyId)) {
                        throw new IllegalStateException("Employee sans company correspondante: " + employeeCompanyId);
                    }
                    companyBlock.add(line);
                    companyBlockSize += line.getBytes(StandardCharsets.UTF_8).length;
                }

                // Si le bloc dépasse max size -> flush
                if (currentSize + companyBlockSize > maxChunkSize) {
                    writeSplitFile(buffer, fileIndex++, splitDir);
                    buffer.clear();
                    currentSize = 0;
                }
            }

            if (currentCompanyId != null && employeeCompanyId == null) {
                throw new IllegalStateException("Company without employee");
            }
            // Écriture finale
            if (!companyBlock.isEmpty()) {
                buffer.addAll(companyBlock);
                writeSplitFile(buffer, fileIndex, splitDir);
            }
        }

        return RepeatStatus.FINISHED;
    }

    private void writeSplitFile(List<String> lines, int index, Path dir) throws IOException {
        Path path = dir.resolve("companies_" + index + ".dat");
        Files.write(path, lines, StandardCharsets.UTF_8);
    }
}
