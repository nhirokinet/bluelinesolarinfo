package net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects;

import net.nhiroki.lib.bluelineastrolib.coordinates.CelestialCoordinatesWithRightAscension;
import net.nhiroki.lib.bluelineastrolib.coordinates.EclipticCoordinates;
import net.nhiroki.lib.bluelineastrolib.earth.Earth;
import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;


/**
 * The sun seen from the earth
 */
public class Sun implements AstronomicalObject {
    // https://en.wikipedia.org/wiki/Astronomical_unit
    public static final double AU_IN_KM = 149597870.7;

    // https://eco.mtk.nao.ac.jp/koyomi/faq/ephemeris.html
    // National Astronomical Observatory of Japan uses this value
    private static final double APPARENT_SEMI_DIAMETER_AT_1_AU_DEG_SEC = 16.0 * 60.0 + 1.18;


    @Override
    public CelestialCoordinatesWithRightAscension calculateCelestialCoordinates(Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        double eclipticTiltRad = Earth.calculateEclipticTiltRad(t);
        return CelestialCoordinatesWithRightAscension.fromEclipticLongitudeWithLatitudeZero(this.calculateEclipticLongitudeRad(t), eclipticTiltRad);
    }

    @Override
    public double estimatedIncrementOfRightAscensionRadPerDay(Instant t) {
        // Use estimated increment of ecliptic longitude from calculateEclipticLongitudeDeg()
        // as rough estimate
        // 0.98564736 / 180 * pi
        return 0.01720279169558985675;
    }

    @Override
    public double calculateEquatorialHorizontalParallaxRad (Instant t) {
        return Earth.calculateEquatorialHorizontalParallaxRadByDistanceAU(this.calculateDistanceFromTheEarthAU(t));
    }

    @Override
    public double calculateApparentRadiusRad (Instant t) {
        double distance = calculateDistanceFromTheEarthAU(t);
        return Math.toRadians(APPARENT_SEMI_DIAMETER_AT_1_AU_DEG_SEC / 3600.0 / distance);
    }

    public EclipticCoordinates calculateEclipticCoordinates(Instant t) {
        // This function assumes ecliptic latitude of sun is 0
        // https://en.wikipedia.org/wiki/Position_of_the_Sun says that the ecliptic latitude of the Sun is very small and never exceeds 0.00033 deg (a little over 1 arcsec).
        return EclipticCoordinates.ofRadians(this.calculateEclipticLongitudeRad(t), 0.0);
    }

    public double calculateEquationOfTimeSec(Instant t) throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        // https://aa.usno.navy.mil/faq/sun_approx as of 2026/03/02
        double D = new TimePointOnTheEarth(t).julianYearFromJ2000_0() * 365.25;
        double q = 280.459 + 0.98564736 * D;

        double eqDeg = q - Math.toDegrees(this.calculateRightAscensionRad(t));
        eqDeg -= 360.0 * Math.floor((eqDeg + 180.0) / 360.0);

        return eqDeg * 240.0;
    }

    public double calculateDistanceFromTheEarthKM(Instant t) {
        return this.calculateDistanceFromTheEarthAU(t) * Sun.AU_IN_KM;
    }

    public double calculateDistanceFromTheEarthAU(Instant t) {
        // https://aa.usno.navy.mil/faq/sun_approx as of 2024/02/29
        // Almost the same formula is found in https://en.wikipedia.org/wiki/Position_of_the_Sun , with 357.528 + 0.9856003n to calculate g
        // The Wikipedia page describes that the formula is from the Astronomical Almanac.
        // Both pages uses distance to the Sun to calculate the longitude of the Sun.
        // Looks like both pages does not mention precision about this formula itself, but US Navy page mentions the precision of the longitude calculated in 1800-2200, and the Wikipedia for 1950-2050.
        double D = new TimePointOnTheEarth(t).julianYearFromJ2000_0() * 365.25;

        double g = Math.toRadians(357.529 + 0.98560028 * D);
        return 1.00014 - 0.01671 * Math.cos(g) - 0.00014 * Math.cos(2.0 * g);
    }

    private double calculateEclipticLongitudeRad (Instant t) {
        return Math.toRadians(this.calculateEclipticLongitudeDeg(t));
    }

    private double calculateEclipticLongitudeDeg (Instant t) {
        // https://aa.usno.navy.mil/faq/sun_approx as of 2024/02/29
        // About precision, the page links to the file: https://aa.usno.navy.mil/graphics/sun_lonlat.pdf
        // The Wikipedia page describes that the formula is from the Astronomical Almanac.
        // Which looks like we can expect precision of roughly 30 arcsecs in 1950-2050, and roughly 50 arcsecs in 1800-2200 (both peak error is a little larger).
        //
        // Almost the same formula is found in https://en.wikipedia.org/wiki/Position_of_the_Sun , except that 0.9856474 instead of 0.98564736 is used to calculate q, 357.528 + 0.9856003n to calculate g
        // This formula is described as precision of 0.01 deg (36 arcsecs) between 1950 and 2050.
        double D = new TimePointOnTheEarth(t).julianYearFromJ2000_0() * 365.25;
        double g = Math.toRadians(357.529 + 0.98560028 * D);
        double q = 280.459 + 0.98564736 * D;
        double L = q + 1.915 * Math.sin(g) + 0.020 * Math.sin(2.0 * g);

        L -= 360.0 * Math.floor(L / 360.0);

        return L;
    }

    private double calculateRightAscensionRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        // This formula assumes ecliptic latitude of sun is 0
        // https://en.wikipedia.org/wiki/Position_of_the_Sun says that the ecliptic latitude of the Sun is very small and never exceeds 0.00033 deg (a little over 1 arcsec).
        double eclipticLongitudeRad = this.calculateEclipticLongitudeRad(t);
        eclipticLongitudeRad -= Math.floor(eclipticLongitudeRad / (2.0 * Math.PI)) * 2.0 * Math.PI;
        double ret = Math.atan(Math.tan(eclipticLongitudeRad) * Math.cos(Earth.calculateEclipticTiltRad(t)));
        if (eclipticLongitudeRad >= 0.5 * Math.PI && eclipticLongitudeRad < 1.5 * Math.PI) {
            ret += Math.PI;
        }
        if (ret < 0.0) {
            ret += 2 * Math.PI;
        }
        return ret;
    }
}
