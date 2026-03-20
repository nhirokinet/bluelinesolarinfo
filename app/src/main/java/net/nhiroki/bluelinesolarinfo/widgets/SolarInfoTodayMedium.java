package net.nhiroki.bluelinesolarinfo.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.widget.RemoteViews;

import net.nhiroki.androidlib.bluelineastroandroidlib.moonphase.MoonPhaseRenderer;
import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.activities.MainActivity;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.AppPreferences;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.bluelinesolarinfo.stringformats.AppTimeFormat;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;
import net.nhiroki.lib.bluelineastrolib.coordinates.LocationOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.logic.AstronomicalEventsCalculation;
import net.nhiroki.lib.bluelineastrolib.tool.MoonTool;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SolarInfoTodayMedium {

    private static void showError(RemoteViews remoteViews, Context context) {
        remoteViews.setTextViewText(R.id.suninfo_widget_date, context.getString(R.string.widget_error_string));
        remoteViews.setTextViewText(R.id.suninfo_widget_sunrise, context.getString(R.string.widget_error_string));
        remoteViews.setTextViewText(R.id.suninfo_widget_sunset, context.getString(R.string.widget_error_string));
        remoteViews.setTextViewText(R.id.suninfo_moon_days, context.getString(R.string.widget_error_string));
    }

    public static Instant updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_solarinfo_today_medium);

        AppPreferences.RegionBasedWidgetConfig widgetConfig = AppPreferences.getRegionBasedWidgetConfig(context, appWidgetId);
        if (widgetConfig == null) {
            showError(remoteViews, context);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            return null;
        }
        RegionOnTheEarth region = DataStore.getInstance(context).getRegionById(widgetConfig.getRegionId());
        if (region == null) {
            showError(remoteViews, context);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            return null;
        }
        LocationOnTheEarth locationOnEarth = region.getLocationOnTheEarth();
        ZoneId localZone = region.getZoneId();
        boolean timeFormat24Hours = android.text.format.DateFormat.is24HourFormat(context);

        Instant now = Instant.now();
        ZonedDateTime nowLocal = ZonedDateTime.ofInstant(now, localZone);
        ZonedDateTime dayStartLocal = nowLocal.withHour(0).withMinute(0).withSecond(0).withNano(0);
        Instant startOfDay = dayStartLocal.toInstant();
        ZonedDateTime dayMidLocal = nowLocal.withHour(12).withMinute(0).withSecond(0).withNano(0);
        Instant midOfDay = dayMidLocal.toInstant();

        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        String dateFormat = DateFormat.getBestDateTimePattern(locale, "MMMdE");
        remoteViews.setTextViewText(R.id.suninfo_widget_date, nowLocal.format(DateTimeFormatter.ofPattern(dateFormat, locale)));
        remoteViews.setTextViewText(R.id.suninfo_widget_location, region.getName());

        Sun sun = new Sun();
        try {
            Instant sunrise = AstronomicalEventsCalculation.calculateRiseWithin24h(sun, startOfDay, locationOnEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
            remoteViews.setTextViewText(R.id.suninfo_widget_sunrise, AppTimeFormat.instantToHmStringForEventTime(sunrise, localZone, timeFormat24Hours, locale));
        } catch (AstronomicalPhenomenonComputationException | UnsupportedDateRangeException e) {
            remoteViews.setTextViewText(R.id.suninfo_widget_sunrise, context.getString(R.string.widget_error_string));
        }

        try {
            Instant sunset = AstronomicalEventsCalculation.calculateSetWithin24h(sun, startOfDay, locationOnEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
            remoteViews.setTextViewText(R.id.suninfo_widget_sunset, AppTimeFormat.instantToHmStringForEventTime(sunset, localZone, timeFormat24Hours, locale));
        } catch (AstronomicalPhenomenonComputationException | UnsupportedDateRangeException e) {
            remoteViews.setTextViewText(R.id.suninfo_widget_sunset, context.getString(R.string.widget_error_string));
        }

        double moonPhaseDeg = MoonTool.calculateMoonPhaseDeg(midOfDay);

        try {
            Instant prevNewMoon = MoonTool.calculatePreviousTimeOfMoonPhase(midOfDay, 0.0);
            double daysAfterNewMoon = (midOfDay.toEpochMilli() - prevNewMoon.toEpochMilli()) / 86400000.0 - 0.0;
            remoteViews.setTextViewText(R.id.suninfo_moon_days, String.format(locale, "%2.1f", daysAfterNewMoon));
        } catch (AstronomicalPhenomenonComputationException e) {
            remoteViews.setTextViewText(R.id.suninfo_moon_days, context.getString(R.string.widget_error_string));
        }

        TypedValue typedValue = new TypedValue();
        Context themedContext = new ContextThemeWrapper(context, R.style.Theme_BlueLineSolarInfo);
        themedContext.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);

        int primaryColor;
        if (typedValue.resourceId != 0) {
            primaryColor = themedContext.getResources().getColor(typedValue.resourceId, themedContext.getTheme());
        } else {
            primaryColor = typedValue.data;
        }
        // Must keep the same as in layout XML
        int moonBitmapSizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics());
        Bitmap moonBitmap = MoonPhaseRenderer.generateBitmapOfSingleColorMoonPhase(moonBitmapSizeInPx, primaryColor, moonPhaseDeg);
        remoteViews.setImageViewBitmap(R.id.suninfo_moon_phase_bitmap, moonBitmap);

        Intent intentForClick = new Intent(context, MainActivity.class);
        intentForClick.putExtra(MainActivity.EXTRA_REGION_ID, region.getId());
        intentForClick.putExtra(MainActivity.EXTRA_FROM_APPWIDGETID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, MainActivity.getRequestCodeForOpeningByWidget(appWidgetId), new Intent[]{intentForClick}, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_suninfo_root, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        return nowLocal.withHour(23).withMinute(59).withSecond(59).withNano(0).plusSeconds(1).toInstant();
    }
}
