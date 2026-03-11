package net.nhiroki.bluelinesolarinfo.stringformats;

import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assume;
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
    public void instantToHmStringForEventTimeTest() {
        Locale locale = Locale.US;

        assertEquals("00:00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("06:12", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("06:13", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("23:59", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("24:00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, locale));
        assertEquals("24:00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), true, locale));

        assertEquals("15:12", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("05:12", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T20:12:29Z"), ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("15:42", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-07-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true, locale));
        assertEquals("16:42", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true, locale));

        assertEquals("02:59", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-04-04T15:59:29Z"), ZoneId.of("Australia/Sydney"), true, locale));
        assertEquals("02:00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-04-04T15:59:30Z"), ZoneId.of("Australia/Sydney"), true, locale));
        assertEquals("02:00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-04-04T16:00:00Z"), ZoneId.of("Australia/Sydney"), true, locale));

        assertEquals("12:00 AM", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("6:12 AM", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("6:13 AM", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("11:59 AM", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T11:59:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("12:00 PM", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T11:59:30Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("11:59 PM", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), false, locale));
        assertEquals("12:00 AM", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), false, locale));

        assertEquals("--:--", AppTimeFormat.instantToHmStringForEventTime(null, ZoneId.of("Asia/Tokyo"), true, locale));
        assertEquals("--:--", AppTimeFormat.instantToHmStringForEventTime(null, ZoneId.of("Asia/Tokyo"), false, locale));
    }

    @Test
    public void instantToHmStringForEventTimeTestWithLocale() {
        // Some locales do not exist in older versions
        Assume.assumeTrue(Build.VERSION.SDK_INT >= 36);

        assertEquals("0:00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("12:34", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("23:59", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("24:00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("--:--", AppTimeFormat.instantToHmStringForEventTime(null, ZoneId.of("UTC"), true, Locale.JAPAN));

        assertEquals("午前0:00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), false, Locale.JAPAN));
        assertEquals("--:--", AppTimeFormat.instantToHmStringForEventTime(null, ZoneId.of("UTC"), false, Locale.JAPAN));

        assertEquals("0.00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("12.34", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("23.59", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("24.00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.of("fi")));
        assertEquals("--.--", AppTimeFormat.instantToHmStringForEventTime(null, ZoneId.of("UTC"), true, Locale.of("fi")));

        assertEquals("00 h 00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("12 h 34", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T12:34:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("23 h 59", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:00Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("24 h 00", AppTimeFormat.instantToHmStringForEventTime(Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
        assertEquals("-- h --", AppTimeFormat.instantToHmStringForEventTime(null, ZoneId.of("UTC"), true, Locale.of("fr", "CA")));
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
    public void fullDateTimeForEventNaturalTest() {
        assertEquals("Mar 18, 16:34", AppTimeFormat.fullDateTimeForEventNatural(Instant.parse("2026-03-18T16:34:00Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("Mar 18, 16:34", AppTimeFormat.fullDateTimeForEventNatural(Instant.parse("2026-03-18T16:34:29Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("Mar 18, 16:35", AppTimeFormat.fullDateTimeForEventNatural(Instant.parse("2026-03-18T16:34:30Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("Mar 18, 4:34 PM", AppTimeFormat.fullDateTimeForEventNatural(Instant.parse("2026-03-18T16:34:00Z"), ZoneId.of("UTC"), false, Locale.US));
    }

    @Test
    public void fullDateTimeForEventForListTest() {
        assertEquals("03/18, 12:34", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("03/18, 12:34", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T12:34:29Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("03/18, 12:35", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T12:34:30Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("03/19, 00:00", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T23:59:30Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("03/19, 00:00", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-19T00:00:00Z"), ZoneId.of("UTC"), true, Locale.US));

        assertEquals("03/18, 21:34", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("Asia/Tokyo"), true, Locale.US));

        assertEquals("04/05, 02:59", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-04-04T15:59:29Z"), ZoneId.of("Australia/Sydney"), true, Locale.US));
        assertEquals("04/05, 02:00", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-04-04T15:59:30Z"), ZoneId.of("Australia/Sydney"), true, Locale.US));
        assertEquals("04/05, 02:00", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-04-04T16:00:00Z"), ZoneId.of("Australia/Sydney"), true, Locale.US));

        assertEquals("03/18, 12:34 PM", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("UTC"), false, Locale.US));

        assertEquals("01/02 00時", AppTimeFormat.fullDateTimeHourPrecisionForEventForList(Instant.parse("2026-01-02T00:00:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("03/18 09:34", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T09:34:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("03/18 13:34", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T13:34:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("03/18 午後0:34", AppTimeFormat.fullDateTimeForEventForList(Instant.parse("2026-03-18T12:34:00Z"), ZoneId.of("UTC"), false, Locale.JAPAN));
    }

    @Test
    public void fullDateTimeHourPrecisionForEventForListTest() {
        assertEquals("01/02, 00", AppTimeFormat.fullDateTimeHourPrecisionForEventForList(Instant.parse("2026-01-02T00:00:00Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("01/02, 00", AppTimeFormat.fullDateTimeHourPrecisionForEventForList(Instant.parse("2026-01-02T00:29:59Z"), ZoneId.of("UTC"), true, Locale.US));
        assertEquals("01/02, 01", AppTimeFormat.fullDateTimeHourPrecisionForEventForList(Instant.parse("2026-01-02T00:30:00Z"), ZoneId.of("UTC"), true, Locale.US));

        assertEquals("01/02 00時", AppTimeFormat.fullDateTimeHourPrecisionForEventForList(Instant.parse("2026-01-02T00:00:00Z"), ZoneId.of("UTC"), true, Locale.JAPAN));
        assertEquals("01/02 午前0時", AppTimeFormat.fullDateTimeHourPrecisionForEventForList(Instant.parse("2026-01-02T00:00:00Z"), ZoneId.of("UTC"), false, Locale.JAPAN));
    }
}
