package net.nhiroki.bluelinesolarinfo.region;

import net.nhiroki.lib.bluelineastrolib.coordinates.LocationOnTheEarth;

import java.time.ZoneId;

public class RegionOnTheEarth {
    private final long id;
    private final String name;
    private final ZoneId zoneId;
    private final LocationOnTheEarth locationOnTheEarth;

    public RegionOnTheEarth(long id, String name, ZoneId zoneId, LocationOnTheEarth locationOnTheEarth) {
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
        this.locationOnTheEarth = locationOnTheEarth;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public LocationOnTheEarth getLocationOnTheEarth() {
        return locationOnTheEarth;
    }
}
