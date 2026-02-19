package net.nhiroki.bluelinesolarinfo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;

import java.util.List;

public class RegionsSettingActivity extends AppCompatActivity {
    private class RegionArrayAdapter extends android.widget.ArrayAdapter<RegionOnTheEarth> {
        public RegionArrayAdapter() {
            super(RegionsSettingActivity.this, R.layout.listviewitem_regions_setting_region);
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            RegionOnTheEarth item = getItem(position);

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listviewitem_regions_setting_region, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.listviewitem_region_name)).setText(item.getName());
            ((TextView)convertView.findViewById(R.id.listviewitem_region_timezone)).setText(
                    getString(R.string.regions_setting_timezone_label, item.getZoneId().toString())
            );
            long longitudeAbs = (long) Math.floor(Math.abs(item.getLocationOnTheEarth().getLongitudeDeg()) * 36000.0);
            long latitudeAbs = (long) Math.floor(Math.abs(item.getLocationOnTheEarth().getLatitudeDeg()) * 36000.0);
            ((TextView)convertView.findViewById(R.id.listviewitem_region_coordinates)).setText(
                    getString(R.string.main_activity_location_coordinates_format,
                            item.getLocationOnTheEarth().getLongitudeDeg() >= 0.0 ? getString(R.string.coordinate_display_east) : getString(R.string.coordinate_display_west),
                            getString(R.string.unit_angle_dms, longitudeAbs / 36000, (longitudeAbs % 36000) / 600, (longitudeAbs % 600) / 10, longitudeAbs % 10),
                            item.getLocationOnTheEarth().getLatitudeDeg() >= 0.0 ? getString(R.string.coordinate_display_north) : getString(R.string.coordinate_display_south),
                            getString(R.string.unit_angle_dms, latitudeAbs / 36000, (latitudeAbs % 36000) / 600, (latitudeAbs % 600) / 10, latitudeAbs % 10)
                    )
            );
            ((TextView)convertView.findViewById(R.id.listviewitem_region_elevation)).setText(
                    getString(R.string.main_activity_location_elevation_meters, item.getLocationOnTheEarth().getElevationMeters())
            );
            return convertView;
        }
    }

    private RegionArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regions_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.listAdapter = new RegionArrayAdapter();
        ((android.widget.ListView) findViewById(R.id.regions_setting_region_list_view)).setAdapter(listAdapter);

        findViewById(R.id.regions_setting_add_region_button).setOnClickListener(view -> {
            startActivity(new android.content.Intent(this, EachRegionSettingActivity.class));
        });

        findViewById(R.id.regions_setting_change_default_region_button).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.regions_setting_dialog_default_region_select_title);

            List<RegionOnTheEarth> regionOnTheEarthList = DataStore.getInstance(RegionsSettingActivity.this.getApplicationContext()).getRegions();
            CharSequence[] items = new CharSequence[regionOnTheEarthList.size() + 1];
            items[0] = getString(R.string.main_activity_current_location);
            for (int i = 0; i < regionOnTheEarthList.size(); i++) {
                items[i + 1] = regionOnTheEarthList.get(i).getName();
            }

            builder.setItems(items, (dialogInterface, i) -> {
                    if (i == 0) {
                        DataStore.getInstance(RegionsSettingActivity.this.getApplicationContext()).setDefaultRegion(null);
                    } else {
                        DataStore.getInstance(RegionsSettingActivity.this.getApplicationContext()).setDefaultRegion(regionOnTheEarthList.get(i - 1));
                    }
                    RegionsSettingActivity.this.refreshDisplay();
            });
            builder.create().show();
        });

        ((ListView) findViewById(R.id.regions_setting_region_list_view)).setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, EachRegionSettingActivity.class);
            intent.putExtra(EachRegionSettingActivity.EXTRA_REGION_ID, RegionsSettingActivity.this.listAdapter.getItem(position).getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.refreshDisplay();
    }

    private void refreshDisplay() {
        List<RegionOnTheEarth> regions = DataStore.getInstance(getApplicationContext()).getRegions();

        RegionOnTheEarth defaultRegion = DataStore.getInstance(getApplicationContext()).getDefaultRegion();
        CharSequence defaultRegionName = (defaultRegion == null) ? getString(R.string.main_activity_current_location) : defaultRegion.getName();

        ((TextView) findViewById(R.id.regions_setting_default_region_text)).setText(getString(R.string.regions_setting_default_region_label, defaultRegionName));
        listAdapter.clear();
        listAdapter.addAll(regions);
    }
}
