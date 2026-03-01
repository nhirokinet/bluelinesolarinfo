package net.nhiroki.lib.bluelineastrolib.earth;


import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

public class TimePointOnTheEarthTest {
    @Test
    public void leapSecondsCountTest() {
        assertEquals(27, new TimePointOnTheEarth(Instant.parse("2999-12-31T23:59:59Z")).countLeapSecondsBefore());
        assertEquals(27, new TimePointOnTheEarth(Instant.parse("2024-06-01T00:00:00Z")).countLeapSecondsBefore());
        assertEquals(27, new TimePointOnTheEarth(Instant.parse("2017-01-01T00:00:00Z")).countLeapSecondsBefore());
        assertEquals(26, new TimePointOnTheEarth(Instant.parse("2016-12-31T23:59:59Z")).countLeapSecondsBefore());
        assertEquals(22, new TimePointOnTheEarth(Instant.parse("2000-01-01T12:00:00Z")).countLeapSecondsBefore());
        assertEquals(20, new TimePointOnTheEarth(Instant.parse("1996-01-01T00:00:00Z")).countLeapSecondsBefore());
        assertEquals(19, new TimePointOnTheEarth(Instant.parse("1995-12-31T23:59:59Z")).countLeapSecondsBefore());
        assertEquals(10, new TimePointOnTheEarth(Instant.parse("1981-07-01T00:00:00Z")).countLeapSecondsBefore());
        assertEquals(9, new TimePointOnTheEarth(Instant.parse("1981-06-30T23:59:59Z")).countLeapSecondsBefore());
        assertEquals(1, new TimePointOnTheEarth(Instant.parse("1972-07-01T00:00:00Z")).countLeapSecondsBefore());
        assertEquals(0, new TimePointOnTheEarth(Instant.parse("1972-06-30T23:59:59Z")).countLeapSecondsBefore());
        assertEquals(0, new TimePointOnTheEarth(Instant.parse("1800-01-01T00:00:00Z")).countLeapSecondsBefore());
    }

    @Test
    public void calculateJulianYearFromJ2000_0_Test() {
        // in J2000.0, UTC = TAI - 22s - 10s = TT - 22s - 10s - 32.184s
        assertEquals(0.0, new TimePointOnTheEarth(Instant.parse("2000-01-01T11:58:55.816Z")).julianYearFromJ2000_0() - 0.0, 0.0005 / 86400.0 / 365.25);
        // in J2000.0, UTC = TAI - 27s - 10s = TT - 27s - 10s - 32.184s
        assertEquals(24.0, new TimePointOnTheEarth(Instant.parse("2024-01-01T11:58:50.816Z")).julianYearFromJ2000_0(), 0.0005 / 86400.0 / 365.25);
        assertEquals(24.0 + 0.5 / 365.25, new TimePointOnTheEarth(Instant.parse("2024-01-01T23:58:50.816Z")).julianYearFromJ2000_0(), 0.0005 / 86400.0 / 365.25);
        assertEquals(24.0 + (43200.0 + 27.0 + 10.0 + 32.184) / 86400.0 / 365.25, new TimePointOnTheEarth(Instant.parse("2024-01-02T00:00:00Z")).julianYearFromJ2000_0(), 0.0005 / 86400.0 / 365.25);
    }

    @Test
    public void calculateSiderealTimeDegTest() throws UnsupportedDateRangeException {
        // Sprint Equinox in 2026 is 03/20 14:46 UTC
        //    Equation time is about -7min, so apparent solar time in E0deg is about 14:39
        //    So sidereal time should be about 02h39m
        // Summer solstice in 2026 is 06/21 08:25 UTC
        //    Equation time is about -2min, so apparent solar time in E0deg is about 08:23
        //    So sidereal time should be about 02h23m
        // Autumnal Equinox in 2026 is 09/23 00:05 UTC
        //    Equation time is about +7min, so apparent solar time in E0deg is about 00:11
        //    So sidereal time should be about 00h11m
        // Winter solstice in 2026 is 12/21 20:50 UTC
        //    Equation time is about +1min, so apparent solar time in E0deg is about 20:51
        //    So sidereal time should be about 02h51m
        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2026/rekiyou262.html
        // https://www.nao.ac.jp/faq/a0107.html
        assertEquals( 2.0 + 39.0 / 60.0, new TimePointOnTheEarth(Instant.parse("2026-03-20T14:46:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 1.0 / 60.0);
        assertEquals( 2.0 + 23.0 / 60.0, new TimePointOnTheEarth(Instant.parse("2026-06-21T08:25:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 1.0 / 60.0);
        assertEquals( 0.0 + 11.0 / 60.0, new TimePointOnTheEarth(Instant.parse("2026-09-23T00:05:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 2.0 / 60.0);
        assertEquals( 2.0 + 51.0 / 60.0, new TimePointOnTheEarth(Instant.parse("2026-12-21T20:51:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 2.0 / 60.0);

        // Sprint Equinox in 2004 is 03/20 06:49 UTC
        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2004/rekiyou042.html
        //   Equation time is about -7min, so apparent solar time in E0deg is about 06:42
        //   So sidereal time should be about 18h42m
        assertEquals(18.0 + 42.0 / 60.0, new TimePointOnTheEarth(Instant.parse("2004-03-20T06:49:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 1.0 / 60.0);

        // Compare with the result of calculator on NAO website
        // Took expected value from the result of https://eco.mtk.nao.ac.jp/cgi-bin/koyomi/cande/gst.cgi
        assertEquals( 6.0 + 43.0 / 60.0 +  7.139 / 3600.0, new TimePointOnTheEarth(Instant.parse("2009-01-01T00:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        assertEquals(20.0 + 37.0 / 60.0 +  9.024 / 3600.0, new TimePointOnTheEarth(Instant.parse("2015-08-01T00:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        assertEquals(10.0 + 35.0 / 60.0 + 15.816 / 3600.0, new TimePointOnTheEarth(Instant.parse("2026-03-01T00:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.02 / 3600.0);
        assertEquals(22.0 + 37.0 / 60.0 + 14.094 / 3600.0, new TimePointOnTheEarth(Instant.parse("2026-03-01T12:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.02 / 3600.0);
        assertEquals(16.0 + 37.0 / 60.0 + 58.861 / 3600.0, new TimePointOnTheEarth(Instant.parse("2026-06-01T00:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        assertEquals(22.0 + 40.0 / 60.0 + 42.112 / 3600.0, new TimePointOnTheEarth(Instant.parse("2026-09-01T00:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.04 / 3600.0);
        assertEquals( 4.0 + 39.0 / 60.0 + 28.652 / 3600.0, new TimePointOnTheEarth(Instant.parse("2026-12-01T00:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.04 / 3600.0);
        assertEquals( 5.0 + 40.0 / 60.0 + 35.070 / 3600.0, new TimePointOnTheEarth(Instant.parse("2027-12-31T23:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        // Not sure why, but NAO website shows this value, and it matches the current implementation.
        // Current implementation does not care about error of less than 0.9s, so it is not a problem in this code, but I'm not sure why NAO website outputs similar values.
        // calculateSiderealTimeDeg inputs time based on UTC and treat it as UT1.
        // Possibly the site also assumes input time is UT1, not UTC?
        // In my understanding the sidereal time should jump about 2 seconds from 2016/12/31 23:59:59 to 2017/01/01 00:00:00 as the difference is 2 seconds due to leap second...
        assertEquals( 5.0 + 43.0 / 60.0 + 10.854 / 3600.0, new TimePointOnTheEarth(Instant.parse("2016-12-31T23:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        assertEquals( 6.0 + 43.0 / 60.0 + 18.705 / 3600.0, new TimePointOnTheEarth(Instant.parse("2016-12-31T23:59:58Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        assertEquals( 6.0 + 43.0 / 60.0 + 19.708 / 3600.0, new TimePointOnTheEarth(Instant.parse("2016-12-31T23:59:59Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        assertEquals( 6.0 + 43.0 / 60.0 + 20.711 / 3600.0, new TimePointOnTheEarth(Instant.parse("2017-01-01T00:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        assertEquals( 6.0 + 43.0 / 60.0 + 21.714 / 3600.0, new TimePointOnTheEarth(Instant.parse("2017-01-01T00:00:01Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);
        assertEquals( 7.0 + 43.0 / 60.0 + 30.568 / 3600.0, new TimePointOnTheEarth(Instant.parse("2017-01-01T01:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 0.01 / 3600.0);

        // Copy of actual result of calculation
        assertEquals(18.71312, new TimePointOnTheEarth(Instant.parse("1999-01-01T12:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 1.0 / 3600.0);
        assertEquals(9.65161, new TimePointOnTheEarth(Instant.parse("1999-04-01T21:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 1.0 / 3600.0);
        assertEquals(18.70960, new TimePointOnTheEarth(Instant.parse("2024-01-01T12:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 1.0 / 3600.0);
        assertEquals(14.97632, new TimePointOnTheEarth(Instant.parse("2024-03-20T03:06:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 1.0 / 3600.0);
        assertEquals(0.68920, new TimePointOnTheEarth(Instant.parse("2024-04-01T12:00:00Z")).calculateSiderealTimeDeg(0.0) / 360.0 * 24.0, 1.0 / 3600.0);
    }

    @Test
    public void calculateSiderealTimeRadAndDegTest() throws UnsupportedDateRangeException {
        Instant t = Instant.parse("2000-01-01T00:00:00Z");

        for (int i = 0; i < 200 * 365; ++i) {
            for (double longitude = -180.0; longitude < 180.0; longitude += 12.3) {
                assertEquals(new TimePointOnTheEarth(t).calculateSiderealTimeRad(Math.toRadians(longitude)), Math.toRadians(new TimePointOnTheEarth(t).calculateSiderealTimeDeg(longitude)), 1e-9);
            }
            t = t.plusSeconds(86400);
        }
        assertEquals("2199-11-13T00:00:00Z", t.toString());
    }
}
