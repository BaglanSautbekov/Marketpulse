package com.marketpulse.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.marketpulse.security.UserPrincipal;
import com.marketpulse.service.WorkspaceAccessService;
import com.marketpulse.service.JobService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/jobs")
public class AdminJobsController {

    private final WorkspaceAccessService access;
    private final JobService jobService;

    public AdminJobsController(WorkspaceAccessService access, JobService jobService) {
        this.access = access;
        this.jobService = jobService;
    }

    @PostMapping("/bulk-enqueue")
    public BulkEnqueueResponse bulk(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody BulkEnqueueRequest request) {
        String role = access.requireAnyRole(principal.id(), request.workspaceId());
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) throw new AccessDeniedException("admin_required");

        List<UUID> ids = request.items().stream()
                .map(i -> jobService.enqueue(principal.id(), i.toJobEnqueueRequest(request.workspaceId())))
                .map(r -> r.jobId())
                .toList();

        return new BulkEnqueueResponse(ids);
    }

    public record BulkEnqueueRequest(
            @NotNull UUID workspaceId,
            @NotEmpty List<BulkItem> items
    ) {}

    public record BulkItem(
            @NotNull UUID marketplaceId,
            @NotNull String jobType,
            @NotNull Instant runAt,
            @NotNull JsonNode payload,
            String dedupeKey
    ) {
        public com.marketpulse.api.dto.JobEnqueueRequest toJobEnqueueRequest(UUID workspaceId) {
            return new com.marketpulse.api.dto.JobEnqueueRequest(workspaceId, marketplaceId, jobType, runAt, payload, dedupeKey);
        }
    }

    public record BulkEnqueueResponse(List<UUID> jobIds) {}
}