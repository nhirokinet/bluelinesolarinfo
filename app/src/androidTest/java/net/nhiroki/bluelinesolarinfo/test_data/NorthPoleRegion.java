package net.nhiroki.bluelinesolarinfo.test_data;

import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;

import java.time.ZoneId;

public class NorthPoleRegion extends RegionOnTheEarth {
    public NorthPoleRegion() {
        super(0, "North Pole", ZoneId.of("UTC"), LocationsForTest.getNorthPoleE0Z());
    }
}
