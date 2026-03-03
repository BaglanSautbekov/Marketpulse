package com.marketpulse.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class JobDedupeKey {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC);

    public static String compute(UUID marketplaceId, String jobType, Instant runAt, JsonNode payload) {
        String day = DAY.format(runAt);

        String rawUrl = text(payload, "url");
        if (rawUrl == null || rawUrl.isBlank()) return null;

        String cityCode = text(payload, "cityCode");
        if (cityCode == null || cityCode.isBlank()) {
            cityCode = queryParam(rawUrl, "c");
        }
        String city = (cityCode == null || cityCode.isBlank()) ? "c0" : ("c" + cityCode.trim());

        String page = text(payload, "page");
        if (page == null || page.isBlank()) {
            page = queryParam(rawUrl, "page");
        }
        String p = (page == null || page.isBlank()) ? "1" : page;

        String normUrl = normalizeUrlForDedupe(rawUrl);
        String h = hashShort(normUrl);

        if ("COLLECT_PRODUCT".equals(jobType)) {
            return "mp:" + marketplaceId + ":prod:" + h + ":" + city + ":" + day;
        }

        if ("COLLECT_CATEGORY".equals(jobType)) {
            return "mp:" + marketplaceId + ":cat:" + h + ":" + city + ":p" + p + ":" + day;
        }

        if ("COLLECT_SEARCH".equals(jobType)) {
            return "mp:" + marketplaceId + ":search:" + h + ":" + city + ":p" + p + ":" + day;
        }

        return null;
    }

    private static String normalizeUrlForDedupe(String url) {
        URI uri = URI.create(url);
        String base = uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
        Map<String, List<String>> qp = parseQuery(uri.getQuery());

        qp.remove("c");
        qp.remove("page");

        if (qp.isEmpty()) return base;

        List<String> parts = new ArrayList<>();
        for (var e : qp.entrySet()) {
            String k = e.getKey();
            for (String v : e.getValue()) {
                parts.add(k + "=" + v);
            }
        }
        parts.sort(Comparator.naturalOrder());
        return base + "?" + String.join("&", parts);
    }

    private static Map<String, List<String>> parseQuery(String query) {
        Map<String, List<String>> map = new HashMap<>();
        if (query == null || query.isBlank()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair.isBlank()) continue;
            int idx = pair.indexOf('=');
            String k = idx >= 0 ? pair.substring(0, idx) : pair;
            String v = idx >= 0 ? pair.substring(idx + 1) : "";
            map.computeIfAbsent(k, __ -> new ArrayList<>()).add(v);
        }
        return map;
    }

    private static String queryParam(String url, String key) {
        try {
            URI uri = URI.create(url);
            Map<String, List<String>> qp = parseQuery(uri.getQuery());
            List<String> v = qp.get(key);
            if (v == null || v.isEmpty()) return null;
            String s = v.get(0);
            return s == null || s.isBlank() ? null : s;
        } catch (Exception e) {
            return null;
        }
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