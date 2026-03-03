package com.marketpulse.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record JobEnqueueRequest(
    @NotNull UUID workspaceId,
    UUID marketplaceId,
    @NotBlank String jobType,
    @NotNull Instant runAt,
    @NotNull JsonNode payload,
    @Size(max = 200) String dedupeKey
) {}
