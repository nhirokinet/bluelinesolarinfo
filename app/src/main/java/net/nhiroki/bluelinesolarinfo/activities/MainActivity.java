package net.nhiroki.bluelinesolarinfo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.nhiroki.androidlib.bluelineastroandroidlib.views.MoonPhaseView;
import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.AppPreferences;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.bluelinesolarinfo.stringformats.AppTimeFormat;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Moon;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.coordinates.HorizontalCoordinatesFromGround;
import net.nhiroki.lib.bluelineastrolib.coordinates.HorizontalCoordinatesFromTheCenterOfTheEarth;
import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;
import net.nhiroki.lib.bluelineastrolib.coordinates.LocationOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.logic.AstronomicalEventsCalculation;
import net.nhiroki.lib.bluelineastrolib.tool.MoonTool;
import net.nhiroki.lib.bluelineastrolib.tool.SunTool;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_REGION_ID = "region_id";
    public final static String EXTRA_FROM_APPWIDGETID = "from_appwidgetid";
    public final static String EXTRA_TARGET_TIME_UNIX_MILLISEC = "target_time_unix_millisec";

    private enum LocationMeasureStatus { UNKNOWN, NO_PERMISSION, FETCHING, SUCCESS, ERROR }

    private final static int REQUEST_CODE_FOR_REGION_SETTING = 1001;
    public final static int REQUEST_CODE_FOR_OPENING_BY_WIDGET = 0x10000000;

    private static final int[] direction16StrResIds = new int[] {
            R.string.direction_short_16_area_0,
            R.string.direction_short_16_area_1,
            R.string.direction_short_16_area_2,
            R.string.direction_short_16_area_3,
            R.string.direction_short_16_area_4,
            R.string.direction_short_16_area_5,
            R.string.direction_short_16_area_6,
            R.string.direction_short_16_area_7,
            R.string.direction_short_16_area_8,
            R.string.direction_short_16_area_9,
            R.string.direction_short_16_area_10,
            R.string.direction_short_16_area_11,
            R.string.direction_short_16_area_12,
            R.string.direction_short_16_area_13,
            R.string.direction_short_16_area_14,
            R.string.direction_short_16_area_15,
    };


    private @Nullable LocalDate targetDate = null;
    private @Nullable RegionOnTheEarth regionOnTheEarth = null;
    private @Nullable LocationOnTheEarth currentLocation = null;
    private LocationMeasureStatus locationMeasureStatus = LocationMeasureStatus.UNKNOWN;

    private @Nullable LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private boolean locationListenerInUse = false;
    private String locationPrevProvider = "";
    private double locationPrevAccuracy = 1000000.0;
    private double locationPrevElevationAccuracy = 0.0;

    private Handler refreshHandler;
    private Runnable refreshRunnable;

    private LocalDate currentDisplayedDate = null;
    private ZoneId currentDisplayedZoneId = null;
    private LocationOnTheEarth currentDisplayedLocationOnTheEarth = null;


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

        this.refreshHandler = new Handler(Looper.getMainLooper());
        this.refreshRunnable = MainActivity.this::periodicRefresh;

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

        findViewById(R.id.main_view_first_guide_add_region_button).setOnClickListener(view -> startActivity(new Intent(this, EachRegionSettingActivity.class)));

        findViewById(R.id.main_view_nolocpriv_guide_add_location_privilege_button).setOnClickListener(view -> ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE_FOR_REGION_SETTING));

        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                if (MainActivity.this.currentLocation != null) {
                    if (! locationPrevProvider.equals(LocationManager.PASSIVE_PROVIDER)) {
                        if (locationPrevProvider.equals(LocationManager.FUSED_PROVIDER) && !location.getProvider().equals(LocationManager.FUSED_PROVIDER)) {
                            return;
                        }
                        if ((!locationPrevProvider.equals(location.getProvider())) && locationPrevAccuracy <= location.getAccuracy()) {
                            return;
                        }
                    }
                }
                double height;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    height = location.hasAltitude() ? (location.hasMslAltitude() ? location.getMslAltitudeMeters() : location.getAltitude()) : 0.0;
                } else {
                    height = location.hasAltitude() ? location.getAltitude() : 0.0;
                }
                MainActivity.this.currentLocation = LocationOnTheEarth.ofDegreesMeters(location.getLongitude(), location.getLatitude(), height);
                MainActivity.this.locationMeasureStatus = LocationMeasureStatus.SUCCESS;
                MainActivity.this.locationPrevProvider = location.getProvider();
                MainActivity.this.locationPrevAccuracy = location.getAccuracy();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && location.hasMslAltitude()) {
                    MainActivity.this.locationPrevElevationAccuracy = location.getMslAltitudeAccuracyMeters();
                } else {
                    MainActivity.this.locationPrevElevationAccuracy = location.hasAltitude() ? location.getVerticalAccuracyMeters() : -1.0;
                }
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
        };

        ((CheckBox) findViewById(R.id.main_view_location_measure_config_use_elevation_checkbox)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppPreferences.setCurrentLocationUsesElevation(MainActivity.this.getApplicationContext(), isChecked);
            MainActivity.this.updateSolarInfo();
        });

        findViewById(R.id.main_view_solar_info_today_this_moon_cycle_button).setOnClickListener(view -> {
            View dialogView = getLayoutInflater().inflate(R.layout.activity_main_dialog_this_moon_cycle, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            builder.create();

            Instant endOfTheDay = this.currentDisplayedDate.plusDays(1).atStartOfDay(this.currentDisplayedZoneId).toInstant();

            Locale locale = getResources().getConfiguration().getLocales().get(0);
            boolean timeFormat24Hour = android.text.format.DateFormat.is24HourFormat(this.getApplicationContext());

            ((TextView) dialogView.findViewById(R.id.main_activity_dialog_this_lunar_cycle_timezone)).setText(
                    getString(R.string.format_regions_setting_timezone_label,
                            this.currentDisplayedZoneId.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, getResources().getConfiguration().getLocales().get(0)),
                            this.currentDisplayedZoneId.toString()
                    )
            );

            try {
                Instant prevNewMoon = MoonTool.calculatePreviousTimeOfMoonPhase(endOfTheDay, 0.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_this_lunar_cycle_moon_phase_0_date)).setText(AppTimeFormat.fullDateTimeForEventForList(prevNewMoon, this.currentDisplayedZoneId, timeFormat24Hour, locale));

                Instant time90Deg = MoonTool.calculateNextTimeOfMoonPhase(prevNewMoon, 90.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_this_lunar_cycle_moon_phase_90_date)).setText(AppTimeFormat.fullDateTimeForEventForList(time90Deg, this.currentDisplayedZoneId, timeFormat24Hour, locale));
                Instant time180Deg = MoonTool.calculateNextTimeOfMoonPhase(time90Deg, 180.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_this_lunar_cycle_moon_phase_180_date)).setText(AppTimeFormat.fullDateTimeForEventForList(time180Deg, this.currentDisplayedZoneId, timeFormat24Hour, locale));
                Instant time270Deg = MoonTool.calculateNextTimeOfMoonPhase(time180Deg, 270.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_this_lunar_cycle_moon_phase_270_date)).setText(AppTimeFormat.fullDateTimeForEventForList(time270Deg, this.currentDisplayedZoneId, timeFormat24Hour, locale));
                Instant time360Deg = MoonTool.calculateNextTimeOfMoonPhase(time270Deg, 0.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_this_lunar_cycle_moon_phase_360_date)).setText(AppTimeFormat.fullDateTimeForEventForList(time360Deg, this.currentDisplayedZoneId, timeFormat24Hour, locale));

            } catch (AstronomicalPhenomenonComputationException e) {
                throw new RuntimeException(e);
            }

            builder.show();
        });

        findViewById(R.id.main_view_solar_info_today_sun_this_year_button).setOnClickListener(view -> {
            View dialogView = getLayoutInflater().inflate(R.layout.activity_main_dialog_sun_events_this_year, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            builder.create();

            int year = this.currentDisplayedDate.getYear();

            Locale locale = getResources().getConfiguration().getLocales().get(0);
            boolean timeFormat24Hour = android.text.format.DateFormat.is24HourFormat(this.getApplicationContext());

            ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_timezone)).setText(
                    getString(R.string.format_regions_setting_timezone_label,
                            this.currentDisplayedZoneId.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, getResources().getConfiguration().getLocales().get(0)),
                            this.currentDisplayedZoneId.toString()
                    )
            );

            Instant startOfTheYear = LocalDate.of(year, 1, 1).atStartOfDay(this.currentDisplayedZoneId).toInstant();
            ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_title_year)).setText(LocalDate.of(year, 1, 1).format(DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, "yyyy"), locale)));

            if (this.currentDisplayedLocationOnTheEarth.getLatitudeDeg() >= 0.0) {
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_event_0_label)).setText(R.string.event_name_sun_ecliptic_longitude_0_north);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_event_90_label)).setText(R.string.event_name_sun_ecliptic_longitude_90_north);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_event_180_label)).setText(R.string.event_name_sun_ecliptic_longitude_180_north);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_event_270_label)).setText(R.string.event_name_sun_ecliptic_longitude_270_north);

            } else {
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_event_0_label)).setText(R.string.event_name_sun_ecliptic_longitude_0_south);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_event_90_label)).setText(R.string.event_name_sun_ecliptic_longitude_90_south);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_event_180_label)).setText(R.string.event_name_sun_ecliptic_longitude_180_south);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_event_270_label)).setText(R.string.event_name_sun_ecliptic_longitude_270_south);

            }
            try {
                Instant nextEquinox0 = SunTool.calculateNextTimeOfEclipticLongitude(startOfTheYear, 0.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_equinox_0_date)).setText(AppTimeFormat.fullDateTimeHourPrecisionForEventForList(nextEquinox0, this.currentDisplayedZoneId, timeFormat24Hour, locale));
                Instant nextEquinox90 = SunTool.calculateNextTimeOfEclipticLongitude(nextEquinox0, 90.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_equinox_90_date)).setText(AppTimeFormat.fullDateTimeHourPrecisionForEventForList(nextEquinox90, this.currentDisplayedZoneId, timeFormat24Hour, locale));
                Instant nextEquinox180 = SunTool.calculateNextTimeOfEclipticLongitude(nextEquinox90, 180.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_equinox_180_date)).setText(AppTimeFormat.fullDateTimeHourPrecisionForEventForList(nextEquinox180, this.currentDisplayedZoneId, timeFormat24Hour, locale));
                Instant nextEquinox270 = SunTool.calculateNextTimeOfEclipticLongitude(nextEquinox180, 270.0);
                ((TextView) dialogView.findViewById(R.id.main_activity_dialog_sun_this_year_equinox_270_date)).setText(AppTimeFormat.fullDateTimeHourPrecisionForEventForList(nextEquinox270, this.currentDisplayedZoneId, timeFormat24Hour, locale));
            } catch (AstronomicalPhenomenonComputationException e) {
                throw new RuntimeException(e);
            }

            builder.show();
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

        Intent intentFrom = this.getIntent();
        long regionId = intentFrom.getLongExtra(EXTRA_REGION_ID, -1);

        if (regionId == -1) {
            this.regionOnTheEarth = DataStore.getInstance(getApplicationContext()).getDefaultRegion();
        } else {
            this.regionOnTheEarth = DataStore.getInstance(getApplicationContext()).getRegionById(regionId);
            if (this.regionOnTheEarth == null) {
                this.regionOnTheEarth = DataStore.getInstance(getApplicationContext()).getDefaultRegion();
            }
        }
        long targetDateUnixMilliSec = intentFrom.getLongExtra(EXTRA_TARGET_TIME_UNIX_MILLISEC, -1);
        if (targetDateUnixMilliSec == -1) {
            this.targetDate = null;
        } else {
            ZoneId zoneId = ZoneId.systemDefault();
            if (this.regionOnTheEarth != null && this.regionOnTheEarth.getZoneId() != null) {
                zoneId = this.regionOnTheEarth.getZoneId();
            }
            this.targetDate = Instant.ofEpochMilli(targetDateUnixMilliSec).atZone(zoneId).toLocalDate();
        }

        ((CheckBox) findViewById(R.id.main_view_location_measure_config_use_elevation_checkbox)).setChecked(AppPreferences.getCurrentLocationUsesElevation(getApplicationContext()));
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.refreshHandler.removeCallbacks(this.refreshRunnable);
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

    public static int getRequestCodeForOpeningByWidget(int appWidgetId) {
        return REQUEST_CODE_FOR_OPENING_BY_WIDGET | (appWidgetId & 0xfffffff);
    }

    private ZoneId previousZoneIdForRefresh = null;
    private LocationOnTheEarth previousLocationForRefresh = null;
    private void periodicRefresh() {
        if (this.previousLocationForRefresh != null && this.previousZoneIdForRefresh != null) {
            Instant now = Instant.now();

            LocalDate today = now.atZone(this.previousZoneIdForRefresh).toLocalDate();
            LocalDate date = (targetDate == null) ? today : this.targetDate;

            String todayString = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(getResources().getConfiguration().getLocales().get(0)));
            if (date.equals(today)) {
                ((TextView) findViewById(R.id.main_view_date_view)).setText(getString(R.string.main_activity_today_format, todayString));
            } else {
                ((TextView) findViewById(R.id.main_view_date_view)).setText(todayString);
            }
            this.displayNowSolarInfo(now, this.previousLocationForRefresh, this.previousZoneIdForRefresh);
        }
    }

    private void updateSolarInfo() {
        ZoneId zoneId;
        LocationOnTheEarth locationOnTheEarth;

        if (regionOnTheEarth == null) {
            findViewById(R.id.main_view_region_location_measure_status_text).setVisibility(View.VISIBLE);

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
                    this.locationPrevProvider = "";
                    this.locationPrevAccuracy = 1000000.0;
                    for (String provider : this.locationManager.getProviders(true)) {
                        this.locationManager.requestLocationUpdates(provider, 3000L, 1.0f, this.locationListener);
                    }
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
                    locationOnTheEarth = LocationOnTheEarth.ofDegreesMeters(this.currentLocation.getLongitudeDeg(), this.currentLocation.getLatitudeDeg(), 0.0);
                }
            }

        } else {
            findViewById(R.id.main_view_region_location_measure_status_text).setVisibility(View.GONE);
            findViewById(R.id.main_view_location_measure_config_area).setVisibility(View.GONE);
            findViewById(R.id.main_view_first_guide_area).setVisibility(View.GONE);
            findViewById(R.id.main_view_nolocpriv_guide_area).setVisibility(View.GONE);
            boolean regionRegistered = !DataStore.getInstance(getApplicationContext()).getRegions().isEmpty();
            findViewById(R.id.main_view_first_guide_area).setVisibility(regionRegistered ? View.GONE : View.VISIBLE);

            ((TextView) findViewById(R.id.main_view_region_text)).setText(regionOnTheEarth.getName());

            zoneId = regionOnTheEarth.getZoneId();
            locationOnTheEarth = regionOnTheEarth.getLocationOnTheEarth();

            findViewById(R.id.main_view_region_text_line2).setVisibility(locationOnTheEarth.getElevationMeters() == 0.0 ? View.GONE : View.VISIBLE);

            if (this.locationListenerInUse && this.locationManager != null) {
                this.locationManager.removeUpdates(this.locationListener);
                this.locationListenerInUse = false;
            }
        }

        if (locationOnTheEarth == null) {
            ((TextView) findViewById(R.id.main_view_region_text)).setText(R.string.main_activity_current_location);

            if (this.locationMeasureStatus == LocationMeasureStatus.NO_PERMISSION) {
                ((TextView) findViewById(R.id.main_view_region_location_measure_status_text)).setText(R.string.main_activity_message_error_permission_location_fetch);
            } else if (this.locationMeasureStatus == LocationMeasureStatus.FETCHING || this.locationMeasureStatus == LocationMeasureStatus.UNKNOWN) {
                ((TextView) findViewById(R.id.main_view_region_location_measure_status_text)).setText(R.string.main_activity_message_location_fetching);
            } else if (this.locationMeasureStatus == LocationMeasureStatus.ERROR) {
                ((TextView) findViewById(R.id.main_view_region_location_measure_status_text)).setText(R.string.main_activity_message_location_fetch_error);
            } else {
                ((TextView) findViewById(R.id.main_view_region_location_measure_status_text)).setText("");
            }
            ((TextView) findViewById(R.id.main_view_region_text_line1)).setText(R.string.main_activity_location_unknown_line);
            ((TextView) findViewById(R.id.main_view_region_text_line2)).setText(R.string.main_activity_location_unknown_line);

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
            if (((CheckBox) findViewById(R.id.main_view_location_measure_config_use_elevation_checkbox)).isChecked() && locationPrevElevationAccuracy != -1.0) {
                ((TextView) findViewById(R.id.main_view_region_location_measure_status_text)).setText(
                        getString(R.string.main_activity_location_provider_info_with_elevation_format,
                                locationPrevProvider, locationPrevAccuracy, locationPrevElevationAccuracy)
                );

            } else {
                ((TextView) findViewById(R.id.main_view_region_location_measure_status_text)).setText(
                        getString(R.string.main_activity_location_provider_info_format,
                                locationPrevProvider, locationPrevAccuracy)
                );

            }
        }

        Instant now = Instant.now();
        LocalDate today = now.atZone(zoneId).toLocalDate();
        LocalDate date = (this.targetDate == null) ? today : this.targetDate;

        this.currentDisplayedDate = date;
        this.currentDisplayedZoneId = zoneId;
        this.currentDisplayedLocationOnTheEarth = locationOnTheEarth;

        String todayString = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(getResources().getConfiguration().getLocales().get(0)));
        if (date.equals(today)) {
            ((TextView) findViewById(R.id.main_view_date_view)).setText(getString(R.string.main_activity_today_format, todayString));
            this.targetDate = null; // If coming here, it should consist even after date changes
        } else {
            ((TextView) findViewById(R.id.main_view_date_view)).setText(todayString);
        }

        ((TextView) findViewById(R.id.main_view_region_timezone_text)).setText(
                getString(R.string.format_regions_setting_timezone_label,
                        zoneId.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, getResources().getConfiguration().getLocales().get(0)),
                        zoneId.toString()
                )
        );

        if (locationOnTheEarth == null) {
            findViewById(R.id.main_view_solar_info_today_area).setVisibility(View.GONE);
            findViewById(R.id.main_view_solar_info_now_area).setVisibility(View.GONE);
            return;
        }

        findViewById(R.id.main_view_solar_info_today_area).setVisibility(View.VISIBLE);

        Instant startOfTheDay = date.atStartOfDay(zoneId).toInstant();
        Instant midOfTheDay = date.atTime(12, 0).atZone(zoneId).toInstant();
        Instant endOfTheDay = date.plusDays(1).atStartOfDay(zoneId).toInstant();
        this.displayTodaySolarInfo(startOfTheDay, midOfTheDay, endOfTheDay, locationOnTheEarth, zoneId);

        if (date.equals(today)) {
            this.displayNowSolarInfo(now, locationOnTheEarth, zoneId);
            findViewById(R.id.main_view_solar_info_now_area).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.main_view_solar_info_now_area).setVisibility(View.GONE);
            this.refreshHandler.removeCallbacks(this.refreshRunnable);
        }
    }

    private static @StringRes int degToStrResId(double deg) {
        int area = (int) Math.floor((deg + 11.25) / 22.5);
        area = area % 16;
        return direction16StrResIds[area];
    }

    // Uses @midOfTheDay to calculate moon phase. Required because it can be 11 hours or 13 hours from @todayStart due to DST
    // @endOfTheDay is assumed to be 23-25 hours from @todayStart
    private void displayTodaySolarInfo(Instant startOfTheDay, Instant midOfTheDay, Instant endOfTheDay, LocationOnTheEarth locationOnTheEarth, ZoneId zoneId) {
        Sun sun = new Sun();
        Moon moon = new Moon();

        Locale locale = getResources().getConfiguration().getLocales().get(0);
        boolean timeFormat24Hour = android.text.format.DateFormat.is24HourFormat(this.getApplicationContext());

        try {
            double sunEclipticLongittudeDegAtNoon = sun.calculateEclipticCoordinates(midOfTheDay).getLongitudeDeg();
            int sunEclipticLongitudeMin = (int) Math.floor(sunEclipticLongittudeDegAtNoon * 60.0);
            ((TextView) findViewById(R.id.main_view_solar_info_today_sun_ecliptic_longitude_today)).setText(getString(R.string.format_unit_angle_dm, sunEclipticLongitudeMin / 60, sunEclipticLongitudeMin % 60));

            Instant sunrise = AstronomicalEventsCalculation.calculateRiseWithin24h(sun, startOfTheDay, locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
            if (sunrise == null && endOfTheDay.isAfter(startOfTheDay.plusSeconds(86400))) {
                sunrise = AstronomicalEventsCalculation.calculateRiseWithin24h(sun, endOfTheDay.minusSeconds(86400), locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
            }
            if (sunrise != null && sunrise.isAfter(endOfTheDay)) {
                sunrise = null;
            }
            ((TextView) findViewById(R.id.main_view_solar_info_today_sunrise_time)).setText(AppTimeFormat.instantToHmStringForEventTime(sunrise, zoneId, timeFormat24Hour, locale));
            if (sunrise != null) {
                double sunAzimuthDeg = HorizontalCoordinatesFromTheCenterOfTheEarth.ofAstronomicalObject(sun, sunrise, locationOnTheEarth).getAzimuthDeg();
                if (Double.isNaN(sunAzimuthDeg)) {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sunrise_direction)).setText("");
                } else {
                    long sunAzimuthArcMin = Math.round(sunAzimuthDeg * 60.0);
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sunrise_direction)).setText(getString(R.string.main_activity_solar_info_today_azimuth_str_format, getString(R.string.format_unit_angle_dm, sunAzimuthArcMin / 60, sunAzimuthArcMin % 60), getString(degToStrResId(sunAzimuthDeg))));
                }
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sunrise_direction)).setText("");
            }

            Instant sunculmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(sun, startOfTheDay, locationOnTheEarth);
            if (sunculmination == null && endOfTheDay.isAfter(startOfTheDay.plusSeconds(86400))) {
                sunculmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(sun, endOfTheDay.minusSeconds(86400), locationOnTheEarth);
            }
            if (sunculmination != null && sunculmination.isAfter(endOfTheDay)) {
                sunculmination = null;
            }
            ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_time)).setText(AppTimeFormat.instantToHmStringForEventTime(sunculmination, zoneId, timeFormat24Hour, locale));
            if (sunculmination != null) {
                HorizontalCoordinatesFromGround sunAppearance = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(sun, sunculmination, locationOnTheEarth);
                if (sunAppearance.isTopAboveHorizon()) {
                    double sunAzimuthDeg = sunAppearance.getAzimuthDeg();
                    double sunElevationDeg = sunAppearance.calculateApparentElevationDeg();
                    long sunElevationArcMin = Math.round(sunElevationDeg * 60.0);
                    long sunElevationArcMinAbs = Math.abs(sunElevationArcMin);
                    String sunElevationSign = sunElevationDeg >= 0.0 ? "+" : "-";
                    if (Double.isNaN(sunAzimuthDeg)) {
                        ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_invisible_label)).setText(getString(R.string.main_activity_solar_info_today_culmination_direction_format, "", getString(R.string.format_unit_angle_dm_signed, sunElevationSign, sunElevationArcMinAbs / 60, sunElevationArcMinAbs % 60)));
                    } else {
                        ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_invisible_label)).setText(getString(R.string.main_activity_solar_info_today_culmination_direction_format, getString(degToStrResId(sunAzimuthDeg)), getString(R.string.format_unit_angle_dm_signed, sunElevationSign, sunElevationArcMinAbs / 60, sunElevationArcMinAbs % 60)));
                    }
                } else {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_invisible_label)).setText(R.string.main_activity_solar_info_today_invisible_culmination_label);
                }
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_invisible_label)).setText("");
            }

            Instant sunset = AstronomicalEventsCalculation.calculateSetWithin24h(sun, startOfTheDay, locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
            if (sunset == null && endOfTheDay.isAfter(startOfTheDay.plusSeconds(86400))) {
                sunset = AstronomicalEventsCalculation.calculateSetWithin24h(sun, endOfTheDay.minusSeconds(86400), locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
            }
            if (sunset != null && sunset.isAfter(endOfTheDay)) {
                sunset = null;
            }
            ((TextView) findViewById(R.id.main_view_solar_info_today_sunset_time)).setText(AppTimeFormat.instantToHmStringForEventTime(sunset, zoneId, timeFormat24Hour, locale));
            if (sunset != null) {
                double sunAzimuthDeg = HorizontalCoordinatesFromTheCenterOfTheEarth.ofAstronomicalObject(sun, sunset, locationOnTheEarth).getAzimuthDeg();
                if (Double.isNaN(sunAzimuthDeg)) {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sunset_direction)).setText("");
                } else {
                    long sunAzimuthArcMin = Math.round(sunAzimuthDeg * 60.0);
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sunset_direction)).setText(getString(R.string.main_activity_solar_info_today_azimuth_str_format, getString(R.string.format_unit_angle_dm, sunAzimuthArcMin / 60, sunAzimuthArcMin % 60), getString(degToStrResId(sunAzimuthDeg))));
                }
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sunset_direction)).setText("");
            }

            if (sunrise == null && sunset == null) {
                Instant nextEventTime = null;
                boolean nextEventIsRise = false;

                Instant t = endOfTheDay.minusSeconds(1);
                for (int i = 0; i < 270; ++i) {
                    Instant sunriseCandidate = AstronomicalEventsCalculation.calculateRiseWithin24h(sun, t, locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
                    Instant sunsetCandidate = AstronomicalEventsCalculation.calculateSetWithin24h(sun, t, locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);

                    if (sunriseCandidate != null || sunsetCandidate != null) {
                        if (sunriseCandidate != null && sunsetCandidate != null) {
                            if (sunriseCandidate.isBefore(sunsetCandidate)) {
                                nextEventTime = sunriseCandidate;
                                nextEventIsRise = true;
                            } else {
                                nextEventTime = sunsetCandidate;
                                nextEventIsRise = false;
                            }
                        } else if (sunriseCandidate != null) {
                            nextEventTime = sunriseCandidate;
                            nextEventIsRise = true;
                        } else {
                            nextEventTime = sunsetCandidate;
                            nextEventIsRise = false;
                        }


                        break;
                    }

                    t = t.plusSeconds(86399);
                }

                if (nextEventTime != null) {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sun_next_event_title)).setText(nextEventIsRise ? R.string.main_activity_solar_info_today_next_event_sunrise : R.string.main_activity_solar_info_today_next_event_sunset);
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sun_next_event_time)).setText(AppTimeFormat.fullDateTimeForEventNatural(nextEventTime, zoneId, timeFormat24Hour, locale));

                    findViewById(R.id.main_view_solar_info_today_sun_next_event_title).setVisibility(View.VISIBLE);
                    findViewById(R.id.main_view_solar_info_today_sun_next_event_time).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.main_view_solar_info_today_sun_next_event_title).setVisibility(View.GONE);
                    findViewById(R.id.main_view_solar_info_today_sun_next_event_time).setVisibility(View.GONE);
                }

            } else {
                findViewById(R.id.main_view_solar_info_today_sun_next_event_title).setVisibility(View.GONE);
                findViewById(R.id.main_view_solar_info_today_sun_next_event_time).setVisibility(View.GONE);
            }

            double equationOfTimeSec = new Sun().calculateEquationOfTimeSec(midOfTheDay);
            String equationOfTimeSign = (equationOfTimeSec >= 0.0) ? "+" : "-";
            long euqationOfTimeSecAbs = Math.abs(Math.round(equationOfTimeSec));
            ((TextView) findViewById(R.id.main_view_solar_info_now_equation_of_time)).setText(getString(R.string.main_activity_solar_info_today_equation_of_time_format, equationOfTimeSign, euqationOfTimeSecAbs / 60, euqationOfTimeSecAbs % 60));

            double moonPhaseDeg = MoonTool.calculateMoonPhaseDeg(midOfTheDay);
            ((MoonPhaseView) findViewById(R.id.main_view_solar_info_today_moon_phase_view)).setMoonPhaseDeg((float) moonPhaseDeg);

            Instant prevNewMoon = MoonTool.calculatePreviousTimeOfMoonPhase(midOfTheDay, 0.0);
            double daysAfterPrevNewMoon = ((double)(midOfTheDay.toEpochMilli() - prevNewMoon.toEpochMilli())) / 86400000.0;
            ((TextView) findViewById(R.id.main_view_solar_info_today_moon_phase_days_text)).setText(String.format(locale, "%.1f", daysAfterPrevNewMoon));

            Instant moonrise = AstronomicalEventsCalculation.calculateRiseWithin24h(moon, startOfTheDay, locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.CENTER);
            if (moonrise == null && endOfTheDay.isAfter(startOfTheDay.plusSeconds(86400))) {
                moonrise = AstronomicalEventsCalculation.calculateRiseWithin24h(moon, endOfTheDay.minusSeconds(86400), locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.CENTER);
            }
            if (moonrise != null && moonrise.isAfter(endOfTheDay)) {
                moonrise = null;
            }
            ((TextView) findViewById(R.id.main_view_solar_info_today_moonrise_time)).setText(AppTimeFormat.instantToHmStringForEventTime(moonrise, zoneId, timeFormat24Hour, locale));
            if (moonrise != null) {
                double moonAzimuthDeg = HorizontalCoordinatesFromTheCenterOfTheEarth.ofAstronomicalObject(moon, moonrise, locationOnTheEarth).getAzimuthDeg();
                if (Double.isNaN(moonAzimuthDeg)) {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moonrise_direction)).setText("");
                } else {
                    long moonAzimuthArcMin = Math.round(moonAzimuthDeg * 60.0);
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moonrise_direction)).setText(getString(R.string.main_activity_solar_info_today_azimuth_str_format, getString(R.string.format_unit_angle_dm, moonAzimuthArcMin / 60, moonAzimuthArcMin % 60), getString(degToStrResId(moonAzimuthDeg))));
                }
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moonrise_direction)).setText("");
            }

            Instant moonculmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(moon, startOfTheDay, locationOnTheEarth);
            if (moonculmination == null && endOfTheDay.isAfter(startOfTheDay.plusSeconds(86400))) {
                moonculmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(moon, endOfTheDay.minusSeconds(86400), locationOnTheEarth);
            }
            if (moonculmination != null && moonculmination.isAfter(endOfTheDay)) {
                moonculmination = null;
            }
            ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_time)).setText(AppTimeFormat.instantToHmStringForEventTime(moonculmination, zoneId, timeFormat24Hour, locale));
            if (moonculmination != null) {
                HorizontalCoordinatesFromGround moonAppearance = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(moon, moonculmination, locationOnTheEarth);

                if (moonAppearance.isTopAboveHorizon()) {
                    double moonAzimuthDeg = moonAppearance.getAzimuthDeg();
                    double moonElevationDeg = moonAppearance.calculateApparentElevationDeg();
                    long moonElevationArcMin = Math.round(moonElevationDeg * 60.0);
                    long moonElevationArcMinAbs = Math.abs(moonElevationArcMin);
                    String moonElevationSign = moonElevationDeg >= 0.0 ? "+" : "-";
                    if (Double.isNaN(moonAzimuthDeg)) {
                        ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_invisible_label)).setText(getString(R.string.main_activity_solar_info_today_culmination_direction_format, "", getString(R.string.format_unit_angle_dm_signed, moonElevationSign, moonElevationArcMinAbs / 60, moonElevationArcMinAbs % 60)));
                    } else {
                        ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_invisible_label)).setText(getString(R.string.main_activity_solar_info_today_culmination_direction_format, getString(degToStrResId(moonAzimuthDeg)), getString(R.string.format_unit_angle_dm_signed, moonElevationSign, moonElevationArcMinAbs / 60, moonElevationArcMinAbs % 60)));
                    }
                } else {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_invisible_label)).setText(R.string.main_activity_solar_info_today_invisible_culmination_label);
                }
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_invisible_label)).setText("");
            }

            Instant moonset = AstronomicalEventsCalculation.calculateSetWithin24h(moon, startOfTheDay, locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.CENTER);
            if (moonset == null && endOfTheDay.isAfter(startOfTheDay.plusSeconds(86400))) {
                moonset = AstronomicalEventsCalculation.calculateSetWithin24h(moon, endOfTheDay.minusSeconds(86400), locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.CENTER);
            }
            if (moonset != null && moonset.isAfter(endOfTheDay)) {
                moonset = null;
            }
            ((TextView) findViewById(R.id.main_view_solar_info_today_moonset_time)).setText(AppTimeFormat.instantToHmStringForEventTime(moonset, zoneId, timeFormat24Hour, locale));
            if (moonset != null) {
                double moonAzimuthDeg = HorizontalCoordinatesFromTheCenterOfTheEarth.ofAstronomicalObject(moon, moonset, locationOnTheEarth).getAzimuthDeg();
                if (Double.isNaN(moonAzimuthDeg)) {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moonset_direction)).setText("");
                } else {
                    long moonAzimuthArcMin = Math.round(moonAzimuthDeg * 60.0);
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moonset_direction)).setText(getString(R.string.main_activity_solar_info_today_azimuth_str_format, getString(R.string.format_unit_angle_dm, moonAzimuthArcMin / 60, moonAzimuthArcMin % 60), getString(degToStrResId(moonAzimuthDeg))));
                }
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moonset_direction)).setText("");
            }

            if (moonrise == null && moonset == null) {
                Instant nextEventTime = null;
                boolean nextEventIsRise = false;

                Instant t = endOfTheDay.minusSeconds(1);
                for (int i = 0; i < 270; ++i) {
                    Instant moonriseCandidate = AstronomicalEventsCalculation.calculateRiseWithin24h(moon, t, locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
                    Instant moonsetCandidate = AstronomicalEventsCalculation.calculateSetWithin24h(moon, t, locationOnTheEarth, true, AstronomicalEventsCalculation.ReferencePoint.TOP);

                    if (moonriseCandidate != null || moonsetCandidate != null) {
                        if (moonriseCandidate != null && moonsetCandidate != null) {
                            if (moonriseCandidate.isBefore(moonsetCandidate)) {
                                nextEventTime = moonriseCandidate;
                                nextEventIsRise = true;
                            } else {
                                nextEventTime = moonsetCandidate;
                                nextEventIsRise = false;
                            }
                        } else if (moonriseCandidate != null) {
                            nextEventTime = moonriseCandidate;
                            nextEventIsRise = true;
                        } else {
                            nextEventTime = moonsetCandidate;
                            nextEventIsRise = false;
                        }


                        break;
                    }

                    t = t.plusSeconds(86399);
                }

                if (nextEventTime != null) {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moon_next_event_title)).setText(nextEventIsRise ? R.string.main_activity_solar_info_today_next_event_moonrise : R.string.main_activity_solar_info_today_next_event_moonset);
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moon_next_event_time)).setText(AppTimeFormat.fullDateTimeForEventNatural(nextEventTime, zoneId, timeFormat24Hour, locale));

                    findViewById(R.id.main_view_solar_info_today_moon_next_event_title).setVisibility(View.VISIBLE);
                    findViewById(R.id.main_view_solar_info_today_moon_next_event_time).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.main_view_solar_info_today_moon_next_event_title).setVisibility(View.GONE);
                    findViewById(R.id.main_view_solar_info_today_moon_next_event_time).setVisibility(View.GONE);

                }
            } else {
                findViewById(R.id.main_view_solar_info_today_moon_next_event_title).setVisibility(View.GONE);
                findViewById(R.id.main_view_solar_info_today_moon_next_event_time).setVisibility(View.GONE);
            }
        } catch (AstronomicalPhenomenonComputationException | UnsupportedDateRangeException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayNowSolarInfo(Instant now, LocationOnTheEarth locationOnTheEarth, ZoneId zoneId) {
        TimePointOnTheEarth nowOnTheEarth = new TimePointOnTheEarth(now);

        Locale locale = getResources().getConfiguration().getLocales().get(0);
        boolean timeFormat24Hour = android.text.format.DateFormat.is24HourFormat(this.getApplicationContext());

        Sun sun = new Sun();
        Moon moon = new Moon();

        try {
            LocalTime localTime = now.atZone(zoneId).toLocalTime();
            ((TextView) findViewById(R.id.main_view_solar_info_now_localtime)).setText(AppTimeFormat.instantToHmsStringForRealtime(localTime, timeFormat24Hour, locale));

            LocalTime meanSolarTime = nowOnTheEarth.calculateMeanSolarTime(locationOnTheEarth).toLocalTime();
            ((TextView) findViewById(R.id.main_view_solar_info_now_mean_solar_time)).setText(AppTimeFormat.instantToHmsStringForRealtime(meanSolarTime, timeFormat24Hour, locale));

            LocalTime apparentSolarTime = new TimePointOnTheEarth(now).calculateApparentSolarTime(locationOnTheEarth).toLocalTime();
            ((TextView) findViewById(R.id.main_view_solar_info_now_apparent_solar_time)).setText(AppTimeFormat.instantToHmsStringForRealtime(apparentSolarTime, timeFormat24Hour, locale));

            double greenwichSideralTimeDeg = nowOnTheEarth.calculateSiderealTimeDeg(0.0);
            ((TextView) findViewById(R.id.main_view_solar_info_now_greenwich_sidereal_time)).setText(AppTimeFormat.degTo24HStr(greenwichSideralTimeDeg, locale));

            double localSideralTimeDeg = nowOnTheEarth.calculateSiderealTimeDeg(locationOnTheEarth.getLongitudeDeg());
            ((TextView) findViewById(R.id.main_view_solar_info_now_local_sidereal_time)).setText(AppTimeFormat.degTo24HStr(localSideralTimeDeg, locale));

            HorizontalCoordinatesFromGround sunAppearance = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(sun, now, locationOnTheEarth);
            double sunAzimuthRad = sunAppearance.getAzimuthRad();
            if (Double.isNaN(sunAzimuthRad)) {
                ((TextView) findViewById(R.id.main_view_solar_info_now_sun_azimuth)).setText(R.string.unit_angle_dm_invalid_3digits);
                ((TextView) findViewById(R.id.main_view_solar_info_now_sun_azimuth_str)).setText("");
            } else {
                long sunAzumithArcMin = (long) Math.floor(Math.toDegrees(sunAzimuthRad) * 60.0 + 0.5);
                ((TextView) findViewById(R.id.main_view_solar_info_now_sun_azimuth)).setText(getString(R.string.format_unit_angle_dm, sunAzumithArcMin / 60, sunAzumithArcMin % 60));
                ((TextView) findViewById(R.id.main_view_solar_info_now_sun_azimuth_str)).setText(getString(R.string.main_activity_solar_info_now_azimuth_str_format, getString(degToStrResId(Math.toDegrees(sunAzimuthRad)))));
            }

            if (sunAppearance.isTopAboveHorizon()) {
                double sunElevationRad = sunAppearance.calculateApparentElevationRad();
                long sunElevationArcMin = (long) Math.floor(Math.toDegrees(sunElevationRad) * 60.0 + 0.5);
                long sunElevationArcMinAbs = Math.abs(sunElevationArcMin);
                String sunElevationSign = (sunElevationRad >= 0.0) ? "+" : "-";
                ((TextView) findViewById(R.id.main_view_solar_info_now_sun_elevation)).setText(getString(R.string.format_unit_angle_dm_signed, sunElevationSign, sunElevationArcMinAbs / 60, sunElevationArcMinAbs % 60));
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_now_sun_elevation)).setText(R.string.main_activity_solar_info_now_invisible_culmination_label);
            }


            HorizontalCoordinatesFromGround moonAppearance = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(moon, now, locationOnTheEarth);
            double moonAzimuthRad = moonAppearance.getAzimuthRad();
            if (Double.isNaN(moonAzimuthRad)) {
                ((TextView) findViewById(R.id.main_view_solar_info_now_moon_azimuth)).setText(R.string.unit_angle_dm_invalid_3digits);
                ((TextView) findViewById(R.id.main_view_solar_info_now_moon_azimuth_str)).setText("");
            } else {
                long moonAzumithArcMin = (long) Math.floor(Math.toDegrees(moonAzimuthRad) * 60.0 + 0.5);
                ((TextView) findViewById(R.id.main_view_solar_info_now_moon_azimuth)).setText(getString(R.string.format_unit_angle_dm, moonAzumithArcMin / 60, moonAzumithArcMin % 60));
                ((TextView) findViewById(R.id.main_view_solar_info_now_moon_azimuth_str)).setText(getString(R.string.main_activity_solar_info_now_azimuth_str_format, getString(degToStrResId(Math.toDegrees(moonAzimuthRad)))));
            }
            if (moonAppearance.isTopAboveHorizon()) {
                double moonElevationRad = moonAppearance.calculateApparentElevationRad();
                long moonElevationArcMin = (long) Math.floor(Math.toDegrees(moonElevationRad) * 60.0 + 0.5);
                long moonElevationArcMinAbs = Math.abs(moonElevationArcMin);
                String moonElevationSign = (moonElevationRad >= 0.0) ? "+" : "-";
                ((TextView) findViewById(R.id.main_view_solar_info_now_moon_elevation)).setText(getString(R.string.format_unit_angle_dm_signed, moonElevationSign, moonElevationArcMinAbs / 60, moonElevationArcMinAbs % 60));
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_now_moon_elevation)).setText(R.string.main_activity_solar_info_now_invisible_culmination_label);
            }

        } catch (UnsupportedDateRangeException | AstronomicalPhenomenonComputationException e) {
            throw new RuntimeException(e);
        }

        this.previousLocationForRefresh = locationOnTheEarth;
        this.previousZoneIdForRefresh = zoneId;

        this.refreshHandler.postDelayed(this.refreshRunnable, 333);
    }
}