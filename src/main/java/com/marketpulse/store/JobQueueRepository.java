package com.marketpulse.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobQueueRepository extends JpaRepository<JobQueueEntity, UUID> {}
