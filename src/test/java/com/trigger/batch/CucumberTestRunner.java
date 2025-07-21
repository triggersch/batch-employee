package com.trigger.batch;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = { "pretty", "summary", "html:target/cucumber-reports.html" },
    features = "src/test/resources/features",
    glue = {
        "com.trigger.batch.stepdefs",     // Tes step definitions
        "com.trigger.batch.config"     // Ta config Cucumber/Spring Batch si besoin
    },
    tags = "not @Ignore", // Permet d'ignorer certains tests
    publish = false
)
public class CucumberTestRunner {
}
