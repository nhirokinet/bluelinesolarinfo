package net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects;

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
    public double calculateRightAscensionRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
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

    @Override
    public double calculateDeclinationRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        final double eclipticLongitudeRad = this.calculateEclipticLongitudeRad(t);
        return Math.asin(Math.sin(eclipticLongitudeRad) * Math.sin(Earth.calculateEclipticTiltRad(t)));
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

    public double calculateDistanceFromTheEarthAU(Instant t) {
        // https://aa.usno.navy.mil/faq/sun_approx as of 2024/02/29
        // TODO: check precision
        double D = new TimePointOnTheEarth(t).julianYearFromJ2000_0() * 365.25;

        double g = Math.toRadians(357.529 + 0.98560028 * D);
        return 1.00014 - 0.01671 * Math.cos(g) - 0.00014 * Math.cos(2.0 * g);
    }

    public double calculateDistanceFromTheEarthKM(Instant t) {
        return this.calculateDistanceFromTheEarthAU(t) * Sun.AU_IN_KM;
    }

    private double calculateEclipticLongitudeRad (Instant t) {
        return Math.toRadians(this.calculateEclipticLongitudeDeg(t));
    }

    public double calculateEclipticLongitudeDeg (Instant t) {
        // https://aa.usno.navy.mil/faq/sun_approx as of 2024/02/29
        // TODO: check precision
        double D = new TimePointOnTheEarth(t).julianYearFromJ2000_0() * 365.25;
        double g = Math.toRadians(357.529 + 0.98560028 * D);
        double q = 280.459 + 0.98564736 * D;
        double L = q + 1.915 * Math.sin(g) + 0.020 * Math.sin(2.0 * g);

        L -= 360.0 * Math.floor(L / 360.0);

        return L;
    }

    @Override
    public double estimatedIncrementOfRightAscensionRadPerDay(Instant t) {
        // Use estimated increment of ecliptic longitude from calculateEclipticLongitudeDeg()
        // as rough estimate
        //0.98564736 / 180 * pi
        return 0.01720279169558985675;
    }
}
