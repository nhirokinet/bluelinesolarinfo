package net.nhiroki.bluelinesolarinfo.activities;

import android.app.LocaleManager;
import android.content.Intent;
import android.os.Build;
import android.os.LocaleList;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.bluelinesolarinfo.test_data.NorthPoleRegion;
import net.nhiroki.bluelinesolarinfo.test_data.TokyoNAORegion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    long tokyoNAOID;
    long northPoleID;

    @Before
    public void setUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ApplicationProvider.getApplicationContext().getSystemService(LocaleManager.class).setApplicationLocales(new LocaleList(new Locale("en", "US")));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(new Locale("en", "US")));
        }
        Locale.setDefault(new Locale("en", "US"));

        ApplicationProvider.getApplicationContext().deleteDatabase("app-data.sqlite");
        DataStore.discardInstance();
        DataStore dataStore = DataStore.getInstance(ApplicationProvider.getApplicationContext());

        tokyoNAOID = dataStore.createRegion(new TokyoNAORegion());
        northPoleID = dataStore.createRegion(new NorthPoleRegion());

        // Defaults to have default region as it is nice in most of the tests
        dataStore.setDefaultRegion(dataStore.getRegionById(tokyoNAOID));
    }

    @Test
    public void testJustLaunch() throws InterruptedException {
        ActivityScenario<MainActivity> activity = ActivityScenario.launch(MainActivity.class);
        Thread.sleep(2000);

        Espresso.onView(ViewMatchers.withText("東京")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("E 139°44'29.0\" N 35°39'29.1\"")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("Timezone: Japan Time (Asia/Tokyo)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("Greenwich sidereal time")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testJustLaunchJapanese() throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ApplicationProvider.getApplicationContext().getSystemService(LocaleManager.class).setApplicationLocales(new LocaleList(new Locale("ja", "JP")));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(new Locale("ja", "JP")));
        }
        Locale.setDefault(new Locale("ja", "JP"));

        ActivityScenario<MainActivity> activity = ActivityScenario.launch(MainActivity.class);
        Thread.sleep(2000);

        Espresso.onView(ViewMatchers.withText("東京")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("E 139°44'29.0\" N 35°39'29.1\"")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("時間帯：日本時間（Asia/Tokyo）")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("グリニッジ恒星時")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testLaunch20260101() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TARGET_TIME_UNIX_MILLISEC, 1767236400000l);  // 2026/01/01 12:00 JST
        ActivityScenario<MainActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        Espresso.onView(ViewMatchers.withText("January 1, 2026")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Sun
        Espresso.onView(ViewMatchers.withText("Azimuth 118°05' (ESE)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("S Elevation +31°22'")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("Azimuth 241°58' (WSW)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Moon
        Espresso.onView(ViewMatchers.withText("Azimuth 56°29' (ENE)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("12.1")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("Azimuth 302°05' (WNW)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        if (android.text.format.DateFormat.is24HourFormat(ApplicationProvider.getApplicationContext())) {
            // Sun
            Espresso.onView(ViewMatchers.withText("06:51")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("11:44")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("16:38")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            // Moon
            Espresso.onView(ViewMatchers.withText("14:04")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("21:47")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("04:21")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else {
            // Sun
            Espresso.onView(ViewMatchers.withText("6:51 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("11:44 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("4:38 PM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            // Moon
            Espresso.onView(ViewMatchers.withText("2:04 PM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("9:47 PM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("4:21 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }

        Espresso.onView(ViewMatchers.withId(R.id.main_view_solar_info_today_this_moon_cycle_button)).perform(ViewActions.click());
        Thread.sleep(500);
        if (android.text.format.DateFormat.is24HourFormat(ApplicationProvider.getApplicationContext())) {
            Espresso.onView(ViewMatchers.withText("12/20, 10:44")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("12/28, 04:10")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("01/03, 19:03")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("01/11, 00:49")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("01/19, 04:53")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else {
            Espresso.onView(ViewMatchers.withText("12/20, 10:44 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("12/28, 4:10 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("01/03, 7:03 PM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("01/11, 12:49 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("01/19, 4:53 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }

        Espresso.pressBack();
    }

    @Test
    public void testJustLaunch20260101Japanese() throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ApplicationProvider.getApplicationContext().getSystemService(LocaleManager.class).setApplicationLocales(new LocaleList(new Locale("ja", "JP")));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(new Locale("ja", "JP")));
        }
        Locale.setDefault(new Locale("ja", "JP"));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TARGET_TIME_UNIX_MILLISEC, 1767236400000l);  // 2026/01/01 12:00 JST
        ActivityScenario<MainActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        Espresso.onView(ViewMatchers.withText("2026年1月1日")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Sun
        Espresso.onView(ViewMatchers.withText("方位 118°05' (東南東)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("南 高度 +31°22'")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("方位 241°58' (西南西)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Moon
        Espresso.onView(ViewMatchers.withText("方位 56°29' (東北東)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("12.1")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("方位 302°05' (西北西)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        if (android.text.format.DateFormat.is24HourFormat(ApplicationProvider.getApplicationContext())) {
            // Sun
            Espresso.onView(ViewMatchers.withText("6:51")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("11:44")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("16:38")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            // Moon
            Espresso.onView(ViewMatchers.withText("14:04")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("21:47")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("4:21")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else {
            // Sun
            Espresso.onView(ViewMatchers.withText("午前6:51")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("午前11:44")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("午後4:38")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            // Moon
            Espresso.onView(ViewMatchers.withText("午後2:04")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("午後9:47")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            Espresso.onView(ViewMatchers.withText("午前4:21")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
    }

    @Test
    public void testJustLaunch20260101NorthPole() throws InterruptedException {
        DataStore dataStore = DataStore.getInstance(ApplicationProvider.getApplicationContext());
        dataStore.setDefaultRegion(dataStore.getRegionById(northPoleID));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TARGET_TIME_UNIX_MILLISEC, 1767236400000l);  // 2026/01/01 03:00 UTC
        ActivityScenario<MainActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        Espresso.onView(ViewMatchers.withText("(Invisible)")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(ViewMatchers.withText("Next sunrise:")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        if (android.text.format.DateFormat.is24HourFormat(ApplicationProvider.getApplicationContext())) {
            Espresso.onView(ViewMatchers.withText("Mar 18, 10:58")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else {
            Espresso.onView(ViewMatchers.withText("Mar 18, 10:58 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
        Espresso.onView(ViewMatchers.withText("Next moonset:")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        if (android.text.format.DateFormat.is24HourFormat(ApplicationProvider.getApplicationContext())) {
            Espresso.onView(ViewMatchers.withText("Jan 8, 17:22")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else {
            Espresso.onView(ViewMatchers.withText("Jan 8, 5:22 PM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
    }

    @Test
    public void testJustLaunch20260501NorthPole() throws InterruptedException {
        DataStore dataStore = DataStore.getInstance(ApplicationProvider.getApplicationContext());
        dataStore.setDefaultRegion(dataStore.getRegionById(northPoleID));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TARGET_TIME_UNIX_MILLISEC, 1777604400000l);  // 2026/05/01 03:00 UTC
        ActivityScenario<MainActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        Espresso.onView(ViewMatchers.withText("Next sunset:")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        if (android.text.format.DateFormat.is24HourFormat(ApplicationProvider.getApplicationContext())) {
            Espresso.onView(ViewMatchers.withText("Sep 25, 04:39")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else {
            Espresso.onView(ViewMatchers.withText("Sep 25, 4:39 AM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
        Espresso.onView(ViewMatchers.withText("Next moonrise:")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        if (android.text.format.DateFormat.is24HourFormat(ApplicationProvider.getApplicationContext())) {
            Espresso.onView(ViewMatchers.withText("May 12, 17:29")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else {
            Espresso.onView(ViewMatchers.withText("May 12, 5:29 PM")).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
    }
}
