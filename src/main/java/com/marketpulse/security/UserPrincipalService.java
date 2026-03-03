package com.marketpulse.security;

import com.marketpulse.store.UserEntity;
import com.marketpulse.store.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserPrincipalService implements UserDetailsService {

  private final UserRepository users;

  public UserPrincipalService(UserRepository users) {
    this.users = users;
  }

  @Override
  public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = users.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("user_not_found"));
    return new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), user.isActive());
  }

  public UserPrincipal loadById(UUID userId) {
    UserEntity user = users.findById(userId).orElseThrow(() -> new UsernameNotFoundException("user_not_found"));
    return new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), user.isActive());
  }
}
