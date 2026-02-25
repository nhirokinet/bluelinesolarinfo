package net.nhiroki.bluelinesolarinfo.storage;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;
import net.nhiroki.bluelinesolarinfo.storage.oldVersions.DataStore_0_1_0;
import net.nhiroki.bluelinesolarinfo.test_data.ShowaStationRegion;
import net.nhiroki.bluelinesolarinfo.test_data.TokyoNAORegion;
import net.nhiroki.bluelinesolarinfo.test_data.TopOfMtFujiRegion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class DataStoreTest {
    @Before
    public void setUp() {
        ApplicationProvider.getApplicationContext().deleteDatabase("app-data.sqlite");
        DataStore.discardInstance();
    }

    @Test
    public void basicFunctionalityTest() {
        DataStore dataStore = DataStore.getInstance(ApplicationProvider.getApplicationContext());
        long tokyoNAOID = dataStore.createRegion(new TokyoNAORegion());
        assertEquals(1, dataStore.getRegions().size());
        assertEquals(new TokyoNAORegion().getName(), dataStore.getRegions().get(0).getName());
        assertEquals(new TokyoNAORegion().getZoneId(), dataStore.getRegions().get(0).getZoneId());
        assertEquals(new TokyoNAORegion().getLocationOnTheEarth(), dataStore.getRegions().get(0).getLocationOnTheEarth());
        assertEquals(1, tokyoNAOID);
        dataStore.close();
    }

    @Test
    public void upgradeFrom_0_1_0_Test() {
        DataStore_0_1_0.discardInstance();
        DataStore_0_1_0 oldDataStore = DataStore_0_1_0.getInstance(ApplicationProvider.getApplicationContext());
        long tokyoNAOID = oldDataStore.createRegion(new TokyoNAORegion());
        assertEquals(1, tokyoNAOID);
        long showaStationID = oldDataStore.createRegion(new ShowaStationRegion());
        assertEquals(2, showaStationID);
        long topOfMtFujiID = oldDataStore.createRegion(new TopOfMtFujiRegion());
        assertEquals(3, topOfMtFujiID);
        RegionOnTheEarth showaStationSaved = oldDataStore.getRegionById(showaStationID);
        oldDataStore.setDefaultRegion(showaStationSaved);
        oldDataStore.close();

        DataStore newDataStore = DataStore.getInstance(ApplicationProvider.getApplicationContext());
        assertEquals(3, newDataStore.getRegions().size());

        assertEquals(new TokyoNAORegion().getName(), newDataStore.getRegions().get(0).getName());
        assertEquals(new TokyoNAORegion().getZoneId(), newDataStore.getRegions().get(0).getZoneId());
        assertEquals(new TokyoNAORegion().getLocationOnTheEarth(), newDataStore.getRegions().get(0).getLocationOnTheEarth());

        RegionOnTheEarth defaultRegion = newDataStore.getDefaultRegion();
        assertEquals(showaStationID, defaultRegion.getId());
        assertEquals(new ShowaStationRegion().getName(), defaultRegion.getName());
        assertEquals(new ShowaStationRegion().getZoneId(), defaultRegion.getZoneId());
        assertEquals(new ShowaStationRegion().getLocationOnTheEarth(), defaultRegion.getLocationOnTheEarth());

        assertEquals(new TopOfMtFujiRegion().getName(), newDataStore.getRegions().get(2).getName());
        assertEquals(new TopOfMtFujiRegion().getZoneId(), newDataStore.getRegions().get(2).getZoneId());
        assertEquals(new TopOfMtFujiRegion().getLocationOnTheEarth(), newDataStore.getRegions().get(2).getLocationOnTheEarth());
        newDataStore.close();
    }
}
