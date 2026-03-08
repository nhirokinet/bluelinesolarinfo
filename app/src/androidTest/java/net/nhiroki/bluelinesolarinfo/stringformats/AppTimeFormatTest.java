package net.nhiroki.bluelinesolarinfo.stringformats;

import android.app.LocaleManager;
import android.content.Context;
import android.os.Build;
import android.os.LocaleList;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AppTimeFormatTest {
    @Before
    public void setUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ApplicationProvider.getApplicationContext().getSystemService(LocaleManager.class).setApplicationLocales(new LocaleList(new Locale("en", "US")));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(new Locale("en", "US")));
        }
        Locale.setDefault(new Locale("en", "US"));
    }

    @Test
    public void instantToStringForMainActivity() {
        Context context = ApplicationProvider.getApplicationContext();

        assertEquals("00:00", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true));
        assertEquals("06:12", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), true));
        assertEquals("06:13", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), true));
        assertEquals("23:59", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), true));
        assertEquals("24:00", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true));
        assertEquals("24:00", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), true));

        assertEquals("15:12", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Asia/Tokyo"), true));
        assertEquals("05:12", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T20:12:29Z"), ZoneId.of("Asia/Tokyo"), true));
        assertEquals("15:42", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-07-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true));
        assertEquals("16:42", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true));

        assertEquals("12:00 AM", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), false));
        assertEquals("6:12 AM", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), false));
        assertEquals("6:13 AM", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), false));
        assertEquals("11:59 AM", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T11:59:29Z"), ZoneId.of("UTC"), false));
        assertEquals("12:00 PM", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T11:59:30Z"), ZoneId.of("UTC"), false));
        assertEquals("11:59 PM", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), false));
        assertEquals("12:00 AM", AppTimeFormat.instantToStringForMainActivity(context, Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), false));

        assertEquals("--:--", AppTimeFormat.instantToStringForMainActivity(context, null, ZoneId.of("Asia/Tokyo"), true));
        assertEquals("--:--", AppTimeFormat.instantToStringForMainActivity(context, null, ZoneId.of("Asia/Tokyo"), false));
    }

    @Test
    public void instantToStringForWidget() {
        Context context = ApplicationProvider.getApplicationContext();

        assertEquals("00:00", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), true));
        assertEquals("06:12", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), true));
        assertEquals("06:13", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), true));
        assertEquals("23:59", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), true));
        assertEquals("24:00", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T23:59:30Z"), ZoneId.of("UTC"), true));
        assertEquals("24:00", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), true));

        assertEquals("15:12", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Asia/Tokyo"), true));
        assertEquals("05:12", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T20:12:29Z"), ZoneId.of("Asia/Tokyo"), true));
        assertEquals("15:42", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-07-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true));
        assertEquals("16:42", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("Australia/Adelaide"), true));

        assertEquals("12:00 AM", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"), false));
        assertEquals("6:12 AM", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T06:12:29Z"), ZoneId.of("UTC"), false));
        assertEquals("6:13 AM", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T06:12:30Z"), ZoneId.of("UTC"), false));
        assertEquals("11:59 AM", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T11:59:29Z"), ZoneId.of("UTC"), false));
        assertEquals("12:00 PM", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T11:59:30Z"), ZoneId.of("UTC"), false));
        assertEquals("11:59 PM", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T23:59:29Z"), ZoneId.of("UTC"), false));
        assertEquals("12:00 AM", AppTimeFormat.instantToStringForWidget(context, Instant.parse("2026-01-01T23:59:59Z"), ZoneId.of("UTC"), false));

        assertEquals("--:--", AppTimeFormat.instantToStringForWidget(context, null, ZoneId.of("Asia/Tokyo"), true));
        assertEquals("--:--", AppTimeFormat.instantToStringForWidget(context, null, ZoneId.of("Asia/Tokyo"), false));
    }
}
