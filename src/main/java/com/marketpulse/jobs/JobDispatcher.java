package com.marketpulse.jobs;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobDispatcher {

    private final List<JobHandler> handlers;

    public JobDispatcher(List<JobHandler> handlers) {
        this.handlers = handlers;
    }

    public void dispatch(JobQueueDao.ClaimedJob job) {
        for (JobHandler h : handlers) {
            if (h.supports(job.jobType())) {
                h.handle(job);
                return;
            }
        }
        throw JobExecutionException.permanent("unsupported_job_type");
    }
}