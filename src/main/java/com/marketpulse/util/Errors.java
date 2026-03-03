package com.marketpulse.util;

public final class Errors {

    public static String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        String msg = cur.getMessage();
        if (msg == null || msg.isBlank()) msg = cur.getClass().getName();
        return msg;
    }

    private Errors() {}
}