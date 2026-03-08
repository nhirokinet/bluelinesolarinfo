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

        return instant.plusSeconds(30).atZone(zoneId).format(timeFormatter);
    }

    public static String instantToStringForWidget(Context context, Instant instant, ZoneId zoneId, boolean timeFormat24Hours) {
        if (instant == null) {
            return context.getString(R.string.time_event_not_occur_hm);
        }

        ZonedDateTime localTime = ZonedDateTime.ofInstant(instant.plusSeconds(30), zoneId);

        if (timeFormat24Hours) {
            return context.getString(R.string.widget_solar_info_today_24h_format, localTime.getHour(), localTime.getMinute());
        } else {
            Locale locale = context.getResources().getConfiguration().getLocales().get(0);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "hma"), locale);
            return localTime.format(timeFormatter);
        }
    }

    public static String degTo24HStr(Context context, double deg) {
        int sec = (int)(deg / 360.0 * 86400.0);
        return context.getString(R.string.main_activity_solar_info_now_hms_24h_format, sec / 3600, (sec % 3600) / 60, sec % 60);
    };
}
