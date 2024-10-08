package com.example.Restaurant;


import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
public class TestContainer {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>
            ("postgres:15")
            .withDatabaseName("restaurant")
            .withUsername("postgres")
            .withPassword("user")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }
}
