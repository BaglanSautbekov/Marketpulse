package com.marketpulse.jobs;

public interface JobHandler {
    boolean supports(String jobType);
    void handle(JobQueueDao.ClaimedJob job);
}