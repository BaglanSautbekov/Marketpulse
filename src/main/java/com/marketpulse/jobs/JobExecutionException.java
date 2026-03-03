package com.marketpulse.jobs;

public class JobExecutionException extends RuntimeException {
    private final boolean permanent;

    public JobExecutionException(String message, boolean permanent) {
        super(message);
        this.permanent = permanent;
    }

    public JobExecutionException(String message, Throwable cause, boolean permanent) {
        super(message, cause);
        this.permanent = permanent;
    }

    public boolean permanent() {
        return permanent;
    }

    public static JobExecutionException permanent(String message) {
        return new JobExecutionException(message, true);
    }

    public static JobExecutionException retryable(String message) {
        return new JobExecutionException(message, false);
    }

    public static JobExecutionException retryable(String message, Throwable cause) {
        return new JobExecutionException(message, cause, false);
    }
}