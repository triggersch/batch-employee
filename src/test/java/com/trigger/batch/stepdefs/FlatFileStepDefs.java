package com.trigger.batch.stepdefs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;

import com.trigger.batch.step.CobolFlatFileSplitterTasklet;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class FlatFileStepDefs {

    @Autowired
    private Tasklet cobolFlatFileSplitterTasklet;

    @Given("the Dat file {string}")
    public void the_dat_file(String path)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Accès au champ
        Field field = CobolFlatFileSplitterTasklet.class.getDeclaredField("inputFlatPath");
        field.setAccessible(true);

        // Modifier la valeur du champ
        field.set(cobolFlatFileSplitterTasklet, path);
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

}
