package net.nhiroki.lib.bluelineastrolib.coordinates;

import net.nhiroki.lib.bluelineastrolib.logic.CoordinatesCalculation;

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

    public static HorizontalCoordinatesFromTheCenterOfTheEarth fromCelestialCoordinatesAndLatitudeRad(CelestialCoordinatesWithHourAngle celestialCoordinatesWithHourAngle, double latitudeRad) {
        return HorizontalCoordinatesFromTheCenterOfTheEarth.ofRadians(
                CoordinatesCalculation.calculateAzimuthRadFromHourAngle(celestialCoordinatesWithHourAngle, latitudeRad),
                CoordinatesCalculation.calculateElevationRadFromHourAngle(celestialCoordinatesWithHourAngle, latitudeRad)
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
}
