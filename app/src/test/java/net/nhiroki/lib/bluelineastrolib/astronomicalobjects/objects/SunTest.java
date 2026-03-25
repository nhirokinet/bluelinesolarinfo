package net.nhiroki.lib.bluelineastrolib.astronomicalobjects.objects;

import static org.junit.Assert.assertEquals;

import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;

import org.junit.Test;

import java.time.Instant;

public class SunTest {
    private double calculateEclipticLongitudeDegBySuirobuKubo1980(Instant t) {
        double T = new TimePointOnTheEarth(t).julianYearFromJ2000_0() / 100.0;

        // https://www1.kaiho.mlit.go.jp/kenkyu/report/rhr15/rhr15-06.pdf
        // Table 12 of this page shows this formula got up to 6 arcsecs of error to 3653 points from 1972 to 1981.
        // The author expects this precision to be retained for 60 years centering at 2000.
        double ret = 0.0;
        ret += 0.0004 * Math.cos(Math.toRadians( 31557.0  * T + 161.0));
        ret += 0.0004 * Math.cos(Math.toRadians( 29930.0  * T +  48.0));
        ret += 0.0005 * Math.cos(Math.toRadians(  2281.0  * T + 221.0));
        ret += 0.0005 * Math.cos(Math.toRadians(   155.0  * T + 118.0));
        ret += 0.0006 * Math.cos(Math.toRadians( 33718.0  * T + 316.0));
        ret += 0.0007 * Math.cos(Math.toRadians(  9038.0  * T +  64.0));
        ret += 0.0007 * Math.cos(Math.toRadians(  3035.0  * T + 110.0));
        ret += 0.0007 * Math.cos(Math.toRadians( 65929.0  * T +  45.0));
        ret += 0.0013 * Math.cos(Math.toRadians( 22519.0  * T + 352.0));
        ret += 0.0015 * Math.cos(Math.toRadians( 45038.0  * T + 254.0));
        ret += 0.0018 * Math.cos(Math.toRadians(445267.0  * T + 208.0));
        ret += 0.0018 * Math.cos(Math.toRadians(    19.0  * T + 159.0));
        ret += 0.0020 * Math.cos(Math.toRadians( 32964.0  * T + 158.0));
        ret += 0.0200 * Math.cos(Math.toRadians( 71998.1  * T + 265.1));
        ret -= 0.0048 * T * Math.cos(Math.toRadians(35999.05 * T + 267.52));
        ret += 1.9147 * Math.cos(Math.toRadians( 35999.05 * T + 267.52));
        ret += 280.4659 + 36000.7695 * T;

        // The above gets the value referring to the mean equinox
        // To get apparent longitude adding this line
        ret += -0.0057 + 0.0048 * Math.cos(Math.toRadians(1934.0 * T + 145.0));

        ret -= Math.floor(ret / 360.0) * 360.0;

        return ret;
    }

    private double calculateDistanceFromTheEarthAUBySuirobuKubo1980(Instant t) {
        double T = new TimePointOnTheEarth(t).julianYearFromJ2000_0() / 100.0;

        // https://www1.kaiho.mlit.go.jp/kenkyu/report/rhr15/rhr15-06.pdf
        // Table 12 of this page shows this formula got up to 4 * 10^-5 AU of error to 3653 points from 1972 to 1981.
        // The author expects this precision to be retained for 60 years centering at 2000.
        double ret = 0.0;
        ret += 0.000005 * Math.cos(Math.toRadians( 33718.0  * T + 226.0));
        ret += 0.000005 * Math.cos(Math.toRadians( 22519.0  * T + 233.0));
        ret += 0.000016 * Math.cos(Math.toRadians( 45038.0  * T + 164.0));
        ret += 0.000016 * Math.cos(Math.toRadians( 32964.0  * T +  68.0));
        ret += 0.000031 * Math.cos(Math.toRadians(445267.0  * T + 298.0));
        ret += 0.000139 * Math.cos(Math.toRadians( 71998.0  * T + 175.0));
        ret -= 0.000042 * T * Math.cos(Math.toRadians( 35999.05 * T + 177.53));
        ret += 0.016706 * Math.cos(Math.toRadians( 35999.05 * T + 177.53));
        ret += 1.00014;

        return ret;
    }

    @Test
    public void calculateEclipticLongitudeDegTest() {
        // delta is based on the result when I tested. it is not the indication of tolerance.
        // Just note that this values goes 360deg in a year, so if the time granularity is minute, delta of less than 360/365.2422/1440 does not make sense.

        Sun sun = new Sun();

        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2004/rekiyou042.html
        assertEquals(  0.0, sun.calculateEclipticCoordinates(Instant.parse("2004-03-20T06:49:00Z")).getLongitudeDeg(), 0.005);
        assertEquals( 90.0, sun.calculateEclipticCoordinates(Instant.parse("2004-06-21T00:57:00Z")).getLongitudeDeg(), 0.005);
        assertEquals(180.0, sun.calculateEclipticCoordinates(Instant.parse("2004-09-22T16:30:00Z")).getLongitudeDeg(), 0.002);
        assertEquals(270.0, sun.calculateEclipticCoordinates(Instant.parse("2004-12-21T12:42:00Z")).getLongitudeDeg(), 0.002);

        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2026/rekiyou262.html
        assertEquals(315.0, sun.calculateEclipticCoordinates(Instant.parse("2026-02-03T20:02:00Z")).getLongitudeDeg(), 0.003);
        assertEquals(  0.0, sun.calculateEclipticCoordinates(Instant.parse("2026-03-20T14:46:00Z")).getLongitudeDeg(), 0.005);
        assertEquals( 90.0, sun.calculateEclipticCoordinates(Instant.parse("2026-06-21T08:25:00Z")).getLongitudeDeg(), 0.001);
        assertEquals(180.0, sun.calculateEclipticCoordinates(Instant.parse("2026-09-23T00:05:00Z")).getLongitudeDeg(), 0.009);
        assertEquals(270.0, sun.calculateEclipticCoordinates(Instant.parse("2026-12-21T20:50:00Z")).getLongitudeDeg(), 0.005);
    }

    @Test
    public void referResultOfFormulaBySuirobuKubo1980() {
        // delta is based on the result when I tested. it is not the indication of tolerance.
        // Just note that this values goes 360deg in a year, so if the time granularity is minute, delta of less than 360/365.2422/1440 does not make sense.

        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2004/rekiyou042.html
        assertEquals(  0.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2004-03-20T06:49:00Z")), 0.0003);
        assertEquals( 90.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2004-06-21T00:57:00Z")), 0.0005);
        assertEquals(180.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2004-09-22T16:30:00Z")), 0.0006);
        assertEquals(270.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2004-12-21T12:42:00Z")), 0.0002);

        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2026/rekiyou262.html
        assertEquals(315.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2026-02-03T20:02:00Z")), 0.002);
        assertEquals(360.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2026-03-20T14:46:00Z")), 0.005);
        assertEquals( 90.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2026-06-21T08:25:00Z")), 0.002);
        assertEquals(180.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2026-09-23T00:05:00Z")), 0.002);
        assertEquals(270.0, calculateEclipticLongitudeDegBySuirobuKubo1980(Instant.parse("2026-12-21T20:50:00Z")), 0.007);
    }

    @Test
    public void eclipticLongitudeCompareWithSuirobuKubo1980() {
        Sun sun = new Sun();

        // Formula by Suirobu Kubo 1980 is described as it is expected to retain precision for 1970-2030, so this check goes fat beyond than that.
        // But just this test passed. Leaving this test to have one record about how these two functions matches.
        Instant t = Instant.parse("1980-01-01T00:00:00Z");
        for (int i = 0; i < 365 * 24 * 70; ++i) {
            double diff = sun.calculateEclipticCoordinates(t).getLongitudeDeg() - calculateEclipticLongitudeDegBySuirobuKubo1980(t);
            diff -= Math.floor((diff + 180.0) / 360.0) * 360.0;
            assertEquals(0.0, diff, 0.8 / 60.0);

            t = t.plusSeconds(3600);
        }
        assertEquals("2049-12-14T00:00:00Z", t.toString());
        for (int i = 0; i < 365 * 24 * 151; ++i) {
            double diff = sun.calculateEclipticCoordinates(t).getLongitudeDeg() - calculateEclipticLongitudeDegBySuirobuKubo1980(t);
            diff -= Math.floor((diff + 180.0) / 360.0) * 360.0;
            assertEquals(0.0, diff, 1.2 / 60.0);

            t = t.plusSeconds(3600);
        }
        assertEquals("2200-11-08T00:00:00Z", t.toString());
    }

    @Test
    public void distanceFromEarthAUCompareWithSuirobuKubo1980() {
        Sun sun = new Sun();

        // Formula by Suirobu Kubo 1980 is described as it is expected to retain precision for 1970-2030, so this check goes fat beyond than that.
        // But just this test passed. Leaving this test to have one record about how these two functions matches.
        Instant t = Instant.parse("1980-01-01T00:00:00Z");
        for (int i = 0; i < 365 * 24 * 70; ++i) {
            assertEquals(calculateDistanceFromTheEarthAUBySuirobuKubo1980(t), sun.calculateDistanceFromTheEarthAU(t), 9e-5);

            t = t.plusSeconds(3600);
        }
        assertEquals("2049-12-14T00:00:00Z", t.toString());
        for (int i = 0; i < 365 * 24 * 151; ++i) {
            assertEquals(calculateDistanceFromTheEarthAUBySuirobuKubo1980(t), sun.calculateDistanceFromTheEarthAU(t), 1.5e-4);

            t = t.plusSeconds(3600);
        }
        assertEquals("2200-11-08T00:00:00Z", t.toString());
    }

    @Test
    public void calculateDistanceFromTheEarthTest() {
        Sun sun = new Sun();
        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        assertEquals(1.0, sun.calculateDistanceFromTheEarthAU(t), 0.04);
        assertEquals(149597870.7, sun.calculateDistanceFromTheEarthKM(t), 5000000.0);
    }
}
