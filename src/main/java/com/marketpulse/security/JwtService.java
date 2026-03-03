package com.marketpulse.security;

import com.marketpulse.config.AppProps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtService {

  private final AppProps props;
  private final SecretKey key;

  public JwtService(AppProps props) {
    this.props = props;
    String secret = props.security().jwt().secret();
    if (secret == null || secret.isBlank()) throw new IllegalStateException("jwt_secret_missing");
    if (secret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalStateException("jwt_secret_too_short");
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String issueAccessToken(UUID userId, String email) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(props.security().jwt().accessTokenMinutes() * 60L);
    return Jwts.builder()
        .issuer(props.security().jwt().issuer())
        .subject(userId.toString())
        .claim("email", email)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }

  public Optional<JwtUser> parse(String token) {
    try {
      Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
      String sub = claims.getSubject();
      String email = claims.get("email", String.class);
      if (sub == null || email == null) return Optional.empty();
      return Optional.of(new JwtUser(UUID.fromString(sub), email));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public record JwtUser(UUID userId, String email) {}
}
