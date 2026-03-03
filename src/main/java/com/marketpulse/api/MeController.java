package com.marketpulse.api;

import com.marketpulse.api.dto.MeResponse;
import com.marketpulse.security.UserPrincipal;
import com.marketpulse.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class MeController {

  private final UserService userService;

  public MeController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public MeResponse me(@AuthenticationPrincipal UserPrincipal principal) {
    return userService.me(principal.id());
  }
}
