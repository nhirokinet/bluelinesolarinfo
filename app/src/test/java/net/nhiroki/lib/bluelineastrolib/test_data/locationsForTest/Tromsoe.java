package net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest;

import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;

public class Tromsoe extends LocationOnTheEarth {
    public Tromsoe() {
        // https://ja.wikipedia.org/wiki/%E3%83%88%E3%83%AD%E3%83%A0%E3%82%BD
        super(18.0 + 56.0 / 60.0 + 34.0 / 3600.0, 69.0 + 40.0 / 60.0 + 68.0 / 3600.0, 0.0);
    }
}
