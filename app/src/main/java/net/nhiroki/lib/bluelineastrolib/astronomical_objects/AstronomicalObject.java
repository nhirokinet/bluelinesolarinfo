package net.nhiroki.lib.bluelineastrolib.astronomical_objects;

import net.nhiroki.lib.bluelineastrolib.coordinates.CelestialCoordinatesWithRightAscension;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;


/**
 * Astronomical object to be used for calculation.
 */
public interface AstronomicalObject {
    /**
     * Calculates celestial coordinates of the corresponding astronomical object.
     * @param t Target time to calculate celestial coordinates
     * @return Computed celestial coordinates
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    CelestialCoordinatesWithRightAscension calculateCurrentCelestialCoordinates(Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Calculates right ascension of the corresponding astronomical object.
     *
     * @param t Target time used to calculate ecliptic tilt
     * @return Right ascension in radian
     */
    @Deprecated
    double calculateRightAscensionRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Returns estimation of how much the right ascension of this astronomical object increases in
     * a day.
     * The value is just a hint for estimation and may not be the exact one.
     * If the fixed star or unsure, implementation should return 0.0.
     *
     * @param t Target time
     * @return Estimated increment of hour angle per day in degrees
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    double estimatedIncrementOfRightAscensionRadPerDay(Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Calculates declination of the corresponding astronomical object.
     *
     * @param t Target time
     * @return Declination in radian
     */
    @Deprecated
    double calculateDeclinationRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Calculates horizontal parallax from the earth of the corresponding astronomical object.
     * This is for calculating rise/set of the object.
     *
     * @param t Target time
     * @return Equatorial horizontal parallax in radian
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    double calculateEquatorialHorizontalParallaxRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Calculates apparent radius from the earth of the corresponding astronomical object.
     *
     * @param t Target time
     * @return Apparent radius in radian
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    double calculateApparentRadiusRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;
}
