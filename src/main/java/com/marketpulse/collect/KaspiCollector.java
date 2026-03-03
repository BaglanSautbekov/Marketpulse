package com.marketpulse.collect;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class KaspiCollector {

    private final HttpFetchClient http;

    public KaspiCollector(HttpFetchClient http) {
        this.http = http;
    }

    public CollectResult collectCategory(String url, Integer page, String cityCode) {
        String u = applyCity(url, cityCode);
        u = applyPage(u, page);
        return new CollectResult(u, http.fetch(u));
    }

    public CollectResult collectSearch(String url, Integer page, String cityCode) {
        String u = applyCity(url, cityCode);
        u = applyPage(u, page);
        return new CollectResult(u, http.fetch(u));
    }

    public CollectResult collectProduct(String url, String cityCode) {
        String u = applyCity(url, cityCode);
        return new CollectResult(u, http.fetch(u));
    }

    private static String applyCity(String url, String cityCode) {
        if (cityCode == null || cityCode.isBlank()) return url;
        return upsertQueryParam(url, "c", cityCode.trim());
    }

    private static String applyPage(String url, Integer page) {
        if (page == null || page <= 1) return url;
        return upsertQueryParam(url, "page", String.valueOf(page));
    }

    private static String upsertQueryParam(String url, String key, String value) {
        URI uri = URI.create(url);
        String base = uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
        String q = uri.getQuery();

        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        String kv = key + "=" + encoded;

        if (q == null || q.isBlank()) return base + "?" + kv;

        if (q.contains(key + "=")) {
            String replaced = q.replaceAll("(^|&)" + key + "=[^&]*", "$1" + key + "=" + encoded);
            return base + "?" + replaced;
        }

        return base + "?" + q + "&" + kv;
    }

    public record CollectResult(String usedUrl, HttpFetchClient.FetchResult response) {}
}