package net.nhiroki.bluelinesolarinfo.activities.widgetConfig;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.AppPreferences;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.bluelinesolarinfo.widgets.SolarInfoTodayTinyProvider;

import java.util.List;

public class SolarInfoTodayTinyWidgetConfigActivity extends AppCompatActivity {
    protected int appWidgetId = -1;
    private RegionOnTheEarth selectedRegion = null;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_widget_config_solarinfo_today_widget);

        this.appWidgetId = this.getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        if (this.appWidgetId != -1) {
            AppPreferences.RegionBasedWidgetConfig widgetConfig = AppPreferences.getRegionBasedWidgetConfig(this.getApplicationContext(), this.appWidgetId);
            if (widgetConfig != null) {
                this.selectedRegion = DataStore.getInstance(this.getApplicationContext()).getRegionById(widgetConfig.getRegionId());
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.config_widget_solar_info_today_change_region_button).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.regions_setting_dialog_default_region_select_title);

            List<RegionOnTheEarth> regionOnTheEarthList = DataStore.getInstance(SolarInfoTodayTinyWidgetConfigActivity.this.getApplicationContext()).getRegions();
            CharSequence[] items = new CharSequence[regionOnTheEarthList.size()];
            for (int i = 0; i < regionOnTheEarthList.size(); i++) {
                items[i] = regionOnTheEarthList.get(i).getName();
            }

            builder.setItems(items, (dialogInterface, i) -> {
                SolarInfoTodayTinyWidgetConfigActivity.this.selectedRegion = regionOnTheEarthList.get(i);
                SolarInfoTodayTinyWidgetConfigActivity.this.refreshDisplay();
            });
            builder.create().show();
        });

        findViewById(R.id.config_widget_solar_info_today_ok_button).setOnClickListener(view -> {
            AppPreferences.RegionBasedWidgetConfig widgetConfig = new AppPreferences.RegionBasedWidgetConfig(this.selectedRegion.getId());
            AppPreferences.setRegionBasedWidgetConfig(SolarInfoTodayTinyWidgetConfigActivity.this.getApplicationContext(), this.appWidgetId, widgetConfig);
            this.updateWidgets();

            Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.appWidgetId);
            SolarInfoTodayTinyWidgetConfigActivity.this.setResult(RESULT_OK, resultValue);
            SolarInfoTodayTinyWidgetConfigActivity.this.finish();
        });

        findViewById(R.id.config_widget_solar_info_today_warn_no_region_yet).setVisibility(View.GONE);
        if (this.selectedRegion == null) {
            List<RegionOnTheEarth> regionOnTheEarthList = DataStore.getInstance(SolarInfoTodayTinyWidgetConfigActivity.this.getApplicationContext()).getRegions();
            if (regionOnTheEarthList.size() > 0) {
                this.selectedRegion = regionOnTheEarthList.get(0);
            }
        }

        refreshDisplay();
    }

    private void refreshDisplay() {
        List<RegionOnTheEarth> regionOnTheEarthList = DataStore.getInstance(SolarInfoTodayTinyWidgetConfigActivity.this.getApplicationContext()).getRegions();
        if (regionOnTheEarthList.size() > 0) {
            findViewById(R.id.config_widget_solar_info_today_warn_no_region_yet).setVisibility(View.GONE);
        } else {
            findViewById(R.id.config_widget_solar_info_today_warn_no_region_yet).setVisibility(View.VISIBLE);
        }
        if (this.selectedRegion != null) {
            ((TextView) findViewById(R.id.config_widget_solar_info_today_region_name)).setText(this.selectedRegion.getName());
            findViewById(R.id.config_widget_solar_info_today_ok_button).setEnabled(true);
        } else {
            ((TextView) findViewById(R.id.config_widget_solar_info_today_region_name)).setText(R.string.config_widget_solar_info_today_no_region);
            findViewById(R.id.config_widget_solar_info_today_ok_button).setEnabled(false);
        }
    }

    private void updateWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        SolarInfoTodayTinyProvider.updateWidget(this.getApplicationContext(), appWidgetManager, this.appWidgetId);
    }
}
