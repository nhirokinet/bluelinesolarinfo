package net.nhiroki.lib.bluelineastrolib.earth;

import org.junit.Test;

import static org.junit.Assert.*;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;


public class EarthTest {
    @Test
    public void calculateAltitudeCorrectionOfHorizonRadTest() {
        assertEquals(0.0, Earth.calculateAltitudeCorrectionOfHorizonRad(0.0), 0.0);

        // Copy of actual value to detect change
        assertEquals(8.540208314459972E-4, Earth.calculateAltitudeCorrectionOfHorizonRad(2.0), 1e-9);
        assertEquals(0.037108163307640656, Earth.calculateAltitudeCorrectionOfHorizonRad(3776.0), 1e-9);
    }

    @Test
    public void calculateAtmosphericRefractionRadFromActualElevationRadTest() {
        assertThrows(IllegalArgumentException.class, () -> Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(-3.1)));

        // For invisible area, it is OK if the function does not crash and gets not to wrong value
        assertEquals(35.0, Math.toDegrees(Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(-2.0))) * 60.0, 15.0);
        assertEquals(35.0, Math.toDegrees(Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(-1.0))) * 60.0, 5.0);

        // This test takes apparent horizon refraction as the value we use for rise/set
        assertEquals(35.0 + 8.0 / 60.0, Math.toDegrees(Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(-35.0 / 60.0 - 8.0 / 3600.0))) * 60.0, 0.7);

        // Took values from https://en.wikipedia.org/wiki/Atmospheric_refraction
        assertEquals(18.4, Math.toDegrees(Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(2.0 - 18.4 / 60.0))) * 60.0, 1.0);
        assertEquals(9.9, Math.toDegrees(Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(5.0 - 9.9 / 60.0))) * 60.0, 1.0);
        assertEquals(5.3, Math.toDegrees(Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(10.0 - 5.3 / 60.0))) * 60.0, 0.5);

        // This page says apparent elevation is 45 deg it is less than 1 arcmin https://en.wikipedia.org/wiki/Atmospheric_refraction
        assertEquals(1.0, Math.toDegrees(Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(45.0 - 1.0))) * 60.0, 0.1);

        // The value should be zero if 90 degrees
        assertEquals(0.0, Math.toDegrees(Earth.calculateAtmosphericRefractionRadFromActualElevationRad(Math.toRadians(90.0))) * 60.0, 0.002);
    }

    @Test
    public void calculateEclipticTiltDegTest() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        assertEquals(23.5,  Earth.calculateEclipticTiltDeg(Instant.parse("1500-01-01T00:00:00Z")), 0.05);
        assertEquals(23.44, Earth.calculateEclipticTiltDeg(Instant.parse("2000-01-01T00:00:00Z")), 0.01);
        assertEquals(23.44, Earth.calculateEclipticTiltDeg(Instant.parse("2026-01-01T00:00:00Z")), 0.01);
        assertEquals(23.44, Earth.calculateEclipticTiltDeg(Instant.parse("2026-07-01T00:00:00Z")), 0.01);
        assertEquals(23.44, Earth.calculateEclipticTiltDeg(Instant.parse("2050-01-01T00:00:00Z")), 0.01);
        assertEquals(23.4,  Earth.calculateEclipticTiltDeg(Instant.parse("2100-01-01T00:00:00Z")), 0.05);
        assertEquals(23.3,  Earth.calculateEclipticTiltDeg(Instant.parse("3000-01-01T00:00:00Z")), 0.05);

        // Copy of result after implementation to detect change
        assertEquals(23.50423222318099,  Earth.calculateEclipticTiltDeg(Instant.parse("1500-01-01T00:00:00Z")), 1e-9);
        assertEquals(23.43929128884232,  Earth.calculateEclipticTiltDeg(Instant.parse("2000-01-01T00:00:00Z")), 1e-9);
        assertEquals(23.435910446008954, Earth.calculateEclipticTiltDeg(Instant.parse("2026-01-01T00:00:00Z")), 1e-9);
        assertEquals(23.43585669169781,  Earth.calculateEclipticTiltDeg(Instant.parse("2026-06-01T00:00:00Z")), 1e-9);
        assertEquals(23.435780510114597, Earth.calculateEclipticTiltDeg(Instant.parse("2027-01-01T00:00:00Z")), 1e-9);
        assertEquals(23.43278987780477,  Earth.calculateEclipticTiltDeg(Instant.parse("2050-01-01T00:00:00Z")), 1e-9);
        assertEquals(23.309816823934558, Earth.calculateEclipticTiltDeg(Instant.parse("3000-01-01T00:00:00Z")), 1e-9);
    }

    @Test
    public void calculateEclipticTileDegAndRadDiffTest() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        Instant t = Instant.parse("2000-01-01T00:00:00Z");

        for (int i = 0; i < 200 * 365; ++i) {
            assertEquals(Earth.calculateEclipticTiltRad(t), Math.toRadians(Earth.calculateEclipticTiltDeg(t)), 1e-9);
            t = t.plusSeconds(86400);
        }
        assertEquals("2199-11-13T00:00:00Z", t.toString());
    }

    @Test
    public void calculateEquatorialHorizontalParallaxRadByDistanceAUTest() {
        // Compare with direct calculation
        // Assume radius of the earth as 6378.1366km referencing some values in https://ja.wikipedia.org/wiki/%E5%9C%B0%E7%90%83%E5%8D%8A%E5%BE%84

        assertEquals(Math.atan(6378.1366 / Sun.AU_IN_KM), Earth.calculateEquatorialHorizontalParallaxRadByDistanceAU(1.0), 1e-9);
        assertEquals(1.0, Math.atan(6378.1366 / Sun.AU_IN_KM) / Earth.calculateEquatorialHorizontalParallaxRadByDistanceAU(1.0), 1e-6);

        assertEquals(Math.atan(6378.1366 / (2.0 * Sun.AU_IN_KM)), Earth.calculateEquatorialHorizontalParallaxRadByDistanceAU(2.0), 1e-9);
        assertEquals(1.0, Math.atan(6378.1366 / (2.0 * Sun.AU_IN_KM)) / Earth.calculateEquatorialHorizontalParallaxRadByDistanceAU(2.0), 1e-6);

        assertEquals(Math.atan(6378.1366 / (0.8 * Sun.AU_IN_KM)), Earth.calculateEquatorialHorizontalParallaxRadByDistanceAU(0.8), 1e-9);
        assertEquals(1.0, Math.atan(6378.1366 / (0.8 * Sun.AU_IN_KM)) / Earth.calculateEquatorialHorizontalParallaxRadByDistanceAU(0.8), 1e-6);
    }

    @Test
    public void calculateDistanceAUByEquatorialHorizontalParallaxRadTest() {
        // Compare with the reverse calculation

        for (double distanceAU = 0.5; distanceAU < 3.0; distanceAU += 0.1) {
            double parallaxRad = Earth.calculateEquatorialHorizontalParallaxRadByDistanceAU(distanceAU);

            assertEquals(distanceAU, Earth.calculateDistanceAUByEquatorialHorizontalParallaxRad(parallaxRad), 1e-14);
            assertEquals(1.0, (distanceAU / Earth.calculateDistanceAUByEquatorialHorizontalParallaxRad(parallaxRad)), 1e-14);
        }
    }
}
