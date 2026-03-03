package com.marketpulse.api;

import com.marketpulse.api.dto.AuthResponse;
import com.marketpulse.api.dto.LoginRequest;
import com.marketpulse.api.dto.RegisterRequest;
import com.marketpulse.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request.email(), request.password(), request.workspaceName());
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request.email(), request.password());
  }
}
