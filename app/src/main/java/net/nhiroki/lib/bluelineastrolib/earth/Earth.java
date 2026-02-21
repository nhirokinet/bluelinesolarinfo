package net.nhiroki.lib.bluelineastrolib.earth;

import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;


public class Earth {
    // https://eco.mtk.nao.ac.jp/koyomi/wiki/C2E7B5A42FB6FEC0DE.html
    // National Astronomical Observatory of Japan uses this value
    public static final double ATMOSPHERIC_REFRACTION_AT_HORIZON_DEC_SEC = 35.0 * 60.0 + 8.0;

    // 8.794148 +/- 0.000007 adopted by IAU in 1976 looks like used frequently
    private static final double MEAN_EQUATORIAL_HORIZONTAL_PARALLAX_AT_1_AU_SUN_DEG_SEC = 8.794148;


    public static double calculateAltitudeCorrectionOfHorizonRad (double elevationMeters) {
        // I could not find a constant that is commonly used,
        // but use sqrt(H in m) * 2.076' for now,
        // because it appears in explanation in the web page of
        // The National Radio Astronomy Observatory.
        // Looks like 2.11' and 2.12' are also used.
        // https://public.nrao.edu/ask/altitude-correction-for-time-of-sunrise-and-sunset/
        // https://en.wikipedia.org/wiki/Sunrise_equation
        // https://www.nao.ac.jp/contents/about-naoj/reports/report-naoj/p91.pdf
        return Math.sqrt(elevationMeters) * Math.toRadians(2.076 / 60.0);
    }

    public static double calculateEclipticTiltDeg (Instant instant) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        // https://en.wikipedia.org/wiki/Axial_tilt as of 2024/02/26
        // > J. Laskar computed an expression to order T10 good to 0.02″ over 1000 years and several arcseconds over 10,000 years.

        double t = new TimePointOnTheEarth(instant).julianYearFromJ2000_0() / 10000.0;
        return 23.0 + 26.0 / 60.0 + 21.448 / 3600.0
                - 4680.93 / 3600.0 * t
                - 1.55 / 3600.0 * t * t
                + 1999.25 / 3600.0 * t * t * t
                - 51.38 / 3600.0 * t * t * t * t
                - 249.67 / 3600.0 * t * t * t * t * t
                - 39.05 / 3600.0 * t * t * t * t * t * t
                + 7.12 / 3600.0 * t * t * t * t * t * t * t
                + 27.87 / 3600.0 * t * t * t * t * t * t * t * t
                + 5.79 / 3600.0 * t * t * t * t * t * t * t * t * t
                + 2.45 / 3600.0 * t * t * t * t * t * t * t * t * t * t;
    }

    public static double calculateEclipticTiltRad (Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        return Math.toRadians(calculateEclipticTiltDeg(t));
    }

    public static double calculateEquatorialHorizontalParallaxRadByDistanceAU (double distanceAU) {
        return Math.toRadians(Earth.MEAN_EQUATORIAL_HORIZONTAL_PARALLAX_AT_1_AU_SUN_DEG_SEC / 3600.0 / distanceAU);
    }

    public static double calculateDistanceAUByEquatorialHorizontalParallaxRad (double parallaxRad) {
        return Math.toRadians(Earth.MEAN_EQUATORIAL_HORIZONTAL_PARALLAX_AT_1_AU_SUN_DEG_SEC / 3600.0) / parallaxRad;
    }
}
