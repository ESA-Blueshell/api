package net.blueshell.api.util;

import java.sql.Timestamp;
import java.time.Instant;

public class TimeUtil {

    public static boolean hasExpired(Timestamp timestamp) {
        if (timestamp == null) return false;

        return timestamp.before(Timestamp.from(Instant.now()));
    }

}
