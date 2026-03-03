package com.marketpulse.collect;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class RawFetchDao {

    private final JdbcTemplate jdbcTemplate;

    public RawFetchDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RawFetchMetaItem> list(UUID workspaceId, UUID marketplaceId, String kind, String cityCode, int limit) {
        int lim = Math.max(1, Math.min(200, limit));

        StringBuilder sql = new StringBuilder("""
      select
        m.id,
        m.workspace_id,
        m.marketplace_id,
        m.kind,
        m.http_status,
        m.checksum,
        m.collected_at,
        m.source_url,
        m.parser_hint,
        p.content_type,
        p.content_encoding,
        p.content_length
      from raw_fetch_meta m
      left join raw_fetch_payload p on p.id = m.id
      where m.workspace_id = ?
    """);

        new Object() {};

        new Object();

        new Object();

        new Object();

        new Object();

        new Object();

        var args = new java.util.ArrayList<Object>();
        args.add(workspaceId);

        if (marketplaceId != null) {
            sql.append(" and m.marketplace_id = ? ");
            args.add(marketplaceId);
        }

        if (kind != null && !kind.isBlank()) {
            sql.append(" and m.kind = ? ");
            args.add(kind.trim());
        }

        if (cityCode != null && !cityCode.isBlank()) {
            sql.append(" and m.source_url like ? ");
            args.add("%c=" + cityCode.trim() + "%");
        }

        sql.append(" order by m.collected_at desc limit ? ");
        args.add(lim);

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapMetaItem(rs), args.toArray());
    }

    public RawFetchMetaItem getMeta(UUID workspaceId, UUID id) {
        List<RawFetchMetaItem> rows = jdbcTemplate.query(
                """
                select
                  m.id,
                  m.workspace_id,
                  m.marketplace_id,
                  m.kind,
                  m.http_status,
                  m.checksum,
                  m.collected_at,
                  m.source_url,
                  m.parser_hint,
                  p.content_type,
                  p.content_encoding,
                  p.content_length
                from raw_fetch_meta m
                left join raw_fetch_payload p on p.id = m.id
                where m.workspace_id = ? and m.id = ?
                """,
                (rs, rowNum) -> mapMetaItem(rs),
                workspaceId, id
        );

        return rows.isEmpty() ? null : rows.get(0);
    }

    public RawFetchPayload getPayload(UUID workspaceId, UUID id) {
        List<RawFetchPayload> rows = jdbcTemplate.query(
                """
                select p.payload, p.content_type, p.content_encoding, p.content_length
                from raw_fetch_meta m
                join raw_fetch_payload p on p.id = m.id
                where m.workspace_id = ? and m.id = ?
                """,
                (rs, rowNum) -> new RawFetchPayload(
                        rs.getBytes("payload"),
                        rs.getString("content_type"),
                        rs.getString("content_encoding"),
                        rs.getInt("content_length")
                ),
                workspaceId, id
        );

        return rows.isEmpty() ? null : rows.get(0);
    }

    private static RawFetchMetaItem mapMetaItem(ResultSet rs) throws java.sql.SQLException {
        return new RawFetchMetaItem(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("workspace_id"),
                (UUID) rs.getObject("marketplace_id"),
                rs.getString("kind"),
                rs.getInt("http_status"),
                rs.getString("checksum"),
                rs.getObject("collected_at", Instant.class),
                rs.getString("source_url"),
                rs.getString("parser_hint"),
                rs.getString("content_type"),
                rs.getString("content_encoding"),
                rs.getInt("content_length")
        );
    }

    public record RawFetchMetaItem(
            UUID id,
            UUID workspaceId,
            UUID marketplaceId,
            String kind,
            int httpStatus,
            String checksum,
            Instant collectedAt,
            String sourceUrl,
            String parserHint,
            String contentType,
            String contentEncoding,
            int contentLength
    ) {}

    public record RawFetchPayload(byte[] payload, String contentType, String contentEncoding, int contentLength) {}
}