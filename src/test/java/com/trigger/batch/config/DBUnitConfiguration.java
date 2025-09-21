package com.trigger.batch.config;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

import jakarta.annotation.PostConstruct;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.TimeZone;

@Configuration
public class DBUnitConfiguration {

    private static final String EUROPE_PARIS_TIMEZONE = "Europe/Paris";

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public static void init() {
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone(EUROPE_PARIS_TIMEZONE));
    }

    @Bean
    DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean dbConfig = new DatabaseConfigBean();
        dbConfig.setDatatypeFactory(new H2DataTypeFactory());
        dbConfig.setQualifiedTableNames(false);
        dbConfig.setAllowEmptyFields(true);
        return dbConfig;
    }

    @Bean
    DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        DatabaseDataSourceConnectionFactoryBean dbConnection = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        dbConnection.setDatabaseConfig(dbUnitDatabaseConfig());
        return dbConnection;
    }

    @Bean
    IDatabaseTester dbTester() throws Exception {
        DefaultDatabaseTester defaultDatabaseTester = new DefaultDatabaseTester(dbUnitDatabaseConnection().getObject());
        defaultDatabaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        defaultDatabaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        return defaultDatabaseTester;
    }

}