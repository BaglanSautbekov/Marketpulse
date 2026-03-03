package com.marketpulse.it;

import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class PostgresTestBase {

  static final PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16")
      .withDatabaseName("marketpulse")
      .withUsername("marketpulse")
      .withPassword("marketpulse");

  static {
    pg.start();
  }

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", pg::getJdbcUrl);
    r.add("spring.datasource.username", pg::getUsername);
    r.add("spring.datasource.password", pg::getPassword);
    r.add("JWT_SECRET", () -> "change-me-please-change-me-please-change-me-please");
  }
}
