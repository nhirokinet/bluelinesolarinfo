package net.nhiroki.lib.bluelineastrolib.coordinates;

import static org.junit.Assert.*;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Moon;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;
import net.nhiroki.lib.bluelineastrolib.logic.AstronomicalEventsCalculation;
import net.nhiroki.lib.bluelineastrolib.test_data.LocationsForTest;

import org.junit.Test;

import java.time.Instant;


public class HorizontalCoordinatesFromGroundTest {
    @Test
    public void testConsistencyWithRiseSet() throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        Sun sun = new Sun();
        Moon moon = new Moon();

        LocationOnTheEarth placesToTest[] = new LocationOnTheEarth[] {
                LocationsForTest.getTokyoNAO(),
                LocationsForTest.getTopOfMtFuji(),
        };

        for (LocationOnTheEarth place : placesToTest) {
            for (Instant testDay = Instant.parse("2026-01-01T00:00:00Z"); testDay.isBefore(Instant.parse("2027-01-01T00:00:00Z")); testDay = testDay.plusSeconds(86400)) {
                Instant sunrise = AstronomicalEventsCalculation.calculateRiseWithin24h(sun, testDay, place, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
                assertFalse(HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(sun, sunrise.minusMillis(500), place).isTopAboveHorizon());
                assertTrue(HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(sun, sunrise.plusMillis(500), place).isTopAboveHorizon());

                Instant sunset = AstronomicalEventsCalculation.calculateSetWithin24h(sun, testDay, place, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
                assertTrue(HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(sun, sunset.minusMillis(500), place).isTopAboveHorizon());
                assertFalse(HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(sun, sunset.plusMillis(500), place).isTopAboveHorizon());
            }
        }

        {
            LocationOnTheEarth place = LocationsForTest.getTokyoNAO();
            for (Instant testDay = Instant.parse("2026-01-01T00:00:00Z"); testDay.isBefore(Instant.parse("2027-01-01T00:00:00Z")); testDay = testDay.plusSeconds(86400)) {
                // Constant for judging rise/set and function to calculate atmospheric refraction differs.
                // This difference makes about 0.5 arcmin.
                Instant moonrise = AstronomicalEventsCalculation.calculateRiseWithin24h(moon, testDay, place, true, AstronomicalEventsCalculation.ReferencePoint.CENTER);
                if (moonrise != null) {
                    assertEquals(-0.5 / 60.0, HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(moon, moonrise, place).calculateApparentElevationDeg(), 0.1 / 60.0);
                }
                Instant moonset = AstronomicalEventsCalculation.calculateSetWithin24h(moon, testDay, place, true, AstronomicalEventsCalculation.ReferencePoint.CENTER);
                if (moonset != null) {
                    assertEquals(-0.5 / 60.0, HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(moon, moonset, place).calculateApparentElevationDeg(), 0.1 / 60.0);
                }
            }
        }
    }
}
