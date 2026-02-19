package net.nhiroki.bluelinesolarinfo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;

import java.util.ArrayList;
import java.util.List;

public class DataStore extends SQLiteOpenHelper {
    private static final String DATABASE_FILENAME = "app-data.sqlite";
    private static final int DATABASE_VERSION = 1;

    private static final String PROP_KEY_DEFAULT_REGION_ID = "default_region_id";
    private static DataStore singleton;

    private DataStore(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    public static DataStore getInstance(Context context) {
        if (singleton == null) {
            singleton = new DataStore(context);
        }
        return singleton;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS regions (" +
                               "    id INTEGER PRIMARY KEY," +
                               "    name TEXT NOT NULL, " +
                               "    timezone TEXT NOT NULL, " +
                               "    longitude REAL NOT NULL, " +
                               "    latitude REAL NOT NULL , " +
                               "    elevation REAL NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS props (" +
                               "    name TEXT PRIMARY KEY," +
                               "    value TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Nothing to do now because database structure have not been modified
    }

    public RegionOnTheEarth getRegionById(long id) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT id, name, timezone, longitude, latitude, elevation FROM regions WHERE id = ?", new String[]{Long.toString(id)});
        if (cursor.moveToNext()) {
            long regionId = cursor.getLong(0);
            String name = cursor.getString(1);
            java.time.ZoneId zoneId = java.time.ZoneId.of(cursor.getString(2));
            double longitude = cursor.getDouble(3);
            double latitude = cursor.getDouble(4);
            double elevation = cursor.getDouble(5);

            cursor.close();
            return new RegionOnTheEarth(regionId, name, zoneId, new net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth(longitude, latitude, elevation));
        } else {
            cursor.close();
            return null;
        }
    }

    public String getProp(String name) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT value FROM props WHERE name = ?", new String[]{name});
        if (cursor.moveToNext()) {
            String value = cursor.getString(0);
            cursor.close();
            return value;
        } else {
            cursor.close();
            return null;
        }
    }

    public void setProp(String name, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("value", value);
        getWritableDatabase().insertWithOnConflict("props", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeProp(String name) {
        getWritableDatabase().delete("props", "name = ?", new String[]{name});
    }

    public List<RegionOnTheEarth> getRegions() {
        ArrayList<RegionOnTheEarth> ret = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery("SELECT id, name, timezone, longitude, latitude, elevation FROM regions", null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            java.time.ZoneId zoneId = java.time.ZoneId.of(cursor.getString(2));
            double longitude = cursor.getDouble(3);
            double latitude = cursor.getDouble(4);
            double elevation = cursor.getDouble(5);

            ret.add(new RegionOnTheEarth(id, name, zoneId, new net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth(longitude, latitude, elevation)));
        }
        cursor.close();
        return ret;
    }

    public @Nullable RegionOnTheEarth getDefaultRegion() {
        String defaultRegionIdStr = getProp(PROP_KEY_DEFAULT_REGION_ID);
        if (defaultRegionIdStr == null) {
            return null;
        }
        long defaultRegionId = Long.parseLong(defaultRegionIdStr);
        return getRegionById(defaultRegionId);
    }

    public void setDefaultRegion(@Nullable RegionOnTheEarth region) {
        if (region == null) {
            removeProp(PROP_KEY_DEFAULT_REGION_ID);
        } else {
            setProp(PROP_KEY_DEFAULT_REGION_ID, Long.toString(region.getId()));
        }
    }

    public void createRegion(RegionOnTheEarth region) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", region.getName());
        contentValues.put("timezone", region.getZoneId().toString());
        contentValues.put("longitude", region.getLocationOnTheEarth().getLongitudeDeg());
        contentValues.put("latitude", region.getLocationOnTheEarth().getLatitudeDeg());
        contentValues.put("elevation", region.getLocationOnTheEarth().getElevationMeters());
        getWritableDatabase().insert("regions", null, contentValues);
    }

    public void updateRegion(RegionOnTheEarth region) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", region.getName());
        contentValues.put("timezone", region.getZoneId().toString());
        contentValues.put("longitude", region.getLocationOnTheEarth().getLongitudeDeg());
        contentValues.put("latitude", region.getLocationOnTheEarth().getLatitudeDeg());
        contentValues.put("elevation", region.getLocationOnTheEarth().getElevationMeters());
        getWritableDatabase().update("regions", contentValues, "id = ?", new String[]{Long.toString(region.getId())});
    }

    public void removeRegionById(long id) {
        getWritableDatabase().delete("regions", "id = ?", new String[]{Long.toString(id)});
    }
}
