package net.nhiroki.lib.bluelineastrolib.coordinates;

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

    public double getAzimuthRad() {
        return azimuthRad;
    }

    public double getElevationRad() {
        return elevationRad;
    }
}
