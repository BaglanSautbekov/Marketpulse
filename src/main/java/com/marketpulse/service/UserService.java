package com.marketpulse.service;

import com.marketpulse.api.dto.MeResponse;
import com.marketpulse.store.UserRepository;
import com.marketpulse.store.WorkspaceRepository;
import com.marketpulse.store.WorkspaceUserEntity;
import com.marketpulse.store.WorkspaceUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

  private final UserRepository users;
  private final WorkspaceRepository workspaces;
  private final WorkspaceUserRepository workspaceUsers;

  public UserService(UserRepository users, WorkspaceRepository workspaces, WorkspaceUserRepository workspaceUsers) {
    this.users = users;
    this.workspaces = workspaces;
    this.workspaceUsers = workspaceUsers;
  }

  public MeResponse me(UUID userId) {
    var user = users.findById(userId).orElseThrow(() -> new IllegalStateException("user_not_found"));
    List<WorkspaceUserEntity> rels = workspaceUsers.findAllByUserId(userId);
    List<MeResponse.WorkspaceItem> ws = rels.stream()
        .map(r -> workspaces.findById(r.getWorkspaceId())
            .map(w -> new MeResponse.WorkspaceItem(w.getId(), w.getName(), r.getRole()))
            .orElse(null))
        .filter(x -> x != null)
        .toList();
    return new MeResponse(user.getId(), user.getEmail(), ws);
  }
}
