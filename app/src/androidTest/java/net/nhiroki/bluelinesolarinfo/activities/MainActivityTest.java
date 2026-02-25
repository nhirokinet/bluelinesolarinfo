package net.nhiroki.bluelinesolarinfo.activities;

import android.app.LocaleManager;
import android.content.Intent;
import android.os.Build;
import android.os.LocaleList;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.bluelinesolarinfo.test_data.TokyoNAORegion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
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
        long tokyoNAOID = dataStore.createRegion(new TokyoNAORegion());

        // Defaults to have default region as it is nice in most of the tests
        RegionOnTheEarth tokyoNAORegion = dataStore.getRegionById(tokyoNAOID);
        dataStore.setDefaultRegion(tokyoNAORegion);
    }

    @Test
    public void testJustLaunch() throws InterruptedException {
        ActivityScenario<MainActivity> activity = ActivityScenario.launch(MainActivity.class);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Tokyo")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
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

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Tokyo")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
    }

    @Test
    public void testJustLaunch20260101() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TARGET_TIME_UNIX_MILLISEC, 1767236400000l);  // 2026/01/01 12:00 JST
        ActivityScenario<MainActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("12.1")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
    }

    @Test
    public void testJustLaunch20260101Japanese() throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ApplicationProvider.getApplicationContext().getSystemService(LocaleManager.class).setApplicationLocales(new LocaleList(new Locale("ja", "JP")));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(new Locale("en", "US")));
        }
        Locale.setDefault(new Locale("ja", "JP"));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TARGET_TIME_UNIX_MILLISEC, 1767236400000l);  // 2026/01/01 12:00 JST
        ActivityScenario<MainActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("12.1")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
    }
}
