package net.nhiroki.lib.bluelineastrolib.coordinates;

import static java.lang.Double.NaN;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;

public class HorizontalCoordinatesFromTheCenterOfTheEarth {
    private final double azimuthRad;
    private final double elevationRad;

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

    public static HorizontalCoordinatesFromTheCenterOfTheEarth ofAstronomicalObject(AstronomicalObject astronomicalObject, Instant time,
                                                                                    LocationOnTheEarth locationOnTheEarth) throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        CelestialCoordinatesWithRightAscension celestialCoordinatesWithRightAscension = astronomicalObject.calculateCelestialCoordinates(time);
        CelestialCoordinatesWithHourAngle celestialCoordinatesWithHourAngle = CelestialCoordinatesWithHourAngle.fromCelestialCoordinatesWithRightAscension(celestialCoordinatesWithRightAscension, new TimePointOnTheEarth(time), locationOnTheEarth);

        return HorizontalCoordinatesFromTheCenterOfTheEarth.fromCelestialCoordinatesAndLocation(celestialCoordinatesWithHourAngle, locationOnTheEarth);
    }

    /**
     * Returns azimuth in radians. It may return NaN.
     *
     * @return azimuth in radians
     */
    public double getAzimuthRad() {
        return azimuthRad;
    }

    /**
     * Returns azimuth in degrees. It may return NaN.
     *
     * @return azimuth in degrees
     */
    public double getAzimuthDeg() {
        if (Double.isNaN(azimuthRad)) {
            return NaN;
        }
        return Math.toDegrees(azimuthRad);
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
