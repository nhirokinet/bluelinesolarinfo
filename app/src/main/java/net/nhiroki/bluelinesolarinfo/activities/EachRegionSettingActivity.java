package net.nhiroki.bluelinesolarinfo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.nhiroki.bluelinesolarinfo.R;
import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.DataStore;
import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Set;

public class EachRegionSettingActivity extends AppCompatActivity {
    public static final String EXTRA_REGION_ID = "region_id";

    private boolean isNewRegion = false;
    private long regionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_each_region_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.each_region_setting_delete_region_button).setOnClickListener(view -> {
            DataStore.getInstance(EachRegionSettingActivity.this.getApplicationContext()).removeRegionById(regionId);
            EachRegionSettingActivity.this.finish();

        });

        findViewById(R.id.each_region_setting_save_region_button).setOnClickListener(view -> {
            RegionOnTheEarth region = new RegionOnTheEarth(
                    regionId,
                    ((TextView)findViewById(R.id.each_region_setting_region_name_edit_text)).getText().toString(),
                    ZoneId.of(((TextView)findViewById(R.id.each_region_setting_timezone_text_view)).getText().toString()),
                    new LocationOnTheEarth(
                            Double.parseDouble(((TextView)findViewById(R.id.each_region_setting_longitude_edit_text)).getText().toString()),
                            Double.parseDouble(((TextView)findViewById(R.id.each_region_setting_latitude_edit_text)).getText().toString()),
                            Double.parseDouble(((TextView)findViewById(R.id.each_region_setting_elevation_edit_text)).getText().toString())
                    )
            );
            if (isNewRegion) {
                DataStore.getInstance(EachRegionSettingActivity.this.getApplicationContext()).createRegion(region);
            } else {
                DataStore.getInstance(EachRegionSettingActivity.this.getApplicationContext()).updateRegion(region);
            }
            EachRegionSettingActivity.this.finish();
        });

        findViewById(R.id.each_region_setting_change_timezone_button).setOnClickListener(view -> {
            Set<String> zoneIdSet = ZoneId.getAvailableZoneIds();
            String[] zoneIdsTmp = new String[zoneIdSet.size()];
            int zoneCountTmp = 0;
            for (String z: zoneIdSet) {
                zoneIdsTmp[zoneCountTmp++] = z;
            }
            Arrays.sort(zoneIdsTmp);

            String[] zoneIds = new String[zoneIdSet.size() + 2];
            zoneIds[0] = ZoneId.systemDefault().getId();
            zoneIds[1] = "UTC";
            int zoneCount = 2;
            for (String z: zoneIdsTmp) {
                zoneIds[zoneCount++] = z;
            }

            String[] items = new String[zoneIds.length];
            String[] tzList = new String[zoneIds.length];

            for (int i = 0; i < zoneIds.length; ++i) {
                ZoneId zoneId = ZoneId.of(zoneIds[i]);
                items[i] = zoneId.getId() + ": " + zoneId.getDisplayName(TextStyle.FULL_STANDALONE, getResources().getConfiguration().getLocales().get(0));
                tzList[i] = zoneId.getId();
            }

            DialogInterface.OnClickListener itemListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((TextView)EachRegionSettingActivity.this.findViewById(R.id.each_region_setting_timezone_text_view)).setText(tzList[which]);
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(EachRegionSettingActivity.this);
            builder.setTitle(getString(R.string.each_region_activity_dialog_timezone_select_title));
            builder.setItems(items, itemListener);
            builder.create().show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent fromIntent = this.getIntent();
        long regionId = fromIntent.getLongExtra(EXTRA_REGION_ID, -1);
        if (regionId != -1) {
            this.isNewRegion = false;
            this.regionId = regionId;

            findViewById(R.id.each_region_setting_delete_region_button).setVisibility(View.VISIBLE);

            RegionOnTheEarth region = DataStore.getInstance(getApplicationContext()).getRegionById(regionId);
            ((TextView)findViewById(R.id.each_region_setting_region_name_edit_text)).setText(region.getName());
            LocationOnTheEarth locationOnTheEarth = region.getLocationOnTheEarth();
            ((TextView)findViewById(R.id.each_region_setting_longitude_edit_text)).setText(Double.toString(locationOnTheEarth.getLongitudeDeg()));
            ((TextView)findViewById(R.id.each_region_setting_latitude_edit_text)).setText(Double.toString(locationOnTheEarth.getLatitudeDeg()));
            ((TextView)findViewById(R.id.each_region_setting_elevation_edit_text)).setText(Double.toString(locationOnTheEarth.getElevationMeters()));
            ((TextView)findViewById(R.id.each_region_setting_timezone_text_view)).setText(region.getZoneId().toString());

        } else {
            this.isNewRegion = true;
            this.regionId = 0;

            findViewById(R.id.each_region_setting_delete_region_button).setVisibility(View.GONE);

            ((TextView)findViewById(R.id.each_region_setting_region_name_edit_text)).setText("");
            ((TextView)findViewById(R.id.each_region_setting_longitude_edit_text)).setText("");
            ((TextView)findViewById(R.id.each_region_setting_latitude_edit_text)).setText("");
            ((TextView)findViewById(R.id.each_region_setting_elevation_edit_text)).setText("");
            ((TextView)findViewById(R.id.each_region_setting_timezone_text_view)).setText(ZoneId.systemDefault().toString());
        }
    }
}
