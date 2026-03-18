package net.nhiroki.bluelinesolarinfo.test_data;

import net.nhiroki.lib.bluelineastrolib.coordinates.LocationOnTheEarth;


public class LocationsForTest {
    public static LocationOnTheEarth[] listLocationsForTest() {
        return new LocationOnTheEarth[] {
                LocationsForTest.getNorthPoleE0Z(),
                LocationsForTest.getNorthPoleE135Z(),
                LocationsForTest.getNullIsland(),
                LocationsForTest.getRioDeJaneiro(),
                LocationsForTest.getShowaStation(),
                LocationsForTest.getSouthPoleE0Z(),
                LocationsForTest.getTokyoNAO(),
                LocationsForTest.getTopOfMtFuji(),
                LocationsForTest.getTromsoe(),
        };
    }

    // https://eco.mtk.nao.ac.jp/koyomi/dni/2024/s1301.html
    public static LocationOnTheEarth getTokyoNAO() {
        return LocationOnTheEarth.ofDegreesMeters(139.7414, 35.6581, 0.0);
    }

    public static LocationOnTheEarth getNorthPoleE0Z() {
        return LocationOnTheEarth.ofDegreesMeters(0.0, 90.0, 0.0);
    }

    public static LocationOnTheEarth getNorthPoleE135Z() {
        return LocationOnTheEarth.ofDegreesMeters(135.0, 90.0, 0.0);
    }

    public static LocationOnTheEarth getSouthPoleE0Z() {
        return LocationOnTheEarth.ofDegreesMeters(0.0, -90.0, 0.0);
    }

    // https://en.wikipedia.org/wiki/Null_Island
    public static LocationOnTheEarth getNullIsland() {
        return LocationOnTheEarth.ofDegreesMeters(0.0, 0.0, 0.0);
    }

    // Place based on https://ja.wikipedia.org/wiki/%E5%AF%8C%E5%A3%AB%E5%B1%B1
    public static LocationOnTheEarth getTopOfMtFuji() {
        return LocationOnTheEarth.ofDegreesMeters(138.0 + 43.0/60.0 + 39.0 / 3600.0, 35.0 + 21.0 / 60.0 + 38.0 / 3600.0, 3776.0);
    }

    // Location based on https://ja.wikipedia.org/wiki/%E3%83%AA%E3%82%AA%E3%83%87%E3%82%B8%E3%83%A3%E3%83%8D%E3%82%A4%E3%83%AD
    public static LocationOnTheEarth getRioDeJaneiro() {
        return LocationOnTheEarth.ofDegreesMeters(-(43.0 + 11.0 / 60.0 + 47.0 / 3600.0), -(22.0 + 54.0 / 60.0 + 30.0 / 3600.0), 0.0);
    }

    // https://ja.wikipedia.org/wiki/%E6%98%AD%E5%92%8C%E5%9F%BA%E5%9C%B0
    public static LocationOnTheEarth getShowaStation() {
        return LocationOnTheEarth.ofDegreesMeters(39.0 + 35.0 / 60.0 + 1.48 / 3600.0, -(69.0 + 25.05 / 3600.0), 28.8);
    }

    // https://ja.wikipedia.org/wiki/%E3%83%88%E3%83%AD%E3%83%A0%E3%82%BD
    public static LocationOnTheEarth getTromsoe() {
        return LocationOnTheEarth.ofDegreesMeters(18.0 + 56.0 / 60.0 + 34.0 / 3600.0, 69.0 + 40.0 / 60.0 + 68.0 / 3600.0, 0.0);
    }
}
