package com.marketpulse.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MarketplaceRepository extends JpaRepository<MarketplaceEntity, UUID> {
  Optional<MarketplaceEntity> findByCode(String code);
}
