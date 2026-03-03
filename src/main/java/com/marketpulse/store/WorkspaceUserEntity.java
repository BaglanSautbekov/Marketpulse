package com.marketpulse.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "workspace_users")
@IdClass(WorkspaceUserPk.class)
public class WorkspaceUserEntity {

  @Id
  @Column(name = "workspace_id", nullable = false)
  private UUID workspaceId;

  @Id
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private String role;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
