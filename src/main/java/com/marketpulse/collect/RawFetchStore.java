package com.marketpulse.collect;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Repository
public class RawFetchStore {

    private final JdbcTemplate jdbcTemplate;

    public RawFetchStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public UUID save(UUID workspaceId, UUID marketplaceId, String kind, String sourceUrl, int httpStatus, String checksum, Instant collectedAt, String parserHint,
                     byte[] payload, String contentType, String contentEncoding) {

        UUID id = UUID.randomUUID();
        Timestamp collectedAtTs = Timestamp.from(collectedAt);

        jdbcTemplate.update(
                """
                insert into raw_fetch_meta(id, workspace_id, marketplace_id, kind, source_url, storage_key, http_status, checksum, collected_at, parser_hint)
                values (?, ?, ?, ?, ?, null, ?, ?, ?, ?)
                """,
                id, workspaceId, marketplaceId, kind, sourceUrl, httpStatus, checksum, collectedAtTs, parserHint
        );

        jdbcTemplate.update(
                """
                insert into raw_fetch_payload(id, payload, content_type, content_encoding, content_length)
                values (?, ?, ?, ?, ?)
                """,
                id, payload, contentType, contentEncoding, payload.length
        );

        return id;
    }
}