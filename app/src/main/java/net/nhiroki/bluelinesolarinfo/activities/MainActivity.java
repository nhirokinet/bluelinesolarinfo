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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.AppPreferences;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Moon;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;
import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.logic.AstronomicalObjectCalculator;
import net.nhiroki.lib.bluelineastrolib.logic.CoordinateConversion;
import net.nhiroki.lib.bluelineastrolib.tool.MoonTool;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

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
    private String locationPrevProvider = "";
    private double locationPrevAccuracy = 1000000.0;

    private Handler refreshHandler;
    private Runnable refreshRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        this.refreshHandler = new Handler(Looper.getMainLooper());
        this.refreshRunnable = () -> MainActivity.this.periodicRefresh();

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
                if (MainActivity.this.currentLocation != null) {
                    if (locationPrevProvider.equals(LocationManager.FUSED_PROVIDER) && !location.getProvider().equals(LocationManager.FUSED_PROVIDER)) {
                        return;
                    }
                    if ((!locationPrevProvider.equals(location.getProvider())) && locationPrevAccuracy < location.getAccuracy()) {
                        return;
                    }
                }
                double height;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    height = location.hasAltitude() ? (location.hasMslAltitude() ? location.getMslAltitudeMeters() : location.getAltitude()) : 0.0;
                } else {
                    height = location.hasAltitude() ? location.getAltitude() : 0.0;
                }
                MainActivity.this.currentLocation = new LocationOnTheEarth(location.getLongitude(), location.getLatitude(), height);
                MainActivity.this.locationMeasureStatus = LocationMeasureStatus.SUCCESS;
                MainActivity.this.locationPrevProvider = location.getProvider();
                MainActivity.this.locationPrevAccuracy = location.getAccuracy();
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
                        this.locationManager.requestLocationUpdates(provider, 3000l, 1.0f, this.locationListener);
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

            if (this.locationListenerInUse && this.locationManager != null) {
                this.locationManager.removeUpdates(this.locationListener);
                this.locationListenerInUse = false;
            }
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

        if (locationOnTheEarth == null) {
            findViewById(R.id.main_view_solar_info_today_area).setVisibility(View.GONE);
            findViewById(R.id.main_view_solar_info_now_area).setVisibility(View.GONE);
            return;
        }

        findViewById(R.id.main_view_solar_info_today_area).setVisibility(View.VISIBLE);

        Instant todayStart = date.atStartOfDay(zoneId).toInstant();
        Instant midOfTheDay = date.atTime(12, 0).atZone(zoneId).toInstant();
        Instant todayEnd = date.plusDays(1).atStartOfDay(zoneId).toInstant();
        this.displayTodaySolarInfo(todayStart, midOfTheDay, todayEnd, locationOnTheEarth, zoneId);

        if (date.equals(today)) {
            this.displayNowSolarInfo(now, locationOnTheEarth, zoneId);
            findViewById(R.id.main_view_solar_info_now_area).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.main_view_solar_info_now_area).setVisibility(View.GONE);
            this.refreshHandler.removeCallbacks(this.refreshRunnable);
        }
    }

    // Uses @midOfTheDay to calculate moon phase. Required because it can be 11 hours or 13 hours from @todayStart due to DST
    // @endOfTheDay is assumed to be 23-25 hours from @todayStart
    private void displayTodaySolarInfo(Instant todayStart, Instant midOfTheDay, Instant todayEnd, LocationOnTheEarth locationOnTheEarth, ZoneId zoneId) {
        Sun sun = new Sun();
        Moon moon = new Moon();

        Locale locale = getResources().getConfiguration().getLocales().get(0);
        boolean timeFormat24Hour = android.text.format.DateFormat.is24HourFormat(this.getApplicationContext());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, timeFormat24Hour ? "Hm" : "hma"), locale);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, timeFormat24Hour ? "yMdHm" : "yMdhma"), locale);

        try {
            Instant sunrise = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, todayStart, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
            if (sunrise == null && todayEnd.isAfter(todayStart.plusSeconds(86400))) {
                sunrise = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, todayEnd.minusSeconds(86400), locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
            }
            if (sunrise != null && sunrise.isAfter(todayEnd)) {
                sunrise = null;
            }
            if (sunrise != null) {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sunrise_time)).setText(sunrise.atZone(zoneId).format(timeFormatter));
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sunrise_time)).setText(R.string.main_activity_solar_info_today_time_placeholder_event_not_occur);
            }

            Instant sunculmination = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, todayStart, locationOnTheEarth);
            if (sunculmination == null && todayEnd.isAfter(todayStart.plusSeconds(86400))) {
                sunculmination = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, todayEnd.minusSeconds(86400), locationOnTheEarth);
            }
            if (sunculmination != null && sunculmination.isAfter(todayEnd)) {
                sunculmination = null;
            }
            if (sunculmination != null) {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_time)).setText(sunculmination.atZone(zoneId).format(timeFormatter));

                if (AstronomicalObjectCalculator.isObjectAboveHorizon(sun, sunculmination, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP)) {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_invisible_label)).setText("");
                } else {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_invisible_label)).setText(R.string.main_activity_solar_info_today_invisible_culmination_label);
                }
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_time)).setText(R.string.main_activity_solar_info_today_time_placeholder_event_not_occur);
                ((TextView) findViewById(R.id.main_view_solar_info_today_sun_culmination_invisible_label)).setText("");
            }

            Instant sunset = AstronomicalObjectCalculator.calculateSetWithin24h(sun, todayStart, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
            if (sunset == null && todayEnd.isAfter(todayStart.plusSeconds(86400))) {
                sunset = AstronomicalObjectCalculator.calculateSetWithin24h(sun, todayEnd.minusSeconds(86400), locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
            }
            if (sunset != null && sunset.isAfter(todayEnd)) {
                sunset = null;
            }
            if (sunset != null) {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sunset_time)).setText(sunset.atZone(zoneId).format(timeFormatter));
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_sunset_time)).setText(R.string.main_activity_solar_info_today_time_placeholder_event_not_occur);
            }

            if (sunrise == null && sunset == null) {
                Instant nextEventTime = null;
                boolean nextEventIsRise = false;

                Instant t = todayEnd.minusSeconds(1);
                for (int i = 0; i < 270; ++i) {
                    Instant sunriseCandidate = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, t, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
                    Instant sunsetCandidate = AstronomicalObjectCalculator.calculateSetWithin24h(sun, t, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);

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
                    ((TextView) findViewById(R.id.main_view_solar_info_today_sun_next_event_time)).setText(nextEventTime.atZone(zoneId).format(dateTimeFormatter));

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

            double moonPhaseDeg = MoonTool.calculateMoonPhaseDeg(midOfTheDay);
            ((net.nhiroki.androidlib.bluelineastroandroidlib.views.MoonPhaseView) findViewById(R.id.main_view_solar_info_today_moon_phase_view)).setMoonPhaseDeg(moonPhaseDeg);

            Instant prevNewMoon = MoonTool.calculatePreviousTimeOfMoonPhaseByDeg(midOfTheDay, 0.0);
            double daysAfterPrevNewMoon = ((double)(midOfTheDay.toEpochMilli() - prevNewMoon.toEpochMilli())) / 86400000.0;
            ((TextView) findViewById(R.id.main_view_solar_info_today_moon_phase_days_text)).setText(String.format("%.1f", daysAfterPrevNewMoon));

            Instant moonrise = AstronomicalObjectCalculator.calculateRiseWithin24h(moon, todayStart, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.CENTER);
            if (moonrise == null && todayEnd.isAfter(todayStart.plusSeconds(86400))) {
                moonrise = AstronomicalObjectCalculator.calculateRiseWithin24h(moon, todayEnd.minusSeconds(86400), locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.CENTER);
            }
            if (moonrise != null && moonrise.isAfter(todayEnd)) {
                moonrise = null;
            }
            if (moonrise != null) {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moonrise_time)).setText(moonrise.atZone(zoneId).format(timeFormatter));
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moonrise_time)).setText(R.string.main_activity_solar_info_today_time_placeholder_event_not_occur);
            }

            Instant moonculmination = AstronomicalObjectCalculator.calculateCulminationWithin24h(moon, todayStart, locationOnTheEarth);
            if (moonculmination == null && todayEnd.isAfter(todayStart.plusSeconds(86400))) {
                moonculmination = AstronomicalObjectCalculator.calculateCulminationWithin24h(moon, todayEnd.minusSeconds(86400), locationOnTheEarth);
            }
            if (moonculmination != null && moonculmination.isAfter(todayEnd)) {
                moonculmination = null;
            }
            if (moonculmination != null) {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_time)).setText(moonculmination.atZone(zoneId).format(timeFormatter));

                if (AstronomicalObjectCalculator.isObjectAboveHorizon(moon, moonculmination, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.CENTER)) {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_invisible_label)).setText("");
                } else {
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_invisible_label)).setText(R.string.main_activity_solar_info_today_invisible_culmination_label);
                }
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_time)).setText(R.string.main_activity_solar_info_today_time_placeholder_event_not_occur);
                ((TextView) findViewById(R.id.main_view_solar_info_today_moon_culmination_invisible_label)).setText("");
            }

            Instant moonset = AstronomicalObjectCalculator.calculateSetWithin24h(moon, todayStart, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.CENTER);
            if (moonset == null && todayEnd.isAfter(todayStart.plusSeconds(86400))) {
                moonset = AstronomicalObjectCalculator.calculateSetWithin24h(moon, todayEnd.minusSeconds(86400), locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.CENTER);
            }
            if (moonset != null && moonset.isAfter(todayEnd)) {
                moonset = null;
            }
            if (moonset != null) {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moonset_time)).setText(moonset.atZone(zoneId).format(timeFormatter));
            } else {
                ((TextView) findViewById(R.id.main_view_solar_info_today_moonset_time)).setText(R.string.main_activity_solar_info_today_time_placeholder_event_not_occur);
            }

            if (moonrise == null && moonset == null) {
                Instant nextEventTime = null;
                boolean nextEventIsRise = false;

                Instant t = todayEnd.minusSeconds(1);
                for (int i = 0; i < 270; ++i) {
                    Instant moonriseCandidate = AstronomicalObjectCalculator.calculateRiseWithin24h(moon, t, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
                    Instant moonsetCandidate = AstronomicalObjectCalculator.calculateSetWithin24h(moon, t, locationOnTheEarth, true, AstronomicalObjectCalculator.ReferencePoint.TOP);

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
                    ((TextView) findViewById(R.id.main_view_solar_info_today_moon_next_event_time)).setText(nextEventTime.atZone(zoneId).format(dateTimeFormatter));

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
        } catch (AstronomicalPhenomenonComputationException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedDateRangeException e) {
            throw new RuntimeException(e);
        }

    }

    private static String degTo24HStr(double deg) {
        int sec = (int)(deg / 360.0 * 86400.0);
        return String.format("%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60);
    };

    private void displayNowSolarInfo(Instant now, LocationOnTheEarth locationOnTheEarth, ZoneId zoneId) {
        TimePointOnTheEarth nowOnTheEarth = new TimePointOnTheEarth(now);

        Locale locale = getResources().getConfiguration().getLocales().get(0);
        boolean timeFormat24Hour = android.text.format.DateFormat.is24HourFormat(this.getApplicationContext());
        DateTimeFormatter timeFormatterWithSec = DateTimeFormatter.ofPattern(android.text.format.DateFormat.getBestDateTimePattern(locale, timeFormat24Hour ? "Hms" : "hmsa"), locale);

        Sun sun = new Sun();
        Moon moon = new Moon();

        try {
            ((TextView) findViewById(R.id.main_view_solar_info_now_greenwich_localtime)).setText(now.atZone(zoneId).format(timeFormatterWithSec));

            double greenwichSideralTimeDeg = nowOnTheEarth.calculateSiderealTimeDeg(0.0);
            ((TextView) findViewById(R.id.main_view_solar_info_now_greenwich_sidereal_time)).setText(degTo24HStr(greenwichSideralTimeDeg));

            double localSideralTimeDeg = nowOnTheEarth.calculateSiderealTimeDeg(locationOnTheEarth.getLongitudeDeg());
            ((TextView) findViewById(R.id.main_view_solar_info_now_local_sidereal_time)).setText(degTo24HStr(localSideralTimeDeg));

            double sunHourAngle = nowOnTheEarth.calculateSiderealTimeRad(locationOnTheEarth.getLongitudeRad()) - sun.calculateRightAscensionRad(now);
            double sunDeclination = sun.calculateDeclinationRad(now);
            double sunAzimuthRad = CoordinateConversion.calculateAzimuthRadFromHourAngle(sunHourAngle, sunDeclination, locationOnTheEarth.getLatitudeRad());
            if (Double.isNaN(sunAzimuthRad)) {
                ((TextView) findViewById(R.id.main_view_solar_info_now_sun_azimuth)).setText(R.string.unit_angle_dm_invalid_3digits);
            } else {
                long sunAzumithArcMin = (long) Math.floor(Math.toDegrees(sunAzimuthRad) * 60.0 + 0.5);
                ((TextView) findViewById(R.id.main_view_solar_info_now_sun_azimuth)).setText(getString(R.string.format_unit_angle_dm_3digits, sunAzumithArcMin / 60, sunAzumithArcMin % 60));
            }
            double sunElevationRad = CoordinateConversion.calculateElevationRadFromHourAngle(sunHourAngle, sunDeclination, locationOnTheEarth.getLatitudeRad());
            long sunElevationArcMin = (long) Math.floor(Math.toDegrees(sunElevationRad) * 60.0 + 0.5);
            long sunElevationArcMinAbs = Math.abs(sunElevationArcMin);
            String sunElevationSign = (sunElevationRad >= 0.0) ? "+" : "-";
            ((TextView) findViewById(R.id.main_view_solar_info_now_sun_elevation)).setText(getString(R.string.format_unit_angle_dm_signed, sunElevationSign, sunElevationArcMinAbs / 60, sunElevationArcMinAbs % 60));


            double moonHourAngle = nowOnTheEarth.calculateSiderealTimeRad(locationOnTheEarth.getLongitudeRad()) - moon.calculateRightAscensionRad(now);
            double moonDeclination = moon.calculateDeclinationRad(now);
            double moonAzimuthRad = CoordinateConversion.calculateAzimuthRadFromHourAngle(moonHourAngle, moonDeclination, locationOnTheEarth.getLatitudeRad());
            if (Double.isNaN(moonAzimuthRad)) {
                ((TextView) findViewById(R.id.main_view_solar_info_now_moon_azimuth)).setText(R.string.unit_angle_dm_invalid_3digits);
            } else {
                long moonAzumithArcMin = (long) Math.floor(Math.toDegrees(moonAzimuthRad) * 60.0 + 0.5);
                ((TextView) findViewById(R.id.main_view_solar_info_now_moon_azimuth)).setText(getString(R.string.format_unit_angle_dm_3digits, moonAzumithArcMin / 60, moonAzumithArcMin % 60));
            }
            double moonElevationRad = CoordinateConversion.calculateElevationRadFromHourAngle(moonHourAngle, moonDeclination, locationOnTheEarth.getLatitudeRad());
            long moonElevationArcMin = (long) Math.floor(Math.toDegrees(moonElevationRad) * 60.0 + 0.5);
            long moonElevationArcMinAbs = Math.abs(moonElevationArcMin);
            String moonElevationSign = (moonElevationRad >= 0.0) ? "+" : "-";
            ((TextView) findViewById(R.id.main_view_solar_info_now_moon_elevation)).setText(getString(R.string.format_unit_angle_dm_signed, moonElevationSign, moonElevationArcMinAbs / 60, moonElevationArcMinAbs % 60));

        } catch (UnsupportedDateRangeException e) {
            throw new RuntimeException(e);
        } catch (AstronomicalPhenomenonComputationException e) {
            throw new RuntimeException(e);
        }

        this.previousLocationForRefresh = locationOnTheEarth;
        this.previousZoneIdForRefresh = zoneId;

        this.refreshHandler.postDelayed(this.refreshRunnable, 333);
    }
}