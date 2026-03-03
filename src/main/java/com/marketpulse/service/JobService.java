package com.marketpulse.service;

import com.marketpulse.api.dto.JobEnqueueRequest;
import com.marketpulse.api.dto.JobEnqueueResponse;
import com.marketpulse.jobs.JobEnqueueDao;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JobService {

  private final WorkspaceAccessService access;
  private final JobEnqueueDao enqueueDao;

  public JobService(WorkspaceAccessService access, JobEnqueueDao enqueueDao) {
    this.access = access;
    this.enqueueDao = enqueueDao;
  }

  public JobEnqueueResponse enqueue(UUID userId, JobEnqueueRequest request) {
    access.requireAnyRole(userId, request.workspaceId());
    UUID jobId = enqueueDao.enqueue(
        request.workspaceId(),
        request.marketplaceId(),
        request.jobType(),
        request.runAt(),
        request.payload(),
        request.dedupeKey()
    );
    return new JobEnqueueResponse(jobId);
  }
}
