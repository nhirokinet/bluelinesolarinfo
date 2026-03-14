package net.nhiroki.bluelinesolarinfo.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import net.nhiroki.bluelinesolarinfo.storage.AppPreferences;

public class SolarInfoTodayTinyProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        WidgetUpdateWorker.updateAllWidgets(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId: appWidgetIds) {
            AppPreferences.deleteRegionWidgetConfig(context, appWidgetId);
        }
    }
}
