package com.onlineshop.productservice;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresqlContainerinitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override public void initialize(final ConfigurableApplicationContext applicationContext) {
        final PostgreSQLContainer container = getPostgresqlContainer();
        container.start();
        TestPropertyValues.of("spring.datasource.url=" + container.getJdbcUrl()).applyTo(applicationContext.getEnvironment());
    }

    private PostgreSQLContainer getPostgresqlContainer() {
        return new PostgreSQLContainer<>("postgres:10")
            .withDatabaseName("onlineshop")
            .withUsername("user")
            .withPassword("user");
    }

}
