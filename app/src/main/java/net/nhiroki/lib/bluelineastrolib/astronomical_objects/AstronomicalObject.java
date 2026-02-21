package net.nhiroki.lib.bluelineastrolib.astronomical_objects;

import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;


/**
 * Astronomical object to be used for calculation.
 */
public interface AstronomicalObject {
    /**
     * Calculates right ascension of the corresponding astronomical object.
     *
     * @param t Target time used to calculate ecliptic tilt
     * @return Right ascension in radian
     */
    double calculateRightAscensionRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Returns estimation of how much the right ascension of this astronomical object increases in
     * a day.
     * The value is just a hint for estimation and may not be the exact one.
     * If the fixed star or unsure, implementation should return 0.0.
     *
     * @param t Target time
     * @return Estimated increment of hour angle per day in degrees
     */
    double estimatedIncrementOfRightAscensionRadPerDay(Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Calculates declination of the corresponding astronomical object.
     *
     * @param t Target time
     * @return Declination in radian
     */
    double calculateDeclinationRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Calculates horizontal parallax from the earth of the corresponding astronomical object.
     * This is for calculating rise/set of the object.
     *
     * @param t Target time
     * @return Equatorial horizontal parallax in radian
     */
    double calculateEquatorialHorizontalParallaxRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;

    /**
     * Calculates apparent radius from the earth of the corresponding astronomical object.
     *
     * @param t Target time
     * @return Apparent radius in radian
     */
    double calculateApparentRadiusRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException;
}
