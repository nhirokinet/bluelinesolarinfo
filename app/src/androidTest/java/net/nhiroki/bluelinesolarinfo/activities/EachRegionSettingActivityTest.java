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
public class EachRegionSettingActivityTest {
    long tokyoNAOID;
    long mtFujiID;

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
        mtFujiID = dataStore.createRegion(new TopOfMtFujiRegion());
        dataStore.createRegion(new ShowaStationRegion());

        RegionOnTheEarth tokyoNAORegion = dataStore.getRegionById(tokyoNAOID);
        dataStore.setDefaultRegion(tokyoNAORegion);
    }

    @Test
    public void testJustLaunch() throws InterruptedException {
        ActivityScenario<EachRegionSettingActivity> activity = ActivityScenario.launch(EachRegionSettingActivity.class);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("0")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testJustLaunchTokyo() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EachRegionSettingActivity.class);
        intent.putExtra(EachRegionSettingActivity.EXTRA_REGION_ID, tokyoNAOID);
        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("139.7414")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("35.6581")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("0.0")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Asia/Tokyo")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Japan Time")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testJustLaunchTokyoJapanese() throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ApplicationProvider.getApplicationContext().getSystemService(LocaleManager.class).setApplicationLocales(new LocaleList(new Locale("ja", "JP")));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(new Locale("ja", "JP")));
        }
        Locale.setDefault(new Locale("ja", "JP"));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EachRegionSettingActivity.class);
        intent.putExtra(EachRegionSettingActivity.EXTRA_REGION_ID, tokyoNAOID);
        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("139.7414")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("35.6581")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("0.0")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Asia/Tokyo")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("日本時間")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testJustLaunchMtFuji() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EachRegionSettingActivity.class);
        intent.putExtra(EachRegionSettingActivity.EXTRA_REGION_ID, mtFujiID);
        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Top of Mt. Fuji")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("3776.0")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Asia/Tokyo")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Japan Time")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testSaveDifferentTokyo() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EachRegionSettingActivity.class);
        intent.putExtra(EachRegionSettingActivity.EXTRA_REGION_ID, tokyoNAOID);
        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("139.7414")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("35.6581")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("0.0")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Asia/Tokyo")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Japan Time")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("東京")).perform(androidx.test.espresso.action.ViewActions.replaceText("New Tokyo"));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("139.7414")).perform(androidx.test.espresso.action.ViewActions.replaceText("135.0"));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("35.6581")).perform(androidx.test.espresso.action.ViewActions.replaceText("35.5"));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("0.0")).perform(androidx.test.espresso.action.ViewActions.replaceText("10.0"));

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Change")).perform(androidx.test.espresso.action.ViewActions.click());
        Thread.sleep(500);
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("UTC: UTC")).perform(androidx.test.espresso.action.ViewActions.click());
        Thread.sleep(500);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Save")).perform(androidx.test.espresso.action.ViewActions.click());

        Thread.sleep(500);

        DataStore dataStore = DataStore.getInstance(ApplicationProvider.getApplicationContext());
        RegionOnTheEarth region = dataStore.getRegionById(tokyoNAOID);
        org.junit.Assert.assertEquals("New Tokyo", region.getName());
        org.junit.Assert.assertEquals(135.0, region.getLocationOnTheEarth().getLongitudeDeg(), 0.0001);
        org.junit.Assert.assertEquals(35.5, region.getLocationOnTheEarth().getLatitudeDeg(), 0.0001);
        org.junit.Assert.assertEquals(10.0, region.getLocationOnTheEarth().getElevationMeters(), 0.0001);
        org.junit.Assert.assertEquals("UTC", region.getZoneId().toString());
    }

    @Test
    public void testDeleteTokyo() throws InterruptedException {
        DataStore dataStore = DataStore.getInstance(ApplicationProvider.getApplicationContext());

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EachRegionSettingActivity.class);
        intent.putExtra(EachRegionSettingActivity.EXTRA_REGION_ID, tokyoNAOID);
        ActivityScenario<RegionsSettingActivity> activity = ActivityScenario.launch(intent);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("東京")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("139.7414")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("35.6581")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));

        org.junit.Assert.assertNotNull(dataStore.getRegionById(tokyoNAOID));
        org.junit.Assert.assertEquals("東京", dataStore.getRegionById(tokyoNAOID).getName());
        org.junit.Assert.assertNotNull(dataStore.getDefaultRegion());
        org.junit.Assert.assertEquals("東京", dataStore.getDefaultRegion().getName());
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Delete")).perform(androidx.test.espresso.action.ViewActions.click());
        Thread.sleep(500);

        org.junit.Assert.assertNull(dataStore.getRegionById(tokyoNAOID));
        org.junit.Assert.assertNull(dataStore.getDefaultRegion());
    }
}
