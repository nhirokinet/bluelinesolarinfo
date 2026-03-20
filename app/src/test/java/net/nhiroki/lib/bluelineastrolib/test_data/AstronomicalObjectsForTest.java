package net.nhiroki.lib.bluelineastrolib.test_data;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.FixedStar;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Moon;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.coordinates.CelestialCoordinatesWithRightAscension;


public class AstronomicalObjectsForTest {
    public static AstronomicalObject[] listAstronomicalObjectsForTest() {
        return new AstronomicalObject[] {
                new Sun(),
                new Moon(),
                AstronomicalObjectsForTest.getPolaris(),
                AstronomicalObjectsForTest.getSirius(),
        };
    }


    public static FixedStar getPolaris() {
        // https://ja.wikipedia.org/wiki/%E3%83%9D%E3%83%A9%E3%83%AA%E3%82%B9_(%E6%81%92%E6%98%9F)
        return new FixedStar(CelestialCoordinatesWithRightAscension.ofRadians(
                Math.toRadians((2.0 + 31.0 / 60.0 + 49.09456 / 4600.0) * 15.0),
                Math.toRadians(89.0 + 15.0 / 60.0 + 50.7923 / 3600.0)));

    }

    public static FixedStar getSirius() {
        // https://ja.wikipedia.org/wiki/%E3%82%B7%E3%83%AA%E3%82%A6%E3%82%B9
        return new FixedStar(CelestialCoordinatesWithRightAscension.ofRadians(
                Math.toRadians((6.0 + 45.0 / 60.0 + 08.91728 / 3600.0) * 15.0),
                Math.toRadians(-(16.0 + 42.0 / 60.0 + 58.0171 / 3600.0))));
    }
}
