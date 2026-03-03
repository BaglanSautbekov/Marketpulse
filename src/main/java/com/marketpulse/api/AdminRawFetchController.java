package com.marketpulse.api;

import com.marketpulse.collect.RawFetchDao;
import com.marketpulse.security.UserPrincipal;
import com.marketpulse.service.WorkspaceAccessService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/admin/raw-fetches")
public class AdminRawFetchController {

    private final WorkspaceAccessService access;
    private final RawFetchDao rawFetchDao;

    public AdminRawFetchController(WorkspaceAccessService access, RawFetchDao rawFetchDao) {
        this.access = access;
        this.rawFetchDao = rawFetchDao;
    }

    @GetMapping
    public List<RawFetchDao.RawFetchMetaItem> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID marketplaceId,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String cityCode,
            @RequestParam(defaultValue = "50") int limit
    ) {
        requireAdmin(principal.id(), workspaceId);
        return rawFetchDao.list(workspaceId, marketplaceId, kind, cityCode, limit);
    }

    @GetMapping("/{id}/meta")
    public RawFetchDao.RawFetchMetaItem meta(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam UUID workspaceId,
            @PathVariable UUID id
    ) {
        requireAdmin(principal.id(), workspaceId);
        RawFetchDao.RawFetchMetaItem meta = rawFetchDao.getMeta(workspaceId, id);
        if (meta == null) throw new ResponseStatusException(NOT_FOUND, "raw_not_found");
        return meta;
    }

    @GetMapping("/{id}/payload")
    public ResponseEntity<byte[]> payload(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam UUID workspaceId,
            @PathVariable UUID id
    ) {
        requireAdmin(principal.id(), workspaceId);
        RawFetchDao.RawFetchPayload p = rawFetchDao.getPayload(workspaceId, id);
        if (p == null) throw new ResponseStatusException(NOT_FOUND, "raw_payload_not_found");

        String ct = (p.contentType() == null || p.contentType().isBlank()) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : p.contentType();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, ct);
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(p.payload().length));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"raw-" + id + ".bin\"");

        return ResponseEntity.ok().headers(headers).body(p.payload());
    }

    private void requireAdmin(UUID userId, UUID workspaceId) {
        String role = access.requireAnyRole(userId, workspaceId);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) throw new AccessDeniedException("admin_required");
    }
}