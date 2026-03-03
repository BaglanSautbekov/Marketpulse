package com.marketpulse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProps(Security security, Jobs jobs) {
  public record Security(Jwt jwt) {
    public record Jwt(String issuer, String secret, long accessTokenMinutes) {}
  }
  public record Jobs(Runner runner) {
    public record Runner(boolean enabled, int batchSize, long fixedDelayMs) {}
  }
}
