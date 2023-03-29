package com.onlineshop.productservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
public class PostgresqlContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override public void initialize(final ConfigurableApplicationContext applicationContext) {
        final PostgreSQLContainer container = getPostgresqlContainer();
        container.start();

        TestPropertyValues.of(
            "spring.datasource.url=" + container.getJdbcUrl()
        ).applyTo(applicationContext.getEnvironment());

        log.info("Container initialized in: {}", container.getJdbcUrl());
        log.info("Environment Data: {}", applicationContext.getEnvironment().getProperty("spring.datasource.url"));

    }

    private PostgreSQLContainer getPostgresqlContainer() {
        return new PostgreSQLContainer<>("postgres:10")
            .withDatabaseName("onlineshop")
            .withUsername("postgres")
            .withPassword("postgres");
    }

}
