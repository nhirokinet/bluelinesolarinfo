package net.nhiroki.lib.bluelineastrolib.coordinates;

import static org.junit.Assert.*;

import net.nhiroki.lib.bluelineastrolib.astronomicalobjects.objects.Moon;
import net.nhiroki.lib.bluelineastrolib.astronomicalobjects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;
import net.nhiroki.lib.bluelineastrolib.logic.AstronomicalEventsCalculation;
import net.nhiroki.lib.bluelineastrolib.test_data.LocationsForTest;

import org.junit.Test;

import java.time.Instant;


public class HorizontalCoordinatesFromGroundTest {
    @Test
    public void testCalculateApparentElevationDeg() throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        {
            // https://eco.mtk.nao.ac.jp/koyomi/dni/2026/m0101.html
            // Culmination at elevation 17.6 degrees at 2026/01/16 9:09 JST in Nemuro
            // In this elevation, atmospheric refraction would affect a few arcminutes.
            // But with allowed error of 0.05 degrees, this is not covered yet with single case.
            Instant moonCulmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(new Moon(), Instant.parse("2026-01-15T15:00:00Z"), LocationsForTest.getNemuroNAO());
            HorizontalCoordinatesFromGround coordinates = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(new Moon(), moonCulmination, LocationsForTest.getNemuroNAO());
            assertEquals(17.6, coordinates.calculateApparentElevationDeg(), 0.05);
        }
        {
            // https://eco.mtk.nao.ac.jp/koyomi/dni/2026/m0101.html
            Instant moonCulmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(new Moon(), Instant.parse("2026-01-28T15:00:00Z"), LocationsForTest.getNemuroNAO());
            HorizontalCoordinatesFromGround coordinates = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(new Moon(), moonCulmination, LocationsForTest.getNemuroNAO());
            assertEquals(74.7, coordinates.calculateApparentElevationDeg(), 0.05);
        }
        {
            // https://eco.mtk.nao.ac.jp/koyomi/dni/2026/m0102.html
            Instant moonCulmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(new Moon(), Instant.parse("2026-02-10T15:00:00Z"), LocationsForTest.getNemuroNAO());
            HorizontalCoordinatesFromGround coordinates = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(new Moon(), moonCulmination, LocationsForTest.getNemuroNAO());
            assertEquals(19.2, coordinates.calculateApparentElevationDeg(), 0.05);
        }
        {
            // https://eco.mtk.nao.ac.jp/koyomi/dni/2026/m0102.html
            Instant moonCulmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(new Moon(), Instant.parse("2026-02-11T15:00:00Z"), LocationsForTest.getNemuroNAO());
            HorizontalCoordinatesFromGround coordinates = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(new Moon(), moonCulmination, LocationsForTest.getNemuroNAO());
            assertEquals(17.7, coordinates.calculateApparentElevationDeg(), 0.05);
        }
        {
            // https://eco.mtk.nao.ac.jp/koyomi/dni/2026/m0102.html
            Instant moonCulmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(new Moon(), Instant.parse("2026-02-12T15:00:00Z"), LocationsForTest.getNemuroNAO());
            HorizontalCoordinatesFromGround coordinates = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(new Moon(), moonCulmination, LocationsForTest.getNemuroNAO());
            assertEquals(17.6, coordinates.calculateApparentElevationDeg(), 0.05);
        }
        {
            // https://eco.mtk.nao.ac.jp/koyomi/dni/2026/m0102.html
            Instant moonCulmination = AstronomicalEventsCalculation.calculateCulminationWithin24h(new Moon(), Instant.parse("2026-02-13T15:00:00Z"), LocationsForTest.getNemuroNAO());
            HorizontalCoordinatesFromGround coordinates = HorizontalCoordinatesFromGround.calculatePositionOfAstronomicalObject(new Moon(), moonCulmination, LocationsForTest.getNemuroNAO());
            assertEquals(18.9, coordinates.calculateApparentElevationDeg(), 0.05);
        }
    }

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
