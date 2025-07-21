package com.trigger.batch.stepdefs;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;

import com.trigger.batch.step.CompaniesJsonReader;

import io.cucumber.java.en.Given;

public class JsonFileStepDefs {

    @Autowired
    private CompaniesJsonReader reader;

    @Given("the JSON file {string}")
    public void the_json_file(String path) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Accès au champ
        Field field = CompaniesJsonReader.class.getDeclaredField("inputJsonPath");
        field.setAccessible(true);

        // Modifier la valeur du champ
        field.set(reader, path);
    }
}
