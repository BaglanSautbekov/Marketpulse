package com.marketpulse.api;

import com.marketpulse.api.dto.JobEnqueueRequest;
import com.marketpulse.api.dto.JobEnqueueResponse;
import com.marketpulse.security.UserPrincipal;
import com.marketpulse.service.JobService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobsController {

  private final JobService jobService;

  public JobsController(JobService jobService) {
    this.jobService = jobService;
  }

  @PostMapping("/enqueue")
  public JobEnqueueResponse enqueue(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody JobEnqueueRequest request) {
    return jobService.enqueue(principal.id(), request);
  }
}
