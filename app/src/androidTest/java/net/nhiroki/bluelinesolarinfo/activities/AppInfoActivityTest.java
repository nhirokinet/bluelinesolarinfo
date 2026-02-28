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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class AppInfoActivityTest {
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
    public void testJustLaunch() throws InterruptedException {
        ActivityScenario<AppInfoActivity> activity = ActivityScenario.launch(AppInfoActivity.class);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Blue Line Solar Info")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("License information")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Apache License, Version 2.0")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testJustLaunchJapanese() throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ApplicationProvider.getApplicationContext().getSystemService(LocaleManager.class).setApplicationLocales(new LocaleList(new Locale("ja", "JP")));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(new Locale("ja", "JP")));
        }
        Locale.setDefault(new Locale("ja", "JP"));

        ActivityScenario<AppInfoActivity> activity = ActivityScenario.launch(AppInfoActivity.class);
        Thread.sleep(2000);

        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Blue Line Solar Info")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("ライセンス情報")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Apache License, Version 2.0")).check(androidx.test.espresso.assertion.ViewAssertions.matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
