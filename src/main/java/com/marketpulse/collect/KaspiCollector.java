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

    public HttpFetchClient.FetchResult collectCategory(String url, Integer page) {
        String u = applyPage(url, page);
        return http.fetch(u);
    }

    public HttpFetchClient.FetchResult collectSearch(String url, Integer page) {
        String u = applyPage(url, page);
        return http.fetch(u);
    }

    public HttpFetchClient.FetchResult collectProduct(String url) {
        return http.fetch(url);
    }

    private static String applyPage(String url, Integer page) {
        if (page == null || page <= 1) return url;

        URI uri = URI.create(url);
        String base = uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
        String q = uri.getQuery();

        String pageParam = "page=" + URLEncoder.encode(String.valueOf(page), StandardCharsets.UTF_8);

        if (q == null || q.isBlank()) return base + "?" + pageParam;

        if (q.contains("page=")) {
            String replaced = q.replaceAll("(^|&)(page=)[^&]*", "$1page=" + page);
            return base + "?" + replaced;
        }

        return base + "?" + q + "&" + pageParam;
    }
}