package com.marketpulse.service;

import com.marketpulse.store.WorkspaceUserPk;
import com.marketpulse.store.WorkspaceUserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspaceAccessService {

  private final WorkspaceUserRepository workspaceUsers;

  public WorkspaceAccessService(WorkspaceUserRepository workspaceUsers) {
    this.workspaceUsers = workspaceUsers;
  }

  public String requireAnyRole(UUID userId, UUID workspaceId) {
    return workspaceUsers.findById(new WorkspaceUserPk(workspaceId, userId))
        .map(x -> x.getRole())
        .orElseThrow(() -> new AccessDeniedException("workspace_access_denied"));
  }
}
