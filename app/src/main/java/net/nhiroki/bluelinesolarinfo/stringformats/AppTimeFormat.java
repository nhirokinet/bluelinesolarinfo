package net.nhiroki.bluelinesolarinfo.stringformats;

import android.content.Context;

import net.nhiroki.bluelinesolarinfo.R;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AppTimeFormat {
    public static String instantToStringForMainActivity(Context context, Instant instant, ZoneId zoneId, boolean timeFormat24Hours) {
        if (instant == null) {
            return context.getString(R.string.time_event_not_occur_hm);
        }

        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, timeFormat24Hours ? "Hm" : "hma"), locale);

        ZonedDateTime localTime = instant.atZone(zoneId);

        if (timeFormat24Hours && localTime.getHour() == 23 && localTime.getMinute() == 59 && localTime.getSecond() >= 30) {
            return context.getString(R.string.time_24_oclock);
        }
        return instant.plusSeconds(30).atZone(zoneId).format(timeFormatter);
    }

    public static String instantToStringForWidget(Context context, Instant instant, ZoneId zoneId, boolean timeFormat24Hours) {
        if (instant == null) {
            return context.getString(R.string.time_event_not_occur_hm);
        }


        if (timeFormat24Hours) {
            ZonedDateTime localTime = instant.atZone(zoneId);
            int hour = localTime.getHour();
            int minute = localTime.getMinute();
            if (localTime.getSecond() >= 30) {
                minute += 1;
                if (minute == 60) {
                    minute = 0;
                    hour += 1;
                }
            }
            return context.getString(R.string.widget_solar_info_today_24h_format, hour, minute);

        } else {
            Locale locale = context.getResources().getConfiguration().getLocales().get(0);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "hma"), locale);
            return ZonedDateTime.ofInstant(instant.plusSeconds(30), zoneId).format(timeFormatter);
        }
    }

    public static String degTo24HStr(Context context, double deg) {
        int sec = (int)(deg / 360.0 * 86400.0);
        return context.getString(R.string.main_activity_solar_info_now_hms_24h_format, sec / 3600, (sec % 3600) / 60, sec % 60);
    };
}
