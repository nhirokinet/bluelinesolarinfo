package net.nhiroki.bluelinesolarinfo.storage;

import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferences {
    private static final String SHARED_PREFERENCES_NAME = "app_preferences";
    private static final String KEY_CURRENT_LOCATION_USES_ELEVATION = "current_location_uses_elevation";

    private static final String SHARED_PREFERENCES_WIDGET_NAME = "widget_preferences";
    private static final String KEY_WIDGET_CONFIG_PREFIX = "widget_config_";

    public static class RegionBasedWidgetConfig {
        private long regionId = -1;

        public RegionBasedWidgetConfig(long regionId) {
            this.regionId = regionId;
        }

        public RegionBasedWidgetConfig() {}

        public long getRegionId() {
            return regionId;
        }
    }

    public static boolean getCurrentLocationUsesElevation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_CURRENT_LOCATION_USES_ELEVATION, true);
    }

    public static void setCurrentLocationUsesElevation(Context context, boolean usesElevation) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(KEY_CURRENT_LOCATION_USES_ELEVATION, usesElevation).apply();
    }

    public static RegionBasedWidgetConfig getRegionBasedWidgetConfig(Context context, int widgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_WIDGET_NAME, Context.MODE_PRIVATE);
        String configString = sharedPreferences.getString(KEY_WIDGET_CONFIG_PREFIX + widgetId, null);
        if (configString == null) {
            return null;
        }
        RegionBasedWidgetConfig config = new RegionBasedWidgetConfig();
        try {
            config.regionId = Integer.parseInt(configString);
        } catch (NumberFormatException e) {
            return null;
        }
        return config;
    }

    public static void setRegionBasedWidgetConfig(Context context, int widgetId, RegionBasedWidgetConfig config) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_WIDGET_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(KEY_WIDGET_CONFIG_PREFIX + widgetId, String.valueOf(config.regionId)).apply();
    }
}
