package com.marketpulse.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUserEntity, WorkspaceUserPk> {
  List<WorkspaceUserEntity> findAllByUserId(UUID userId);
}
