package net.nhiroki.bluelinesolarinfo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LocalDate targetDate = null;
    RegionOnTheEarth regionOnTheEarth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.main_view_date_view).setOnClickListener(view -> {
            LocalDate date = targetDate;
            if (targetDate == null) {
                if (MainActivity.this.regionOnTheEarth == null) {
                    date = LocalDate.now();
                } else {
                    date = LocalDate.now(regionOnTheEarth.getZoneId());
                }
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
                MainActivity.this.targetDate = LocalDate.of(year, month + 1, day);
                MainActivity.this.updateSolarInfo();
            }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
            datePickerDialog.show();
        });

        findViewById(R.id.main_view_region_area).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.main_activity_region_select_dialog_title);

            List<RegionOnTheEarth> regionOnTheEarthList = DataStore.getInstance(getApplicationContext()).getRegions();
            CharSequence[] items = new CharSequence[regionOnTheEarthList.size() + 1];
            items[0] = getString(R.string.main_activity_current_location);
            for (int i = 0; i < regionOnTheEarthList.size(); i++) {
                items[i + 1] = regionOnTheEarthList.get(i).getName();
            }

            builder.setItems(items, (dialogInterface, i) -> {
                if (i == 0) {
                    MainActivity.this.regionOnTheEarth = null;
                } else {
                    MainActivity.this.regionOnTheEarth = regionOnTheEarthList.get(i - 1);
                }
                MainActivity.this.updateSolarInfo();
            });

            builder.create().show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_locations_setting) {
            Intent intent = new Intent(this, RegionsSettingActivity.class);
            this.startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.menu_item_app_info) {
            Intent intent = new Intent(this, AppInfoActivity.class);
            this.startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.regionOnTheEarth = DataStore.getInstance(getApplicationContext()).getDefaultRegion();

        updateSolarInfo();
    }

    private void updateSolarInfo() {
        LocalDate today = regionOnTheEarth != null ? LocalDate.now(regionOnTheEarth.getZoneId()) : LocalDate.now();
        LocalDate date = (targetDate == null) ? today : targetDate;

        String todayString = date.toString();
        if (date.equals(today)) {
            ((TextView) findViewById(R.id.main_view_date_view)).setText(getString(R.string.main_activity_today_format, todayString));
        } else {
            ((TextView) findViewById(R.id.main_view_date_view)).setText(todayString);
        }


        if (regionOnTheEarth == null) {
            // TODO: handle GPS location
            ((TextView) findViewById(R.id.main_view_region_text)).setText(R.string.main_activity_region_not_set);
            ((TextView) findViewById(R.id.main_view_region_coordinates_text)).setText("");
            ((TextView) findViewById(R.id.main_view_region_elevation_text)).setText("");
            return;
        }

        ((TextView) findViewById(R.id.main_view_region_text)).setText(regionOnTheEarth.getName());
        long longitudeAbs = (long) Math.floor(Math.abs(regionOnTheEarth.getLocationOnTheEarth().getLongitudeDeg()) * 36000.0);
        long latitudeAbs = (long) Math.floor(Math.abs(regionOnTheEarth.getLocationOnTheEarth().getLatitudeDeg()) * 36000.0);
        ((TextView) findViewById(R.id.main_view_region_coordinates_text)).setText(
                getString(R.string.main_activity_location_coordinates_format,
                        regionOnTheEarth.getLocationOnTheEarth().getLongitudeDeg() >= 0.0 ? getString(R.string.coordinate_display_east) : getString(R.string.coordinate_display_west),
                        getString(R.string.format_unit_angle_dms, longitudeAbs / 36000, (longitudeAbs % 36000) / 600, (longitudeAbs % 600) / 10, longitudeAbs % 10),
                        regionOnTheEarth.getLocationOnTheEarth().getLatitudeDeg() >= 0.0 ? getString(R.string.coordinate_display_north) : getString(R.string.coordinate_display_south),
                        getString(R.string.format_unit_angle_dms, latitudeAbs / 36000, (latitudeAbs % 36000) / 600, (latitudeAbs % 600) / 10, latitudeAbs % 10)
                        )
        );
        ((TextView) findViewById(R.id.main_view_region_elevation_text)).setText(
                getString(R.string.main_activity_location_elevation_meters, regionOnTheEarth.getLocationOnTheEarth().getElevationMeters())
        );

        Instant t = date.atStartOfDay(regionOnTheEarth.getZoneId()).toInstant();
    }
}