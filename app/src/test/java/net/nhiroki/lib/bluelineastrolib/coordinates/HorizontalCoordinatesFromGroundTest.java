package net.nhiroki.lib.bluelineastrolib.coordinates;

import static org.junit.Assert.*;

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

        LocationOnTheEarth placesToTest[] = new LocationOnTheEarth[] {
                LocationsForTest.getTokyoNAO(),
                LocationsForTest.getTopOfMtFuji(),
        };

        for (LocationOnTheEarth place : placesToTest) {
            for (Instant testDay = Instant.parse("2026-01-01T00:00:00Z"); testDay.isBefore(Instant.parse("2027-01-01T00:00:00Z")); testDay = testDay.plusSeconds(86400)) {
                Instant sunriseOnTestDay1 = AstronomicalEventsCalculation.calculateRiseWithin24h(sun, testDay, place, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
                assertFalse(HorizontalCoordinatesFromGround.ofAstronomicalObject(sun, sunriseOnTestDay1.minusMillis(500), place).isTopAboveHorizon());
                assertTrue(HorizontalCoordinatesFromGround.ofAstronomicalObject(sun, sunriseOnTestDay1.plusMillis(500), place).isTopAboveHorizon());

                Instant sunsetOnTestDay1 = AstronomicalEventsCalculation.calculateSetWithin24h(sun, testDay, place, true, AstronomicalEventsCalculation.ReferencePoint.TOP);
                assertTrue(HorizontalCoordinatesFromGround.ofAstronomicalObject(sun, sunsetOnTestDay1.minusMillis(500), place).isTopAboveHorizon());
                assertFalse(HorizontalCoordinatesFromGround.ofAstronomicalObject(sun, sunsetOnTestDay1.plusMillis(500), place).isTopAboveHorizon());
            }
        }
    }
}
