package net.nhiroki.bluelinesolarinfo.region;

import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;

import java.time.ZoneId;

public class RegionOnTheEarth {
    private long id;
    private String name;
    private ZoneId zoneId;
    private LocationOnTheEarth locationOnTheEarth;

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
