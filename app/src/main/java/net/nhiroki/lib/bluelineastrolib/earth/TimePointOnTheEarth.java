package net.nhiroki.lib.bluelineastrolib.earth;

import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;


/**
 * Indicate a time point on the earth.
 */
public class TimePointOnTheEarth {
    private static final Instant[] LEAP_SECOND_LIST = {
            Instant.parse("2017-01-01T00:00:00.000Z"),
            Instant.parse("2015-07-01T00:00:00.000Z"),
            Instant.parse("2012-07-01T00:00:00.000Z"),
            Instant.parse("2009-01-01T00:00:00.000Z"),
            Instant.parse("2006-01-01T00:00:00.000Z"),
            Instant.parse("1999-01-01T00:00:00.000Z"),
            Instant.parse("1997-07-01T00:00:00.000Z"),
            Instant.parse("1996-01-01T00:00:00.000Z"),
            Instant.parse("1994-07-01T00:00:00.000Z"),
            Instant.parse("1993-07-01T00:00:00.000Z"),
            Instant.parse("1992-07-01T00:00:00.000Z"),
            Instant.parse("1991-01-01T00:00:00.000Z"),
            Instant.parse("1990-01-01T00:00:00.000Z"),
            Instant.parse("1988-01-01T00:00:00.000Z"),
            Instant.parse("1985-07-01T00:00:00.000Z"),
            Instant.parse("1983-07-01T00:00:00.000Z"),
            Instant.parse("1982-07-01T00:00:00.000Z"),
            Instant.parse("1981-07-01T00:00:00.000Z"),
            Instant.parse("1980-01-01T00:00:00.000Z"),
            Instant.parse("1979-01-01T00:00:00.000Z"),
            Instant.parse("1978-01-01T00:00:00.000Z"),
            Instant.parse("1977-01-01T00:00:00.000Z"),
            Instant.parse("1976-01-01T00:00:00.000Z"),
            Instant.parse("1975-01-01T00:00:00.000Z"),
            Instant.parse("1974-01-01T00:00:00.000Z"),
            Instant.parse("1973-01-01T00:00:00.000Z"),
            Instant.parse("1972-07-01T00:00:00.000Z"),
    };
    private static final Instant INSTANT_2000_1_1_12Z = Instant.parse("2000-01-01T12:00:00.000Z");

    private final Instant instant;

    public TimePointOnTheEarth(Instant instant) {
        this.instant = instant;
    }

    /**
     * Returns julian year from time point J2000.0, which increases 1.0 per 365.25 days.
     * If you want julian century, just divide the result by 100.0.
     *
     * @return Julian year from J2000.0
     */
    public double julianYearFromJ2000_0 () {
        // TT (based on TAI) = TAI + 32.184s
        // TAI = UTC + leap seconds + 10s
        return (instant.toEpochMilli() - INSTANT_2000_1_1_12Z.toEpochMilli() + 1000.0 * countLeapSecondsBefore() + 42184.0) / 86400000.0 / 365.25;
    }

    public int countLeapSecondsBefore () {
        // Check from first because most calls near now
        for (int i = 0; i < LEAP_SECOND_LIST.length; ++i) {
            if (! LEAP_SECOND_LIST[i].isAfter(instant)) {
                return LEAP_SECOND_LIST.length - i;
            }
        }
        return 0;
    }

    public double calculateSiderealTimeRad (double longitudeRad) throws UnsupportedDateRangeException {
        return Math.toRadians(this.calculateSiderealTimeDeg(Math.toDegrees(longitudeRad)));
    }

    public double calculateSiderealTimeDeg (double longitudeDeg) throws UnsupportedDateRangeException{
        // Estimation method open to public by U.S. navy
        // https://aa.usno.navy.mil/faq/GAST
        // This page writes:
        // > The maximum error in GAST resulting from the use of these formulae over the period 2000-2100 is 0.432 seconds; the RMS error is 0.01512 seconds.
        // In this function, we don't handle D_UT properly and this will have error of 0.9s, so this is largest factor and total error is about 1.3s.
        double T = this.julianYearFromJ2000_0() / 100.0;
        double D_TT = T * 36525.0;
        // D_UT here should be the elapsed time in UT1 from 2000/01/01 12:00:00 UT1, but this.instant.toEpochMillis is UTC.
        // UTC and UT1 is kept to have absolute difference less than 0.9 in 2024, so
        // this has error of less than 0.9s.
        // After abandoning leap seconds, this different will be larger.
        double D_UT = (this.instant.toEpochMilli() - INSTANT_2000_1_1_12Z.toEpochMilli()) / 86400000.0;
        double D_UT_INT = Math.floor(D_UT - 0.5) + 0.5;
        double D_UT_TIMEOFDAY = D_UT - D_UT_INT;

        // Greenwich mean sidereal time
        double gsmtHours = 6.697375 + 0.065707485828 * D_UT_INT + 1.0027379 * 24.0 * D_UT_TIMEOFDAY + 0.0854103 * T + 0.0000258 * T * T;
        gsmtHours -= 24.0 * Math.floor(gsmtHours / 24.0);

        // the Longitude of the ascending node of the Moon
        // Not sure about the precision of this one itself, so for now using this value only here
        double omegaDeg = 125.04 - 0.052954 * D_TT;

        // the Mean Longitude of the Sun
        // Not sure about the precision of this one itself, so for now using this value only here
        double LDeg = 280.47 + 0.98565 * D_TT;

        // the obliquity
        // This library has another function to calculate this in class Sun, but this algorithm looks like verified by this algorithm,
        // so using this here.
        // Effect of the error of this is very small though.
        double epsilonDeg = 23.4393 - 0.0000004 * D_TT;

        // the nutation in longitude
        double deltaPsiHour = -0.000319 * Math.sin(Math.toRadians(omegaDeg)) - 0.000024 * Math.sin(Math.toRadians(2 * LDeg));

        // the equation of the equinoxes
        double eqeqHour = deltaPsiHour * Math.cos(Math.toRadians(epsilonDeg));

        double retVal = (gsmtHours + eqeqHour) / 24.0 * 360.0 + longitudeDeg;
        retVal -= 360.0 * Math.floor(retVal / 360.0);
        return retVal;
    }

    public double estimatedIncrementOfSiderealTimeRadPerDay () {
        // 1.0027379 * 2 * pi
        return 6.30038804023211344976;
    }
}
