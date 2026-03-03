package com.marketpulse.service;

import com.marketpulse.api.dto.AuthResponse;
import com.marketpulse.security.JwtService;
import com.marketpulse.store.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository users;
    private final WorkspaceRepository workspaces;
    private final WorkspaceUserRepository workspaceUsers;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthService(
            UserRepository users,
            WorkspaceRepository workspaces,
            WorkspaceUserRepository workspaceUsers,
            PasswordEncoder encoder,
            JwtService jwtService,
            AuthenticationManager authManager
    ) {
        this.users = users;
        this.workspaces = workspaces;
        this.workspaceUsers = workspaceUsers;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    @Transactional
    public AuthResponse register(String email, String password, String workspaceName) {
        Instant now = Instant.now();

        UserEntity user = new UserEntity();
        user.setEmail(email.trim());
        user.setPasswordHash(encoder.encode(password));
        user.setActive(true);

        WorkspaceEntity ws = new WorkspaceEntity();
        ws.setName(workspaceName.trim());
        ws.setPlan("FREE");

        try {
            users.saveAndFlush(user);
            workspaces.saveAndFlush(ws);

            WorkspaceUserEntity rel = new WorkspaceUserEntity();
            rel.setWorkspaceId(ws.getId());
            rel.setUserId(user.getId());
            rel.setRole("OWNER");
            rel.setCreatedAt(now);
            workspaceUsers.saveAndFlush(rel);

            user.setDefaultWorkspaceId(ws.getId());
            users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("email_already_exists");
        }

        String token = jwtService.issueAccessToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getDefaultWorkspaceId());
    }

    public AuthResponse login(String email, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email.trim(), password));
        UserEntity user = users.findByEmail(email.trim()).orElseThrow(() -> new IllegalStateException("user_not_found"));
        String token = jwtService.issueAccessToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getDefaultWorkspaceId());
    }
}