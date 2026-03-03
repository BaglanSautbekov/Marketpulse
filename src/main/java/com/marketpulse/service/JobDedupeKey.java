package com.marketpulse.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class JobDedupeKey {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC);

    public static String compute(UUID marketplaceId, String jobType, Instant runAt, JsonNode payload) {
        String day = DAY.format(runAt);
        String url = text(payload, "url");
        String page = text(payload, "page");
        if ("COLLECT_PRODUCT".equals(jobType)) {
            if (url == null || url.isBlank()) return null;
            return "mp:" + marketplaceId + ":prod:" + hashShort(url) + ":" + day;
        }
        if ("COLLECT_CATEGORY".equals(jobType) || "COLLECT_SEARCH".equals(jobType)) {
            if (url == null || url.isBlank()) return null;
            String p = (page == null || page.isBlank()) ? "1" : page;
            return "mp:" + marketplaceId + ":" + (jobType.equals("COLLECT_CATEGORY") ? "cat" : "search") + ":" + hashShort(url) + ":p" + p + ":" + day;
        }
        return null;
    }

    private static String text(JsonNode p, String key) {
        if (p == null) return null;
        JsonNode n = p.get(key);
        if (n == null || n.isNull()) return null;
        return n.asText();
    }

    private static String hashShort(String s) {
        int h = s.hashCode();
        return Integer.toUnsignedString(h, 16);
    }

    private JobDedupeKey() {}
}