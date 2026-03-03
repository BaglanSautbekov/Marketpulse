package com.marketpulse.jobs.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.marketpulse.collect.KaspiCollector;
import com.marketpulse.collect.RawFetchStore;
import com.marketpulse.jobs.JobExecutionException;
import com.marketpulse.jobs.JobHandler;
import com.marketpulse.jobs.JobQueueDao;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class KaspiCollectSearchJobHandler implements JobHandler {

    private final KaspiCollector collector;
    private final RawFetchStore store;

    public KaspiCollectSearchJobHandler(KaspiCollector collector, RawFetchStore store) {
        this.collector = collector;
        this.store = store;
    }

    @Override
    public boolean supports(String jobType) {
        return "COLLECT_SEARCH".equals(jobType);
    }

    @Override
    public void handle(JobQueueDao.ClaimedJob job) {
        UUID workspaceId = job.workspaceId();
        UUID marketplaceId = job.marketplaceId();
        if (workspaceId == null) throw JobExecutionException.permanent("workspace_id_required");
        if (marketplaceId == null) throw JobExecutionException.permanent("marketplace_id_required");

        JsonNode p = job.payload();
        String url = text(p, "url");
        Integer page = intOrNull(p, "page");
        String cityCode = text(p, "cityCode");

        if (url == null || url.isBlank()) throw JobExecutionException.permanent("payload.url_required");

        var collected = collector.collectSearch(url, page, cityCode);
        var res = collected.response();

        byte[] body = res.body();
        String checksum = sha256(body);

        store.save(
                workspaceId,
                marketplaceId,
                "SEARCH",
                collected.usedUrl(),
                res.statusCode(),
                checksum,
                Instant.now(),
                "kaspi:search",
                body,
                res.contentType(),
                res.contentEncoding()
        );

        throwForStatus(res.statusCode());
    }

    private static void throwForStatus(int status) {
        if (status == 429) throw JobExecutionException.retryable("rate_limited");
        if (status >= 500) throw JobExecutionException.retryable("upstream_5xx");
        if (status == 404) throw JobExecutionException.permanent("not_found");
        if (status == 403) throw JobExecutionException.retryable("forbidden");
        if (status >= 400) throw JobExecutionException.permanent("upstream_4xx");
    }

    private static String text(JsonNode p, String key) {
        JsonNode n = p.get(key);
        if (n == null || n.isNull()) return null;
        return n.asText();
    }

    private static Integer intOrNull(JsonNode p, String key) {
        JsonNode n = p.get(key);
        if (n == null || n.isNull()) return null;
        if (n.isInt()) return n.asInt();
        try {
            return Integer.parseInt(n.asText());
        } catch (Exception e) {
            return null;
        }
    }

    private static String sha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(bytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}