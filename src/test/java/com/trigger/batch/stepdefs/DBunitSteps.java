package com.trigger.batch.stepdefs;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

public class DBunitSteps {

    @Autowired
    private IDatabaseTester dbTester;

    @Given("^(.+) datasets$")
    @Transactional
    public void loadDatasets(String datasetsfilename) throws Exception {
        String[] dataSets = datasetsfilename.split(",");
        IDataSet[] idataSets = new IDataSet[dataSets.length];

        for (int i = 0; i < dataSets.length; i++) {
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
            builder.setColumnSensing(true);
            FlatXmlDataSet flatXmlDataSet = builder.build(new ClassPathResource("/datasets/" + dataSets[i] + ".xml").getFile());

            idataSets[i] = new ReplacementDataSet(flatXmlDataSet);
            ((ReplacementDataSet) idataSets[i]).addReplacementObject("[NULL]", null);
        }
        dbTester.setDataSet(new CompositeDataSet(idataSets));
        dbTester.onSetup();
    }

    @After
    public void closeDbTester() throws Exception {
        if (dbTester.getDataSet() != null) {
            dbTester.onTearDown();
        }
    }

}
