package com.marketpulse.store;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "job_queue")
public class JobQueueEntity {
  @Id
  @UuidGenerator
  private UUID id;

  @Column(name = "workspace_id")
  private UUID workspaceId;

  @Column(name = "marketplace_id")
  private UUID marketplaceId;

  @Column(name = "job_type", nullable = false)
  private String jobType;

  @Column(nullable = false)
  private String status;

  @Column(name = "run_at", nullable = false)
  private Instant runAt;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb", nullable = false)
  private JsonNode payload;

  @Column(name = "dedupe_key")
  private String dedupeKey;

  @Column(nullable = false)
  private int attempts;

  @Column(name = "max_attempts", nullable = false)
  private int maxAttempts;

  @Column(name = "last_error")
  private String lastError;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "started_at")
  private Instant startedAt;

  @Column(name = "finished_at")
  private Instant finishedAt;
}
