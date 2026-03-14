package net.nhiroki.bluelinesolarinfo.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class WidgetUpdateWorker extends Worker {
    private static final String WORK_TAG_FOR_WIDGETS_UPDATE = "widgets_update";
    private static final int MILLISECONDS_WAIT_AFTER_IDEAL_UPDATE = 2000;

    private Context context;

    public WidgetUpdateWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);

        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        updateAllWidgets(this.context);
        return Result.success();
    }

    public static void updateAllWidgets(Context context) {
        Instant nextUpdate = Instant.now().plusSeconds(172800);

        for (int widgetID: AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, SolarInfoTodayTinyProvider.class))) {
            Instant updateForThis = SolarInfoTodayTiny.updateWidget(context, AppWidgetManager.getInstance(context), widgetID);
            if (updateForThis != null) {
                nextUpdate = nextUpdate.isBefore(updateForThis) ? nextUpdate : updateForThis;
            }
        }

        for (int widgetID: AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, SolarInfoTodayMediumProvider.class))) {
            Instant updateForThis = SolarInfoTodayMedium.updateWidget(context, AppWidgetManager.getInstance(context), widgetID);
            if (updateForThis != null) {
                nextUpdate = nextUpdate.isBefore(updateForThis) ? nextUpdate : updateForThis;
            }
        }

        nextUpdate = nextUpdate.plusMillis(MILLISECONDS_WAIT_AFTER_IDEAL_UPDATE);

        WorkManager workManager = WorkManager.getInstance(context);

        workManager.cancelAllWorkByTag(WORK_TAG_FOR_WIDGETS_UPDATE);

        Duration durationUntilNext = Duration.between(Instant.now(), nextUpdate);
        long durationUntilNextMillis = durationUntilNext.toMillis();
        Log.d(WidgetUpdateWorker.class.getName(), "Widget next update: " + nextUpdate.toString() + " (in " + durationUntilNextMillis + " ms)");
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(WidgetUpdateWorker.class)
                .setInitialDelay(durationUntilNextMillis, TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG_FOR_WIDGETS_UPDATE)
                .build();
        workManager.enqueue(workRequest);
    }
}
