package net.nhiroki.bluelinesolarinfo.storage;

import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferences {
    private static final String SHARED_PREFERENCES_NAME = "app_preferences";
    private static final String KEY_CURRENT_LOCATION_USES_ELEVATION = "current_location_uses_elevation";

    public static boolean getCurrentLocationUsesElevation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_CURRENT_LOCATION_USES_ELEVATION, true);
    }

    public static void setCurrentLocationUsesElevation(Context context, boolean usesElevation) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(KEY_CURRENT_LOCATION_USES_ELEVATION, usesElevation).apply();
    }
}
