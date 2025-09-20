package com.trigger.batch;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("com.trigger")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,summary,html:target/cucumber-reports.html")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.trigger.batch.stepdefs,com.trigger.batch.config")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @Ignore")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
public class CucumberTestRunner {
}
