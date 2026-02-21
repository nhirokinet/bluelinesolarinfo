package net.nhiroki.lib.bluelineastrolib.test_data;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Moon;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.test_data.fixedStarsForTest.Polaris;
import net.nhiroki.lib.bluelineastrolib.test_data.fixedStarsForTest.Sirius;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.NorthPoleE0Z;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.NorthPoleE135Z;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.NullIsland;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.RioDeJaneiro;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.ShowaStation;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.SouthPoleE0Z;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.TokyoNAO;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.TopOfMtFuji;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.Tromsoe;

public class AstroComputingTestDataList {
    public static LocationOnTheEarth[] listLocationsForTest() {
        return new LocationOnTheEarth[] {
                new NorthPoleE0Z(),
                new NorthPoleE135Z(),
                new NullIsland(),
                new RioDeJaneiro(),
                new ShowaStation(),
                new SouthPoleE0Z(),
                new TokyoNAO(),
                new TopOfMtFuji(),
                new Tromsoe(),
        };
    }

    public static AstronomicalObject[] listAstronomicalObjectsForTest() {
        return new AstronomicalObject[] {
                new Sun(),
                new Moon(),
                new Polaris(),
                new Sirius(),
        };
    }
}
