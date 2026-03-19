package net.nhiroki.lib.bluelineastrolib.logic;

import static java.lang.Double.NaN;

import net.nhiroki.lib.bluelineastrolib.coordinates.CelestialCoordinatesWithHourAngle;
import net.nhiroki.lib.bluelineastrolib.coordinates.HorizontalCoordinatesFromTheCenterOfTheEarth;


public class CoordinateConversion {
    public static HorizontalCoordinatesFromTheCenterOfTheEarth calculateHorizontalCoordinatesFromTheCenterOfTheEarth(CelestialCoordinatesWithHourAngle celestialCoordinatesWithHourAngle, double latitudeRad) {
        return HorizontalCoordinatesFromTheCenterOfTheEarth.ofRadians(
                calculateAzimuthRadFromHourAngle(celestialCoordinatesWithHourAngle, latitudeRad),
                calculateElevationRadFromHourAngle(celestialCoordinatesWithHourAngle, latitudeRad)
        );
    }

    public static double calculateHourAngleCrossingHeightRad (double heightRad, double declinationRad, double latitudeRad) {
        double denominator = Math.cos(declinationRad) * Math.cos(latitudeRad);
        double numerator = Math.sin(heightRad) - Math.sin(declinationRad) * Math.sin(latitudeRad);

        if (Math.abs(denominator) < Math.abs(numerator)) {
            return NaN;
        }

        return Math.acos(numerator / denominator);
    }

    @Deprecated
    public static double calculateElevationRadFromHourAngle(CelestialCoordinatesWithHourAngle coordinates, double latitudeRad) {
        return Math.asin(Math.sin(coordinates.getDeclinationRad()) * Math.sin(latitudeRad) + Math.cos(coordinates.getDeclinationRad()) * Math.cos(latitudeRad) * Math.cos(coordinates.getHourAngleRad()));
    }

    // May return NaN
    @Deprecated
    public static double calculateAzimuthRadFromHourAngle(CelestialCoordinatesWithHourAngle coordinates, double latitudeRad) {
        double hourAngleRad = coordinates.getHourAngleRad();
        double declinationRad = coordinates.getDeclinationRad();

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
