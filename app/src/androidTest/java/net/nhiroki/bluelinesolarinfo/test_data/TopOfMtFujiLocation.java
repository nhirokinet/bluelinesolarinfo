package net.nhiroki.bluelinesolarinfo.test_data;

import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;

public class TopOfMtFujiLocation extends LocationOnTheEarth {
    // Place based on https://ja.wikipedia.org/wiki/%E5%AF%8C%E5%A3%AB%E5%B1%B1
    public TopOfMtFujiLocation() {
        super(138.0 + 43.0/60.0 + 39.0 / 3600.0, 35.0 + 21.0 / 60.0 + 38.0 / 3600.0, 3776.0);
    }
}
