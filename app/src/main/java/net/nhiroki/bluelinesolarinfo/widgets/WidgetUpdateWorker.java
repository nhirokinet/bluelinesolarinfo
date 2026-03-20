package net.nhiroki.bluelinesolarinfo.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WidgetUpdateWorker extends Worker {
    private static final String WORK_TAG_FOR_WIDGETS_UPDATE = "widgets_update";
    private static final int MILLISECONDS_WAIT_AFTER_IDEAL_UPDATE = 2000;

    private final Context context;

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
        Instant nextUpdate = null;

        for (int widgetID: AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, SolarInfoTodayTinyProvider.class))) {
            Instant updateForThis = SolarInfoTodayTiny.updateWidget(context, AppWidgetManager.getInstance(context), widgetID);
            if (nextUpdate == null) {
                nextUpdate = updateForThis;
            } else {
                if (updateForThis != null) {
                    nextUpdate = nextUpdate.isBefore(updateForThis) ? nextUpdate : updateForThis;
                }
            }
        }

        for (int widgetID: AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, SolarInfoTodayMediumProvider.class))) {
            Instant updateForThis = SolarInfoTodayMedium.updateWidget(context, AppWidgetManager.getInstance(context), widgetID);
            if (nextUpdate == null) {
                nextUpdate = updateForThis;
            } else {
                if (updateForThis != null) {
                    nextUpdate = nextUpdate.isBefore(updateForThis) ? nextUpdate : updateForThis;
                }
            }
        }

        WorkManager workManager = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> workersInfo = workManager.getWorkInfosByTag(WORK_TAG_FOR_WIDGETS_UPDATE);

        try {
            for (WorkInfo workInfo: workersInfo.get()) {
                // RUNNING one may be oneself
                if (workInfo.getState().equals(WorkInfo.State.ENQUEUED)) {
                    Log.d(WidgetUpdateWorker.class.getName(), "Canceling ENQUEUED widget update worker: " + workInfo.getId());
                    workManager.cancelWorkById(workInfo.getId());
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(WidgetUpdateWorker.class.getName(), "Failed to get scheduled widget update workers. Skipping cancelling existing ones.", e);
        }

        if (nextUpdate == null) {
            Log.d(WidgetUpdateWorker.class.getName(), "No widgets to update. Not scheduling next update.");
            return;
        }

        nextUpdate = nextUpdate.plusMillis(MILLISECONDS_WAIT_AFTER_IDEAL_UPDATE);

        if (nextUpdate.isBefore(Instant.now().plusSeconds(30))) {
            nextUpdate = Instant.now().plusSeconds(30);
        }

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
