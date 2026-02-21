package net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest;

import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;


public class RioDeJaneiro extends LocationOnTheEarth {
    // Location based on https://ja.wikipedia.org/wiki/%E3%83%AA%E3%82%AA%E3%83%87%E3%82%B8%E3%83%A3%E3%83%8D%E3%82%A4%E3%83%AD
    public RioDeJaneiro() {
        super(-(43.0 + 11.0 / 60.0 + 47.0 / 3600.0), -(22.0 + 54.0 / 60.0 + 30.0 / 3600.0), 0.0);
    }
}
