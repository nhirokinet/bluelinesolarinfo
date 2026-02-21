package net.nhiroki.bluelinesolarinfo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.AppPreferences;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private enum LocationMeasureStatus { UNKNOWN, NO_PERMISSION, FETCHING, SUCCESS, ERROR };

    private final static int REQUEST_CODE_FOR_REGION_SETTING = 1001;


    private @Nullable LocalDate targetDate = null;
    private @Nullable RegionOnTheEarth regionOnTheEarth = null;
    private @Nullable LocationOnTheEarth currentLocation = null;
    private LocationMeasureStatus locationMeasureStatus = LocationMeasureStatus.UNKNOWN;

    private @Nullable LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private boolean locationListenerInUse = false;


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

        findViewById(R.id.main_view_first_guide_add_region_button).setOnClickListener(view -> {
            startActivity(new android.content.Intent(this, EachRegionSettingActivity.class));
        });

        findViewById(R.id.main_view_nolocpriv_guide_add_location_privilege_button).setOnClickListener(view -> {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_FOR_REGION_SETTING);
        });

        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                double height;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    height = location.hasAltitude() ? (location.hasMslAltitude() ? location.getMslAltitudeMeters() : location.getAltitude()) : 0.0;
                } else {
                    height = location.hasAltitude() ? location.getAltitude() : 0.0;
                }
                MainActivity.this.currentLocation = new LocationOnTheEarth(location.getLongitude(), location.getLatitude(), height);
                MainActivity.this.locationMeasureStatus = LocationMeasureStatus.SUCCESS;
                MainActivity.this.updateSolarInfo();
            }

            @Override
            public void onProviderDisabled(String provider) {
                MainActivity.this.currentLocation = null;
                MainActivity.this.locationMeasureStatus = LocationMeasureStatus.ERROR;
                MainActivity.this.updateSolarInfo();
            }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
        };

        ((CheckBox) findViewById(R.id.main_view_location_measure_config_use_elevation_checkbox)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppPreferences.setCurrentLocationUsesElevation(MainActivity.this.getApplicationContext(), isChecked);
            MainActivity.this.updateSolarInfo();
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
    protected void onStart() {
        super.onStart();

        this.regionOnTheEarth = DataStore.getInstance(getApplicationContext()).getDefaultRegion();
        this.targetDate = null;

        ((CheckBox) findViewById(R.id.main_view_location_measure_config_use_elevation_checkbox)).setChecked(AppPreferences.getCurrentLocationUsesElevation(getApplicationContext()));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (this.locationListenerInUse && this.locationManager != null) {
            this.locationManager.removeUpdates(this.locationListener);
            this.locationListenerInUse = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.currentLocation = null;
        this.locationMeasureStatus = LocationMeasureStatus.UNKNOWN;
        updateSolarInfo();
    }

    private void updateSolarInfo() {
        ZoneId zoneId;
        LocationOnTheEarth locationOnTheEarth;

        if (regionOnTheEarth == null) {
            boolean hasLocationPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED;
            if (hasLocationPermission) {
                findViewById(R.id.main_view_nolocpriv_guide_area).setVisibility(View.GONE);
                findViewById(R.id.main_view_location_measure_config_area).setVisibility(View.VISIBLE);
                findViewById(R.id.main_view_first_guide_area).setVisibility(View.GONE);

                if (! this.locationListenerInUse) {
                    if (this.locationManager == null) {
                        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    }

                    this.currentLocation = null;
                    this.locationMeasureStatus = LocationMeasureStatus.FETCHING;
                    this.locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 3000l, 1.0f, this.locationListener);
                    this.locationListenerInUse = true;
                }

                if (((CheckBox) findViewById(R.id.main_view_location_measure_config_use_elevation_checkbox)).isChecked()) {
                    findViewById(R.id.main_view_region_text_line2).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.main_view_region_text_line2).setVisibility(View.GONE);
                }

            } else {
                findViewById(R.id.main_view_nolocpriv_guide_area).setVisibility(View.VISIBLE);
                findViewById(R.id.main_view_location_measure_config_area).setVisibility(View.GONE);

                this.currentLocation = null;
                this.locationMeasureStatus = LocationMeasureStatus.NO_PERMISSION;

                findViewById(R.id.main_view_region_text_line2).setVisibility(View.GONE);
            }

            zoneId = ZoneId.systemDefault();

            if (this.currentLocation == null) {
                locationOnTheEarth = null;
            } else {
                if (((CheckBox) findViewById(R.id.main_view_location_measure_config_use_elevation_checkbox)).isChecked()) {
                    locationOnTheEarth = this.currentLocation;
                } else {
                    locationOnTheEarth = new LocationOnTheEarth(this.currentLocation.getLongitudeDeg(), this.currentLocation.getLatitudeDeg(), 0.0);
                }
            }

        } else {
            findViewById(R.id.main_view_location_measure_config_area).setVisibility(View.GONE);
            findViewById(R.id.main_view_first_guide_area).setVisibility(View.GONE);
            findViewById(R.id.main_view_nolocpriv_guide_area).setVisibility(View.GONE);
            boolean regionRegistered = DataStore.getInstance(getApplicationContext()).getRegions().size() > 0;
            findViewById(R.id.main_view_first_guide_area).setVisibility(regionRegistered ? View.GONE : View.VISIBLE);

            ((TextView) findViewById(R.id.main_view_region_text)).setText(regionOnTheEarth.getName());

            zoneId = regionOnTheEarth.getZoneId();
            locationOnTheEarth = regionOnTheEarth.getLocationOnTheEarth();

            findViewById(R.id.main_view_region_text_line2).setVisibility(locationOnTheEarth.getElevationMeters() == 0.0 ? View.GONE : View.VISIBLE);
        }

        if (locationOnTheEarth == null) {
            ((TextView) findViewById(R.id.main_view_region_text)).setText(R.string.main_activity_current_location);

            if (this.locationMeasureStatus == LocationMeasureStatus.NO_PERMISSION) {
                ((TextView) findViewById(R.id.main_view_region_text_line1)).setText(R.string.main_activity_message_error_permission_location_fetch);
            } else if (this.locationMeasureStatus == LocationMeasureStatus.FETCHING || this.locationMeasureStatus == LocationMeasureStatus.UNKNOWN) {
                ((TextView) findViewById(R.id.main_view_region_text_line1)).setText(R.string.main_activity_message_location_fetching);
            } else if (this.locationMeasureStatus == LocationMeasureStatus.ERROR) {
                ((TextView) findViewById(R.id.main_view_region_text_line1)).setText(R.string.main_activity_message_location_fetch_error);
            } else {
                ((TextView) findViewById(R.id.main_view_region_text_line1)).setText("");
            }
            ((TextView) findViewById(R.id.main_view_region_text_line2)).setText(R.string.main_activity_location_elevation_meters_unknown);

        } else {
            long longitudeAbs = (long) Math.floor(Math.abs(locationOnTheEarth.getLongitudeDeg()) * 36000.0);
            long latitudeAbs = (long) Math.floor(Math.abs(locationOnTheEarth.getLatitudeDeg()) * 36000.0);
            ((TextView) findViewById(R.id.main_view_region_text_line1)).setText(
                    getString(R.string.main_activity_location_coordinates_format,
                            locationOnTheEarth.getLongitudeDeg() >= 0.0 ? getString(R.string.coordinate_display_east) : getString(R.string.coordinate_display_west),
                            getString(R.string.format_unit_angle_dms, longitudeAbs / 36000, (longitudeAbs % 36000) / 600, (longitudeAbs % 600) / 10, longitudeAbs % 10),
                            locationOnTheEarth.getLatitudeDeg() >= 0.0 ? getString(R.string.coordinate_display_north) : getString(R.string.coordinate_display_south),
                            getString(R.string.format_unit_angle_dms, latitudeAbs / 36000, (latitudeAbs % 36000) / 600, (latitudeAbs % 600) / 10, latitudeAbs % 10)
                    )
            );
            ((TextView) findViewById(R.id.main_view_region_text_line2)).setText(
                    getString(R.string.main_activity_location_elevation_meters, locationOnTheEarth.getElevationMeters())
            );
        }

        Instant now = Instant.now();
        LocalDate today = now.atZone(zoneId).toLocalDate();
        LocalDate date = (targetDate == null) ? today : this.targetDate;

        String todayString = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(getResources().getConfiguration().getLocales().get(0)));
        if (date.equals(today)) {
            ((TextView) findViewById(R.id.main_view_date_view)).setText(getString(R.string.main_activity_today_format, todayString));
        } else {
            ((TextView) findViewById(R.id.main_view_date_view)).setText(todayString);
        }

        ((TextView) findViewById(R.id.main_view_region_timezone_text)).setText(
                getString(R.string.format_regions_setting_timezone_label,
                        zoneId.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, getResources().getConfiguration().getLocales().get(0)),
                        zoneId.toString()
                )
        );

        Instant todayStart = date.atStartOfDay(zoneId).toInstant();
    }
}