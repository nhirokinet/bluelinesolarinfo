package net.nhiroki.bluelinesolarinfo.stringformats;

import android.app.LocaleManager;
import android.content.Context;
import android.os.Build;
import android.os.LocaleList;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AppTimeFormatTest {
    @Test
    public void instantToStringForMainActivityTest() {
        Locale locale = Locale.US;

        assertEquals("00:00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("06:12", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("06:13", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("23:59", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("24:00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("24:00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), true, locale));

        assertEquals("15:12", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("05:12", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T20:12:29Z"), ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("15:42", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-07-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true, locale));
        assertEquals("16:42", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true, locale));

        assertEquals("02:59", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-04-04T15:59:29Z"), ZoneId.of("Australia/Sydney"), true, locale));
        assertEquals("02:00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-04-04T15:59:30Z"), ZoneId.of("Australia/Sydney"), true, locale));
        assertEquals("02:00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-04-04T16:00:00Z"), ZoneId.of("Australia/Sydney"), true, locale));

        assertEquals("12:00 AM", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("6:12 AM", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("6:13 AM", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("11:59 AM", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T11:59:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("12:00 PM", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T11:59:30Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("11:59 PM", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("12:00 AM", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), false, locale));

        assertEquals("--:--", AppTimeFormat.instantToStringForMainActivity(null, ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("--:--", AppTimeFormat.instantToStringForMainActivity(null, ZoneId.of("Asia/Tokyo"), false, locale));
    }

    @Test
    public void instantToStringForMainActivityTestWithLocale() {
        // Some locales do not exist in older versions
        Assume.assumeTrue(Build.VERSION.SDK_INT >= 36);

        assertEquals("0:00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("12:34", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("23:59", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("24:00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("--:--", AppTimeFormat.instantToStringForMainActivity(null, ZoneId.of("UTC"), true, Locale.JAPAN));

        assertEquals("午前0:00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), false, Locale.JAPAN));
        assertEquals("--:--", AppTimeFormat.instantToStringForMainActivity(null, ZoneId.of("UTC"), false, Locale.JAPAN));

        assertEquals("0.00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("12.34", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("23.59", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("24.00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("--.--", AppTimeFormat.instantToStringForMainActivity(null, ZoneId.of("UTC"), true, Locale.of("fi")));

        assertEquals("00 h 00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("12 h 34", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("23 h 59", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("24 h 00", AppTimeFormat.instantToStringForMainActivity(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("-- h --", AppTimeFormat.instantToStringForMainActivity(null, ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
    }

    @Test
    public void instantToStringForWidgetTest() {
        Locale locale = Locale.US;

        assertEquals("00:00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("06:12", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("06:13", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("23:59", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("24:00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("24:00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), true, locale));

        assertEquals("15:12", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("05:12", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T20:12:29Z"), ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("15:42", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-07-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true, locale));
        assertEquals("16:42", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true, locale));

        assertEquals("02:59", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-04-04T15:59:29Z"), ZoneId.of("Australia/Sydney"), true, locale));
        assertEquals("02:00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-04-04T15:59:30Z"), ZoneId.of("Australia/Sydney"), true, locale));
        assertEquals("02:00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-04-04T16:00:00Z"), ZoneId.of("Australia/Sydney"), true, locale));

        assertEquals("12:00 AM", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("6:12 AM", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("6:13 AM", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("11:59 AM", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T11:59:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("12:00 PM", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T11:59:30Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("11:59 PM", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("12:00 AM", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), false, locale));

        assertEquals("--:--", AppTimeFormat.instantToStringForWidget(null, ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("--:--", AppTimeFormat.instantToStringForWidget(null, ZoneId.of("Asia/Tokyo"), false, locale));
    }

    @Test
    public void instantToStringForWidgetTestWithLocale() {
        // Some locales do not exist in older versions
        Assume.assumeTrue(Build.VERSION.SDK_INT >= 36);

        assertEquals("00:00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("12:34", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("23:59", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("24:00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("--:--", AppTimeFormat.instantToStringForWidget(null, ZoneId.of("UTC"), true, Locale.JAPAN));

        assertEquals("午前0:00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), false, Locale.JAPAN));
        assertEquals("--:--", AppTimeFormat.instantToStringForWidget(null, ZoneId.of("UTC"), false, Locale.JAPAN));

        assertEquals("00.00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("12.34", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("23.59", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("24.00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("--.--", AppTimeFormat.instantToStringForWidget(null, ZoneId.of("UTC"), true, Locale.of("fi")));

        assertEquals("00 h 00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("12 h 34", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("23 h 59", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("24 h 00", AppTimeFormat.instantToStringForWidget(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("-- h --", AppTimeFormat.instantToStringForWidget(null, ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
    }

    @Test
    public void instantToHmsStringForRealtimeTest() {
        assertEquals("00:00:00", AppTimeFormat.instantToHmsStringForRealtime(LocalTime.of( 0,  0,  0), true, Locale.JAPAN));
        assertEquals("12:34:56", AppTimeFormat.instantToHmsStringForRealtime(LocalTime.of(12, 34, 56), true, Locale.JAPAN));
        assertEquals("23:59:59", AppTimeFormat.instantToHmsStringForRealtime(LocalTime.of(23, 59, 59), true, Locale.JAPAN));
    }

    @Test
    public void instantToHmsStringForRealtimeTestWithLocale() {
        // Some locales do not exist in older versions
        Assume.assumeTrue(Build.VERSION.SDK_INT >= 36);

        assertEquals("12.34.56", AppTimeFormat.instantToHmsStringForRealtime(LocalTime.of(12, 34, 56), true, Locale.of("fi")));
        assertEquals("12:34:56", AppTimeFormat.instantToHmsStringForRealtime(LocalTime.of(12, 34, 56), true, Locale.of("fr", "CA")));
    }

    @Test
    public void degTo24HStrTest() {
        assertEquals("00:00:00", AppTimeFormat.degTo24HStr(0.0, Locale.JAPAN));
        assertEquals("23:59:59", AppTimeFormat.degTo24HStr(359.9999999, Locale.JAPAN));
    }

    @Test
    public void degTo24HStrTestLocale() {
        // Some locales do not exist in older versions
        Assume.assumeTrue(Build.VERSION.SDK_INT >= 36);

        assertEquals("23.59.59", AppTimeFormat.degTo24HStr(359.9999999, Locale.of("fi")));
        assertEquals("23:59:59", AppTimeFormat.degTo24HStr(359.9999999, Locale.of("fr", "CA")));
    }

    @Test
    public void fullDateTimeForEventTest() {
        assertEquals("3/18, 12:34", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("3/18, 12:34", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-18T12:34:29Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("3/18, 12:35", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-18T12:34:30Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("3/19, 00:00", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-18T23:59:30Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("3/19, 00:00", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-19T00:00:00Z"), ZoneId.of("UTC"), true, Locale.US));

        assertEquals("3/18, 21:34", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("Asia/Tokyo"), true, Locale.US));

        assertEquals("4/5, 02:59", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-04-04T15:59:29Z"), ZoneId.of("Australia/Sydney"), true, Locale.US));
        assertEquals("4/5, 02:00", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-04-04T15:59:30Z"), ZoneId.of("Australia/Sydney"), true, Locale.US));
        assertEquals("4/5, 02:00", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-04-04T16:00:00Z"), ZoneId.of("Australia/Sydney"), true, Locale.US));

        assertEquals("3/18, 12:34 PM", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("UTC"), false, Locale.US));

        assertEquals("03/18 12:34", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("3/18 午後0:34", AppTimeFormat.fullDateTimeForEvent(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("UTC"), false, Locale.JAPAN));
    }

    @Test
    public void fullDateTimeHourPrecisionForEventTest() {
        assertEquals("1/2, 00", AppTimeFormat.fullDateTimeHourPrecisionForEvent(Instant.parse("2026-01-02T00:00:00Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("1/2, 00", AppTimeFormat.fullDateTimeHourPrecisionForEvent(Instant.parse("2026-01-02T00:29:59Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("1/2, 01", AppTimeFormat.fullDateTimeHourPrecisionForEvent(Instant.parse("2026-01-02T00:30:00Z"), ZoneId.of("UTC"), true, Locale.US));
    }
}
