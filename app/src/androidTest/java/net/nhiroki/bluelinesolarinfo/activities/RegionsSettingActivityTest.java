package net.nhiroki.bluelinesolarinfo.activities;

import android.app.LocaleManager;
import android.os.Build;
import android.os.LocaleList;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.bluelinesolarinfo.test_data.ShowaStationRegion;
import net.nhiroki.bluelinesolarinfo.test_data.TokyoNAORegion;
import net.nhiroki.bluelinesolarinfo.test_data.TopOfMtFujiRegion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class RegionsSettingActivityTest {
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
        dataStore.createRegion(new TopOfMtFujiRegion());
        dataStore.createRegion(new ShowaStationRegion());

        RegionOnTheEarth tokyoNAORegion = dataStore.getRegionById(tokyoNAOID);
        dataStore.setDefaultRegion(tokyoNAORegion);
    }

    @Test
    public void testJustLaunch() throws InterruptedException {
        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(RegionsSettingActivity.class);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Showa Station")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Top of Mt. Fuji")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Default: 東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testJustLaunchJapanese() throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ApplicationProvider.getApplicationContext().getSystemService(LocaleManager.class).setApplicationLocales(new LocaleList(new Locale("ja", "JP")));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(new Locale("ja", "JP")));
        }
        Locale.setDefault(new Locale("ja", "JP"));

        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(RegionsSettingActivity.class);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Showa Station")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Top of Mt. Fuji")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("デフォルト：東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testClickRegion() throws InterruptedException {
        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(RegionsSettingActivity.class);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Top of Mt. Fuji")).perform(androidx.test.espresso.action.ViewActions.click());
        Thread.sleep(1000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Save")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Delete")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("3776.0")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testChangeDefaultRegion() throws InterruptedException {
        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(RegionsSettingActivity.class);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Default: 東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        DataStore dataStore = DataStore.getInstance(ApplicationProvider.getApplicationContext());
        org.junit.Assert.assertEquals("東京", dataStore.getDefaultRegion().getName());

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Change default region")).perform(androidx.test.espresso.action.ViewActions.click());
        Thread.sleep(500);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Top of Mt. Fuji")).perform(androidx.test.espresso.action.ViewActions.click());
        Thread.sleep(500);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Default: Top of Mt. Fuji")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        org.junit.Assert.assertEquals("Top of Mt. Fuji", dataStore.getDefaultRegion().getName());
    }
}
