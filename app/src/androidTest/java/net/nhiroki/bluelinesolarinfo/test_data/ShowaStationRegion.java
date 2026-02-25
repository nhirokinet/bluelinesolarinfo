package net.nhiroki.bluelinesolarinfo.test_data;

import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;

import java.time.ZoneId;

public class ShowaStationRegion extends RegionOnTheEarth {
    public ShowaStationRegion() {
        super(0, "Showa Station", ZoneId.of("UTC"), new ShowaStationLocation());
    }
}
