package com.trigger.batch.stepdefs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;

import com.trigger.batch.step.JsonFileSplitterTasklet;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class JsonFileStepDefs {

    @Autowired
    private Tasklet splitterTasklet;

    @Given("the JSON file {string}")
    public void the_json_file(String path)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Accès au champ
        Field field = JsonFileSplitterTasklet.class.getDeclaredField("inputJsonPath");
        field.setAccessible(true);

        // Modifier la valeur du champ
        field.set(splitterTasklet, path);
    }

    @Given("the split file max size is {string} bytes")
    public void set_split_file_max_size(String size) throws Exception {
        Field field = JsonFileSplitterTasklet.class.getDeclaredField("maxChunkSize");
        field.setAccessible(true);
        field.set(splitterTasklet, Long.parseLong(size));
    }

    @Then("the directory {string} should contain multiple split JSON files")
    public void the_directory_should_contain_multiple_split_json_files(String path) {
        File dir = new File(path);
        assertTrue(dir.exists());
        File[] files = dir.listFiles((d, name) -> name.startsWith("companies_") && name.endsWith(".json"));
        assertNotNull(files);
        assertTrue(files.length > 1); // Split attendu
    }

    @Then("each split file should be smaller than {string} bytes")
    public void each_split_file_should_be_smaller_than(String sizeStr) {
        long maxSize = Long.parseLong(sizeStr);
        File dir = new File("src/test/resources/jsons/split/");
        File[] files = dir.listFiles((d, name) -> name.startsWith("companies_") && name.endsWith(".json"));

        for (File file : files) {
            assertTrue(file.length() < maxSize);
        }
    }

}
