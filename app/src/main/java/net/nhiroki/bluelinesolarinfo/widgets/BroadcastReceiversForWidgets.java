package net.nhiroki.bluelinesolarinfo.widgets;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiversForWidgets extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        {
            int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, SolarInfoTodayTinyProvider.class.getName()));
            SolarInfoTodayTinyProvider.updateAllWidgets(context, AppWidgetManager.getInstance(context), widgetIds);
        }
        {
            int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, SolarInfoTodayMediumProvider.class.getName()));
            SolarInfoTodayMediumProvider.updateAllWidgets(context, AppWidgetManager.getInstance(context), widgetIds);
        }
    }
}
