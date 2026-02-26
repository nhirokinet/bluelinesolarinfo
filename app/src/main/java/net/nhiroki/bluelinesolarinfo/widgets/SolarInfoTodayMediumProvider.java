package net.nhiroki.bluelinesolarinfo.widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.nhiroki.androidlib.bluelineastroandroidlib.moonphase.MoonPhaseRenderer;
import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.activities.MainActivity;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.AppPreferences;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;
import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.logic.AstronomicalObjectCalculator;
import net.nhiroki.lib.bluelineastrolib.tool.MoonTool;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class SolarInfoTodayMediumProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateAllWidgets(context, appWidgetManager, appWidgetIds);
    }

    public static void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void showError(RemoteViews remoteViews, Context context) {
        remoteViews.setTextViewText(R.id.suninfo_widget_date, context.getString(R.string.widget_error_string));
        remoteViews.setTextViewText(R.id.suninfo_widget_sunrise, context.getString(R.string.widget_error_string));
        remoteViews.setTextViewText(R.id.suninfo_widget_sunset, context.getString(R.string.widget_error_string));
        remoteViews.setTextViewText(R.id.suninfo_moon_days, context.getString(R.string.widget_error_string));
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_solarinfo_today_medium);

        AppPreferences.RegionBasedWidgetConfig widgetConfig = AppPreferences.getRegionBasedWidgetConfig(context, appWidgetId);
        if (widgetConfig == null) {
            showError(remoteViews, context);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            return;
        }
        RegionOnTheEarth region = DataStore.getInstance(context).getRegionById(widgetConfig.getRegionId());
        if (region == null) {
            showError(remoteViews, context);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            return;
        }
        LocationOnTheEarth locationOnEarth = region.getLocationOnTheEarth();
        ZoneId localZone = region.getZoneId();

        Instant now = Instant.now();
        ZonedDateTime nowLocal = ZonedDateTime.ofInstant(now, localZone);
        ZonedDateTime dayStartLocal = nowLocal.withHour(0).withMinute(0).withSecond(0).withNano(0);
        Instant startOfDay = dayStartLocal.toInstant();
        ZonedDateTime dayMidLocal = nowLocal.withHour(12).withMinute(0).withSecond(0).withNano(0);
        Instant midOfDay = dayMidLocal.toInstant();

        Locale locale = context.getResources().getConfiguration().locale;
        String dateFormat = DateFormat.getBestDateTimePattern(locale, "MMddE");
        remoteViews.setTextViewText(R.id.suninfo_widget_date, new SimpleDateFormat(dateFormat, locale).format(new Date(nowLocal.getYear() - 1900, nowLocal.getMonthValue() - 1, nowLocal.getDayOfMonth())));
        remoteViews.setTextViewText(R.id.suninfo_widget_location, region.getName());

        Sun sun = new Sun();
        try {
            Instant sunrise = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, startOfDay, locationOnEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
            remoteViews.setTextViewText(R.id.suninfo_widget_sunrise, instantToString(context, sunrise, localZone));
        } catch (AstronomicalPhenomenonComputationException e) {
            remoteViews.setTextViewText(R.id.suninfo_widget_sunrise, context.getString(R.string.widget_error_string));
        } catch (UnsupportedDateRangeException e) {
            remoteViews.setTextViewText(R.id.suninfo_widget_sunrise, context.getString(R.string.widget_error_string));
        }

        try {
            Instant sunset = AstronomicalObjectCalculator.calculateSetWithin24h(sun, startOfDay, locationOnEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
            remoteViews.setTextViewText(R.id.suninfo_widget_sunset, instantToString(context, sunset, localZone));
        } catch (AstronomicalPhenomenonComputationException e) {
            remoteViews.setTextViewText(R.id.suninfo_widget_sunset, context.getString(R.string.widget_error_string));
        } catch (UnsupportedDateRangeException e) {
            remoteViews.setTextViewText(R.id.suninfo_widget_sunset, context.getString(R.string.widget_error_string));
        }

        double moonPhaseDeg = MoonTool.calculateMoonPhaseDeg(midOfDay);

        try {
            Instant prevNewMoon = MoonTool.calculatePreviousTimeOfMoonPhaseByDeg(midOfDay, 0.0);
            double daysAfterNewMoon = (midOfDay.toEpochMilli() - prevNewMoon.toEpochMilli()) / 86400000.0 - 0.0;
            remoteViews.setTextViewText(R.id.suninfo_moon_days, String.format("%2.1f", daysAfterNewMoon));
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
        // Must keep the same as in layout xml
        int moonBitmapSizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics());
        Bitmap moonBitmap = MoonPhaseRenderer.generateBitmapOfSingleColorMoonPhase(moonBitmapSizeInPx, primaryColor, moonPhaseDeg);
        remoteViews.setImageViewBitmap(R.id.suninfo_moon_phase_bitmap, moonBitmap);

        Intent intentForClick = new Intent(context, MainActivity.class);
        intentForClick.putExtra(MainActivity.EXTRA_REGION_ID, region.getId());
        PendingIntent pendingIntent = PendingIntent.getActivities(context, MainActivity.getRequestCodeForOpeningByWidget(appWidgetId), new Intent[]{intentForClick}, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_suninfo_root, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private static String instantToString(Context context, Instant instant, ZoneId zoneId) {
        if (instant == null) {
            return context.getString(R.string.time_event_not_occur_hm);
        }

        boolean timeFormat24Hour = android.text.format.DateFormat.is24HourFormat(context);
        ZonedDateTime localTime = ZonedDateTime.ofInstant(instant.plusSeconds(30), zoneId);

        if (timeFormat24Hour) {
            return context.getString(R.string.widget_solar_info_today_24h_format, localTime.getHour(), localTime.getMinute());
        } else {
            Locale locale = context.getResources().getConfiguration().getLocales().get(0);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "hma"), locale);
            return localTime.format(timeFormatter);
        }
    }
}
