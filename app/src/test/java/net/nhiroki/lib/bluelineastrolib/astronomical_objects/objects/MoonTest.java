package net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects;

import static org.junit.Assert.assertEquals;

import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import org.junit.Test;

import java.time.Instant;

public class MoonTest {
    @Test
    public void calculateRightAscensionRadTest() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        Moon moon = new Moon();

        // 19h45m27.4s at 1999/11/14 00:00 UTC, from p 120 of 日の出・日の入りの計算 天体の出没時刻の求め方 by 長沢 工
        //   It's described as from 理科年表
        // https://www.chijinshokan.co.jp/Books/ISBN4-8052-0634-9.htm
        assertEquals((19.0 + 45.0 / 60.0 + 27.4 / 3600.0) * 15.0, Math.toDegrees(moon.calculateRightAscensionRad(Instant.parse("1999-11-14T00:00:00Z"))), 0.01);
    }

    @Test
    public void calculateEquatorialHorizontalParallaxRadTest() {
        Moon moon = new Moon();

        // 54'10.6" at 1999/11/13 00:00 UTC, from p 131 of 日の出・日の入りの計算 天体の出没時刻の求め方 by 長沢 工
        //   It's described as from 理科年表
        // https://www.chijinshokan.co.jp/Books/ISBN4-8052-0634-9.htm
        assertEquals(54.0 / 60.0 + 10.6 / 3600.0, Math.toDegrees(moon.calculateEquatorialHorizontalParallaxRad(Instant.parse("1999-11-13T00:00:00Z"))), 0.2 / 3600.0);

        // 54'28.8" at 1999/11/14 00:00 UTC, from p 131 of 日の出・日の入りの計算 天体の出没時刻の求め方 by 長沢 工
        //   It's described as from 理科年表
        // https://www.chijinshokan.co.jp/Books/ISBN4-8052-0634-9.htm
        assertEquals(54.0 / 60.0 + 28.8 / 3600.0, Math.toDegrees(moon.calculateEquatorialHorizontalParallaxRad(Instant.parse("1999-11-14T00:00:00Z"))), 0.2 / 3600.0);

        // 54'56.4" at 1999/11/15 00:00 UTC, from p 131 of 日の出・日の入りの計算 天体の出没時刻の求め方 by 長沢 工
        //   It's described as from 理科年表
        // https://www.chijinshokan.co.jp/Books/ISBN4-8052-0634-9.htm
        assertEquals(54.0 / 60.0 + 56.4 / 3600.0, Math.toDegrees(moon.calculateEquatorialHorizontalParallaxRad(Instant.parse("1999-11-15T00:00:00Z"))), 0.2 / 3600.0);
    }

    @Test
    public void calculateEclipticLongitudeDegTest() {
        Moon moon = new Moon();

        // 294.6433deg at 1999/11/14 00:00 UTC, from p 129 of 日の出・日の入りの計算 天体の出没時刻の求め方 by 長沢 工
        // https://www.chijinshokan.co.jp/Books/ISBN4-8052-0634-9.htm
        assertEquals(294.6433, moon.calculateEclipticLongitudeDeg(Instant.parse("1999-11-14T00:00:00Z")), 0.005);
    }

    @Test
    public void calculateEclipticLatitudeDegTest() {
        Moon moon = new Moon();

        // 1.0609deg at 1999/11/14 00:00 UTC, from p 130 of 日の出・日の入りの計算 天体の出没時刻の求め方 by 長沢 工
        // https://www.chijinshokan.co.jp/Books/ISBN4-8052-0634-9.htm
        assertEquals(1.0609, moon.calculateEclipticLatitudeDeg(Instant.parse("1999-11-14T00:00:00Z")), 1e-4);
    }

    @Test
    public void calculateApparentRadiusRadTest() {
        Sun sun = new Sun();
        Moon moon = new Moon();

        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        for (int i = 0; i < 365; ++i) {
            // Roughly the same
            assertEquals(1.0, moon.calculateApparentRadiusRad(t) / sun.calculateApparentRadiusRad(t), 0.1);
            t = t.plusSeconds(86400);
        }
        assertEquals("2027-01-01T00:00:00Z", t.toString());
    }

    @Test
    public void calculateDistanceFromTheEarthTest() {
        Moon moon = new Moon();
        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        assertEquals(385000.0, moon.calculateDistanceFromTheEarthAU(t) * 149597870.7, 25000.0);
        assertEquals(385000.0, moon.calculateDistanceFromTheEarthKM(t), 25000.0);
    }

    @Test
    public void estimatedIncrementOfRightAscensionDegPerDayTest() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        Moon moon = new Moon();

        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        for (int i = 0; i < 100 * 365 * 4; ++i) {
            // Roughly 360.0/29.53 + new Sun().estimatedIncrementOfRightAscensionDegPerDay(t)
            // But changes so much
            assertEquals(13.17, Math.toDegrees(moon.estimatedIncrementOfRightAscensionRadPerDay(t)), 5.0);
            t = t.plusSeconds(21600);
        }
        assertEquals("2125-12-08T00:00:00Z", t.toString());
    }
}
