package com.morango.chat.chatapplication;

import android.app.Application;

public class LastSeenTime extends Application {

    private static final int SECOND_MILLS = 1000;
    private static final int MIN_MILLS = SECOND_MILLS * 60;
    private static final int HOUR_MILLS = MIN_MILLS * 60;
    private static final int DAY_MILLS = HOUR_MILLS * 24;


    public static String getTimeAgo(long time) {

        if (time < 1000000000000L) {
            time *= 1000;

        }

        long now = System.currentTimeMillis();

        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;

        if (diff < MIN_MILLS) {
            return "just now";
        } else if (diff < (MIN_MILLS * 2)) {
            return "last seen a minute ago";
        } else if (diff < (MIN_MILLS * 50)) {
            return "last seen" + diff / MIN_MILLS + " minutes ago";
        } else if (diff < (MIN_MILLS * 90)) {
            return "last seen an hour ago";
        } else if (diff < (HOUR_MILLS * 24)) {
            return "last seen" + diff / HOUR_MILLS + " hours ago";
        } else if (diff < (HOUR_MILLS * 48)) {
            return "last seen yesterday";
        } else {
            return "last seen" + diff / DAY_MILLS + " days ago";
        }
    }
}
