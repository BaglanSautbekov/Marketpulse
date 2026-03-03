package com.marketpulse.it;

import com.marketpulse.api.dto.AuthResponse;
import com.marketpulse.api.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthFlowIT extends PostgresTestBase {

  private final TestRestTemplate http;

  public AuthFlowIT(TestRestTemplate http) {
    this.http = http;
  }

  @Test
  void register_returns_token_and_default_workspace() {
    ResponseEntity<AuthResponse> res = http.postForEntity(
        "/api/auth/register",
        new RegisterRequest("test1@mail.com", "password123", "ws"),
        AuthResponse.class
    );

    assertEquals(HttpStatus.CREATED, res.getStatusCode());
    assertNotNull(res.getBody());
    assertNotNull(res.getBody().accessToken());
    assertNotNull(res.getBody().defaultWorkspaceId());
  }
}
