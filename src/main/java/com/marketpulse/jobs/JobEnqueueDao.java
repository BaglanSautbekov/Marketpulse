package com.marketpulse.jobs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Repository
public class JobEnqueueDao {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public JobEnqueueDao(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UUID enqueue(UUID workspaceId, UUID marketplaceId, String jobType, Instant runAt, JsonNode payload, String dedupeKey) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid_payload");
        }

        OffsetDateTime runAtUtc = OffsetDateTime.ofInstant(runAt, ZoneOffset.UTC);

        String sql =
                "insert into job_queue(id, workspace_id, marketplace_id, job_type, status, run_at, payload, dedupe_key, attempts, max_attempts, created_at) " +
                        "values (gen_random_uuid(), ?, ?, ?, 'QUEUED', ?, ?::jsonb, ?, 0, 5, now()) " +
                        "on conflict (workspace_id, job_type, dedupe_key) " +
                        "do update set run_at = least(job_queue.run_at, excluded.run_at) " +
                        "returning id";

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> (UUID) rs.getObject("id"),
                workspaceId, marketplaceId, jobType, runAtUtc, payloadJson, dedupeKey
        );
    }
}