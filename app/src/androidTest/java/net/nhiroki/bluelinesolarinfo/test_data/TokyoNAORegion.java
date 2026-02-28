package net.nhiroki.bluelinesolarinfo.test_data;

import net.nhiroki.bluelinesolarinfo.region.RegionOnTheEarth;

import java.time.ZoneId;

public class TokyoNAORegion extends RegionOnTheEarth {
    public TokyoNAORegion() {
        super(0, "東京", ZoneId.of("Asia/Tokyo"), new TokyoNAOLocation());
    }
}
