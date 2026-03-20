package net.nhiroki.lib.bluelineastrolib.coordinates;

import static java.lang.Double.NaN;

public class HorizontalCoordinatesFromTheCenterOfTheEarth {
    private double azimuthRad;
    private double elevationRad;

    private HorizontalCoordinatesFromTheCenterOfTheEarth(double azimuthRad, double elevationRad) {
        this.azimuthRad = azimuthRad;
        this.elevationRad = elevationRad;
    }

    public static HorizontalCoordinatesFromTheCenterOfTheEarth ofRadians(double azimuthRad, double elevationRad) {
        return new HorizontalCoordinatesFromTheCenterOfTheEarth(azimuthRad, elevationRad);
    }

    public static HorizontalCoordinatesFromTheCenterOfTheEarth fromCelestialCoordinatesAndLocation(CelestialCoordinatesWithHourAngle celestialCoordinatesWithHourAngle, LocationOnTheEarth locationOnTheEarth) {
        return HorizontalCoordinatesFromTheCenterOfTheEarth.ofRadians(
                calculateAzimuthRadFromHourAngle(celestialCoordinatesWithHourAngle, locationOnTheEarth.getLatitudeRad()),
                calculateElevationRadFromHourAngle(celestialCoordinatesWithHourAngle, locationOnTheEarth.getLatitudeRad())
        );
    }

    /**
     * Get azimuth in radians. May return NaN.
     *
     * @return azimuth in radians
     */
    public double getAzimuthRad() {
        return azimuthRad;
    }

    public double getElevationRad() {

        return elevationRad;
    }

    private static double calculateElevationRadFromHourAngle(CelestialCoordinatesWithHourAngle coordinates, double latitudeRad) {
        return Math.asin(Math.sin(coordinates.getDeclinationRad()) * Math.sin(latitudeRad) + Math.cos(coordinates.getDeclinationRad()) * Math.cos(latitudeRad) * Math.cos(coordinates.getHourAngleRad()));
    }

    // May return NaN
    private static double calculateAzimuthRadFromHourAngle(CelestialCoordinatesWithHourAngle coordinates, double latitudeRad) {
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
