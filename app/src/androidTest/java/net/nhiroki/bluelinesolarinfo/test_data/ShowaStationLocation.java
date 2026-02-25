package net.nhiroki.bluelinesolarinfo.test_data;

import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;

public class ShowaStationLocation extends LocationOnTheEarth {
    public ShowaStationLocation() {
        // https://ja.wikipedia.org/wiki/%E6%98%AD%E5%92%8C%E5%9F%BA%E5%9C%B0
        super(39.0 + 35.0 / 60.0 + 1.48 / 3600.0, -(69.0 + 25.05 / 3600.0), 28.8);
    }
}
