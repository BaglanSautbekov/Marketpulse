package com.marketpulse.jobs;

import com.marketpulse.config.AppProps;
import com.marketpulse.util.Errors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobRunner {

    private final AppProps props;
    private final JobQueueDao jobQueueDao;
    private final JobDispatcher dispatcher;

    public JobRunner(AppProps props, JobQueueDao jobQueueDao, JobDispatcher dispatcher) {
        this.props = props;
        this.jobQueueDao = jobQueueDao;
        this.dispatcher = dispatcher;
    }

    @Scheduled(fixedDelayString = "${app.jobs.runner.fixedDelayMs:3000}")
    public void tick() {
        if (!props.jobs().runner().enabled()) return;

        List<JobQueueDao.ClaimedJob> claimed = jobQueueDao.claimNextBatch(props.jobs().runner().batchSize());
        for (JobQueueDao.ClaimedJob job : claimed) {
            try {
                dispatcher.dispatch(job);
                jobQueueDao.markSucceeded(job.id());
            } catch (JobExecutionException e) {
                String err = Errors.rootMessage(e);
                if (e.permanent() || job.attempts() >= job.maxAttempts()) {
                    jobQueueDao.markDead(job.id(), err);
                } else {
                    jobQueueDao.markFailedAndRequeue(job.id(), err, backoffSeconds(job.attempts()));
                }
            } catch (Exception e) {
                String err = Errors.rootMessage(e);
                if (job.attempts() >= job.maxAttempts()) {
                    jobQueueDao.markDead(job.id(), err);
                } else {
                    jobQueueDao.markFailedAndRequeue(job.id(), err, backoffSeconds(job.attempts()));
                }
            }
        }
    }

    private static long backoffSeconds(int attempts) {
        long base = 10L * (1L << Math.min(5, Math.max(0, attempts)));
        return Math.min(300L, base);
    }
}