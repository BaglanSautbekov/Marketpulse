package com.marketpulse.collect;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@Component
public class HttpFetchClient {

    private final HttpClient client;

    public HttpFetchClient() {
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public FetchResult fetch(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.7,en;q=0.6")
                    .header("Accept-Encoding", "gzip")
                    .GET()
                    .build();

            HttpResponse<byte[]> res = client.send(req, HttpResponse.BodyHandlers.ofByteArray());

            String contentType = header(res, "content-type").orElse(null);
            String contentEncoding = header(res, "content-encoding").orElse(null);

            byte[] body = res.body() == null ? new byte[0] : res.body();
            if (contentEncoding != null && contentEncoding.toLowerCase().contains("gzip")) {
                body = gunzip(body);
            }

            return new FetchResult(res.statusCode(), body, contentType, contentEncoding);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() == null ? e.getClass().getName() : e.getMessage(), e);
        }
    }

    private static Optional<String> header(HttpResponse<?> res, String name) {
        return res.headers().firstValue(name);
    }

    private static byte[] gunzip(byte[] gz) {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(gz));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) >= 0) {
                out.write(buf, 0, r);
            }
            return out.toByteArray();
        } catch (Exception e) {
            return gz;
        }
    }

    public record FetchResult(int statusCode, byte[] body, String contentType, String contentEncoding) {}
}