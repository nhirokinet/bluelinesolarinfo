package net.nhiroki.lib.bluelineastrolib.logic;

import static java.lang.Double.NaN;


public class CoordinateConversion {
    public static double calculateHourAngleCrossingHeightRad (double heightRad, double declinationRad, double latitudeRad) {
        double denominator = Math.cos(declinationRad) * Math.cos(latitudeRad);
        double numerator = Math.sin(heightRad) - Math.sin(declinationRad) * Math.sin(latitudeRad);

        if (Math.abs(denominator) < Math.abs(numerator)) {
            return NaN;
        }

        return Math.acos(numerator / denominator);
    }

    public static double calculateHeightRadFromHourAngle (double hourAngleRad, double declinationRad, double latitudeRad) {
        return Math.asin(Math.sin(declinationRad) * Math.sin(latitudeRad) + Math.cos(declinationRad) * Math.cos(latitudeRad) * Math.cos(hourAngleRad));
    }

    public static double calculateAngleRadFromHourAngle (double hourAngleRad, double declinationRad, double latitudeRad) {
        double numerator = - Math.cos(declinationRad) * Math.sin(hourAngleRad);
        double denominator = Math.sin(declinationRad) * Math.cos(latitudeRad) - Math.cos(declinationRad) * Math.sin(latitudeRad) * Math.cos(hourAngleRad);

        if (Math.abs(denominator) < 1e-20) {
            // abs(tan(ret)) ~ 10^20 or infinite
            double sint = Math.sin(hourAngleRad);
            if (sint == 0.0) {
                // height = 90DEG
                return NaN;
            } else if (sint > 0.0) {
                return 1.5 * Math.PI;
            } else {
                return 0.5 * Math.PI;
            }
        }

        double ret = Math.atan(numerator / denominator);

        if (denominator < 0.0) {
            ret += Math.PI;
        }

        if (ret < 0.0) {
            ret += 2.0 * Math.PI;
        }

        return ret;
    }
}
