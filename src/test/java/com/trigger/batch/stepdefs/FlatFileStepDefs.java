package com.trigger.batch.stepdefs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.trigger.batch.step.CobolFlatFileSplitterTasklet;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlatFileStepDefs {

    @Value("${batch.files.paths.input.flat}")
    private String inputFlatPath;
    @Autowired
    private Tasklet cobolFlatFileSplitterTasklet;

    private Path inputDir;
    private Path inputFile;
    private static final Path SOURCE_DIR = Paths.get("src/test/resources/dats");

    @Before
    public void cleanInputDirectory() throws IOException {

        inputFile = Paths.get(inputFlatPath);
        inputDir = inputFile.getParent();

        if (Files.exists(inputDir)) {
            try (var paths = Files.list(inputDir)) {
                paths.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new RuntimeException("❌ Impossible de nettoyer le dossier " + inputDir, e);
                    }
                });
            }
        } else {
            Files.createDirectories(inputDir);
        }
    }

    @Before
    public void cleanOutputDir() throws IOException, URISyntaxException {
        final var url = getClass().getResource("/output/actual");
        Path outputDir;
        if (url == null) {
            final var rootUri = getClass().getResource("/").toURI();
            outputDir = Paths.get(rootUri).resolve("output/actual");
        } else {
            outputDir = Paths.get(url.toURI());
        }

        if (Files.exists(outputDir)) {
            // Supprime récursivement le contenu du dossier
            Files.walk(outputDir)
                    .sorted(Comparator.reverseOrder())
                    .filter(path -> !path.equals(outputDir))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Impossible de supprimer " + path, e);
                        }
                    });
        } else {
            Files.createDirectories(outputDir);
        }
    }

    @Given("the Dat file {string}")
    public void the_dat_file(String fileName) throws IOException {
        Path sourceFile = SOURCE_DIR.resolve(fileName);

        if (!Files.exists(sourceFile)) {
            throw new IllegalArgumentException("❌ Le fichier source " + sourceFile + " n'existe pas !");
        }

        Files.copy(sourceFile, inputFile, StandardCopyOption.REPLACE_EXISTING);
    }

    @Given("the split file max size is {string} bytes")
    public void set_split_file_max_size(String size) throws Exception {
        Field field = CobolFlatFileSplitterTasklet.class.getDeclaredField("maxChunkSize");
        field.setAccessible(true);
        field.set(cobolFlatFileSplitterTasklet, Long.parseLong(size));
    }

    @Then("the directory {string} should contain multiple split Dat files")
    public void the_directory_should_contain_multiple_split_dat_files(String path) {
        File dir = new File(path);
        assertTrue(dir.exists());
        File[] files = dir.listFiles((d, name) -> name.startsWith("companies_") && name.endsWith(".dat"));
        assertNotNull(files);
        assertTrue(files.length > 1); // Split attendu
    }

    @Then("each split file should be smaller than {string} bytes")
    public void each_split_file_should_be_smaller_than(String sizeStr) {
        long maxSize = Long.parseLong(sizeStr);
        File dir = new File("src/test/resources/dats/split/");
        File[] files = dir.listFiles((d, name) -> name.startsWith("companies_") && name.endsWith(".dat"));

        for (File file : files) {
            assertTrue(file.length() < maxSize);
        }
    }

    @Then("genrerated file {string} must be identical to file {string}")
    public void generatedFileMustBeIdenticalToFile(String actualPath, String expectedPath)
            throws IOException, URISyntaxException {
        final var expectedUrl = getClass().getResource(expectedPath);
        if (expectedUrl == null) {
            throw new IllegalArgumentException("Expected file not found : " + expectedPath);
        }
        String expectedContent = Files.readString(Paths.get(expectedUrl.toURI()), StandardCharsets.UTF_8);

        final var actualUrl = getClass().getResource("/output/actual/" + actualPath);
        if (actualUrl == null) {
            throw new IllegalArgumentException("Actual file not found : " + actualPath);
        }
        String actualContent = Files.readString(Paths.get(actualUrl.toURI()), StandardCharsets.UTF_8);

        assertEquals(expectedContent, actualContent);
    }

}
