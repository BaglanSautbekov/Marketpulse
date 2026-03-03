package com.marketpulse.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserPrincipalService principalService;

  public JwtAuthFilter(JwtService jwtService, UserPrincipalService principalService) {
    this.jwtService = jwtService;
    this.principalService = principalService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7).trim();
    jwtService.parse(token).ifPresent(jwtUser -> {
      if (SecurityContextHolder.getContext().getAuthentication() != null) return;
      UserPrincipal principal = principalService.loadById(jwtUser.userId());
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
      auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(auth);
    });

    filterChain.doFilter(request, response);
  }
}
