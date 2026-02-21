package net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects;

import static org.junit.Assert.assertEquals;

import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;

import org.junit.Test;

import java.time.Instant;

public class SunTest {
    private double calculateEclipticLongitudeDegBySuirobuKubo1980(Instant t) {
        double T = new TimePointOnTheEarth(t).julianYearFromJ2000_0() / 100.0;

        // https://www1.kaiho.mlit.go.jp/kenkyu/report/rhr15/rhr15-06.pdf
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

        ret -= Math.floor(ret / 360.0) * 360.0;

        return ret;
    }

    private double calculateDistanceFromTheEarthAUBySuirobuKubo1980(Instant t) {
        double T = new TimePointOnTheEarth(t).julianYearFromJ2000_0() / 100.0;

        // https://www1.kaiho.mlit.go.jp/kenkyu/report/rhr15/rhr15-06.pdf
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
        // delta is based on the result when I tested. it is not tolerance.
        // Just note that this values goes 360deg in a year, so if the time granularity is minute, delta of less than 360/365.2422/1440 does not make sense.

        Sun sun = new Sun();

        // Sprint Equinox in 2026 is 03/20 14:46 UTC
        // Summer solstice in 2026 is 06/21 08:25 UTC
        // Autumnal Equinox in 2026 is 09/23 00:05 UTC
        // Winter solstice in 2026 is 12/21 20:50 UTC
        // 立春(315deg) in 2026 is 02/03 20:02 UTC
        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2026/rekiyou262.html
        assertEquals(315.0, sun.calculateEclipticLongitudeDeg(Instant.parse("2026-02-03T20:02:00Z")), 0.003);
        assertEquals(  0.0, sun.calculateEclipticLongitudeDeg(Instant.parse("2026-03-20T14:46:00Z")), 0.005);
        assertEquals( 90.0, sun.calculateEclipticLongitudeDeg(Instant.parse("2026-06-21T08:25:00Z")), 0.001);
        assertEquals(180.0, sun.calculateEclipticLongitudeDeg(Instant.parse("2026-09-23T00:05:00Z")), 0.01);
        assertEquals(270.0, sun.calculateEclipticLongitudeDeg(Instant.parse("2026-12-21T20:50:00Z")), 0.005);

        // Sprint Equinox in 2004 is 03/20 06:49 UTC
        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2004/rekiyou042.html
        assertEquals(  0.0, sun.calculateEclipticLongitudeDeg(Instant.parse("2004-03-20T06:49:00Z")), 0.005);
    }

    @Test
    public void eclipticLongitudeCompareWithSuirobuKubo1980() {
        Sun sun = new Sun();

        Instant t = Instant.parse("1980-01-01T00:00:00Z");
        for (int i = 0; i < 365 * 24 * 70; ++i) {
            double diff = sun.calculateEclipticLongitudeDeg(t) - calculateEclipticLongitudeDegBySuirobuKubo1980(t);
            diff -= Math.floor((diff + 180.0) / 360.0) * 360.0;
            assertEquals(0.0, diff, 0.02);

            t = t.plusSeconds(3600);
        }
        assertEquals("2049-12-14T00:00:00Z", t.toString());
    }

    @Test
    public void distanceFromEarthAUCompareWithSuirobuKubo1980() {
        Sun sun = new Sun();

        Instant t = Instant.parse("1980-01-01T00:00:00Z");
        for (int i = 0; i < 365 * 24 * 70; ++i) {
            assertEquals(calculateDistanceFromTheEarthAUBySuirobuKubo1980(t), sun.calculateDistanceFromTheEarthAU(t), 2e-4);

            t = t.plusSeconds(3600);
        }
        assertEquals("2049-12-14T00:00:00Z", t.toString());
    }

    @Test
    public void calculateDistanceFromTheEarthTest() {
        Sun sun = new Sun();
        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        assertEquals(1.0, sun.calculateDistanceFromTheEarthAU(t), 0.04);
        assertEquals(149597870.7, sun.calculateDistanceFromTheEarthKM(t), 5000000.0);
    }
}
