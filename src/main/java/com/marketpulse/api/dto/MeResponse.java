package com.marketpulse.api.dto;

import java.util.List;
import java.util.UUID;

public record MeResponse(
    UUID userId,
    String email,
    List<WorkspaceItem> workspaces
) {
  public record WorkspaceItem(UUID id, String name, String role) {}
}
