package com.marketpulse.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "marketplaces")
public class MarketplaceEntity {
  @Id
  private UUID id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(name = "is_enabled", nullable = false)
  private boolean enabled;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
