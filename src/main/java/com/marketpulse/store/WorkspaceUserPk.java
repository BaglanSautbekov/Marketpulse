package com.marketpulse.store;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@EqualsAndHashCode
public class WorkspaceUserPk implements Serializable {
  private UUID workspaceId;
  private UUID userId;

  public WorkspaceUserPk(UUID workspaceId, UUID userId) {
    this.workspaceId = workspaceId;
    this.userId = userId;
  }
}
