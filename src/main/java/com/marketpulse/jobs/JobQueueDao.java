package com.marketpulse.jobs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class JobQueueDao {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public JobQueueDao(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public record ClaimedJob(UUID id, UUID workspaceId, UUID marketplaceId, String jobType, JsonNode payload, int attempts, int maxAttempts) {}

    @Transactional
    public List<ClaimedJob> claimNextBatch(int batchSize) {
        return jdbcTemplate.query(
                """
                with cte as (
                  select id
                  from job_queue
                  where status = 'QUEUED' and run_at <= now()
                  order by run_at asc, created_at asc
                  limit ?
                  for update skip locked
                )
                update job_queue j
                set status = 'RUNNING', started_at = now(), attempts = attempts + 1
                from cte
                where j.id = cte.id
                returning j.id, j.workspace_id, j.marketplace_id, j.job_type, j.payload::text, j.attempts, j.max_attempts
                """,
                (rs, rowNum) -> {
                    JsonNode payload;
                    try {
                        payload = objectMapper.readTree(rs.getString("payload"));
                    } catch (Exception e) {
                        payload = objectMapper.createObjectNode();
                    }
                    return new ClaimedJob(
                            UUID.fromString(rs.getString("id")),
                            (UUID) rs.getObject("workspace_id"),
                            (UUID) rs.getObject("marketplace_id"),
                            rs.getString("job_type"),
                            payload,
                            rs.getInt("attempts"),
                            rs.getInt("max_attempts")
                    );
                },
                batchSize
        );
    }

    @Transactional
    public void markSucceeded(UUID id) {
        jdbcTemplate.update("update job_queue set status = 'SUCCEEDED', finished_at = now() where id = ?", id);
    }

    @Transactional
    public void markDead(UUID id, String error) {
        jdbcTemplate.update("update job_queue set status = 'DEAD', last_error = ?, finished_at = now() where id = ?", error, id);
    }

    @Transactional
    public void markFailedAndRequeue(UUID id, String error, long delaySeconds) {
        jdbcTemplate.update(
                """
                update job_queue
                set status = 'QUEUED',
                    last_error = ?,
                    run_at = now() + (? || ' seconds')::interval,
                    finished_at = now()
                where id = ?
                """,
                error, String.valueOf(delaySeconds), id
        );
    }
}