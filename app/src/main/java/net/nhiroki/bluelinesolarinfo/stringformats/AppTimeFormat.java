package net.nhiroki.bluelinesolarinfo.stringformats;

import android.content.Context;

import net.nhiroki.bluelinesolarinfo.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AppTimeFormat {
    public static String instantToStringForMainActivity(Instant instant, ZoneId zoneId, boolean timeFormat24Hours, Locale locale) {
        if (instant == null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "Hm"), locale);
            String format2312 = LocalTime.of(23, 4, 0).format(timeFormatter);
            if (format2312.length() == 5 && format2312.startsWith("23") && format2312.endsWith("04")) {
                return "--" + format2312.charAt(2) + "--";
            }
            if (format2312.length() == 7 && format2312.startsWith("23 ") && format2312.endsWith(" 04")) {
                return "-- " + format2312.charAt(3) + " --";
            }
            // Fallback
            return "--:--";
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, timeFormat24Hours ? "Hm" : "hma"), locale);

        ZonedDateTime localTime = instant.atZone(zoneId);

        if (timeFormat24Hours && localTime.getHour() == 23 && localTime.getMinute() == 59 && localTime.getSecond() >= 30) {
            String format2300 = LocalTime.of(23, 0, 0).format(timeFormatter);
            if (format2300.length() == 5 && format2300.startsWith("23") && format2300.endsWith("00")) {
                return "24" + format2300.charAt(2) + "00";
            }
            if (format2300.length() == 7 && format2300.startsWith("23 ") && format2300.endsWith(" 00")) {
                return "24 " + format2300.charAt(3) + " 00";
            }
            // Fallback
            return "24:00";
        }
        return instant.plusSeconds(30).atZone(zoneId).format(timeFormatter);
    }

    public static String instantToStringForWidget(Instant instant, ZoneId zoneId, boolean timeFormat24Hours, Locale locale) {
        if (instant == null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "Hm"), locale);
            String format2312 = LocalTime.of(23, 4, 0).format(timeFormatter);
            if (format2312.length() == 5 && format2312.startsWith("23") && format2312.endsWith("04")) {
                return "--" + format2312.charAt(2) + "--";
            }
            if (format2312.length() == 7 && format2312.startsWith("23 ") && format2312.endsWith(" 04")) {
                return "-- " + format2312.charAt(3) + " --";
            }
            // Fallback
            return "--:--";
        }

        if (timeFormat24Hours) {
            String ourPattern = "%02d:%02d";
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "Hm"), locale);
            String format2312 = LocalTime.of(23, 4, 0).format(timeFormatter);
            if (format2312.length() == 5 && format2312.startsWith("23") && format2312.endsWith("04")) {
                ourPattern = "%02d" + format2312.charAt(2) + "%02d";
            }
            if (format2312.length() == 7 && format2312.startsWith("23 ") && format2312.endsWith(" 04")) {
                ourPattern = "%02d " + format2312.charAt(3) + " %02d";
            }
            LocalTime localTime = instant.atZone(zoneId).toLocalTime();
            // explicitly show 24:00 if the nearest minute is the end of the day
            int secOfDay;
            if (localTime.toSecondOfDay() >= 86370) {
                secOfDay = 86400;
            } else {
                LocalTime localTimeRounded = instant.plusSeconds(30).atZone(zoneId).toLocalTime();
                secOfDay = localTimeRounded.toSecondOfDay();
            }
            int hour = secOfDay / 3600;
            int min = (secOfDay % 3600) / 60;
            return String.format(ourPattern, hour, min);

        } else {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "hma"), locale);
            return ZonedDateTime.ofInstant(instant.plusSeconds(30), zoneId).format(timeFormatter);
        }
    }

    public static String instantToHmsStringForRealtime(LocalTime localTime, boolean timeFormat24Hours, Locale locale) {
        if (timeFormat24Hours) {
            String defaultPattern = android.text.format.DateFormat.getBestDateTimePattern(locale, "Hms");

            String ourPattern = "%02d:%02d:%02d";
            if (defaultPattern.equals("HH.mm.ss") || defaultPattern.equals("H.mm.ss")) {
                ourPattern = "%02d.%02d.%02d";
            }
            int sec = localTime.toSecondOfDay();
            return String.format(ourPattern, sec / 3600, (sec % 3600) / 60, sec % 60);

        } else {
            DateTimeFormatter timeFormatterWithSec = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "hmsa"), locale);
            return localTime.format(timeFormatterWithSec);
        }
    }

    public static String degTo24HStr(double deg, Locale locale) {
        String defaultPattern = android.text.format.DateFormat.getBestDateTimePattern(locale, "Hms");

        String ourPattern = "%02d:%02d:%02d";
        if (defaultPattern.equals("HH.mm.ss") || defaultPattern.equals("H.mm.ss")) {
            ourPattern = "%02d.%02d.%02d";
        }
        int sec = (int)(deg / 360.0 * 86400.0);
        return String.format(ourPattern, sec / 3600, (sec % 3600) / 60, sec % 60);
    }

    public static String fullDateTimeForEvent(Instant instant, ZoneId zoneId, boolean timeFormat24Hours, Locale locale) {
        DateTimeFormatter dateTimeFormatter;
        if (locale.getLanguage().equals("ja") && timeFormat24Hours) {
            dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        } else {
            dateTimeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, timeFormat24Hours ? "yMdHm" : "yMdhma"), locale);
        }
        return instant.plusSeconds(30).atZone(zoneId).format(dateTimeFormatter);
    }
}
