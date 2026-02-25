package net.nhiroki.bluelinesolarinfo.test_data;

import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;

public class TopOfMtFujiRegion extends RegionOnTheEarth {
    public TopOfMtFujiRegion() {
        super(0, "Top of Mt. Fuji", java.time.ZoneId.of("Asia/Tokyo"), new TopOfMtFujiLocation());
    }
}
