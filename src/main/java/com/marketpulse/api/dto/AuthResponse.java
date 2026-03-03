package com.marketpulse.api.dto;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    UUID userId,
    UUID defaultWorkspaceId
) {}
