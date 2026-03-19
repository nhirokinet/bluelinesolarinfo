package net.nhiroki.lib.bluelineastrolib.coordinates;

public class EclipticCoordinates {
    private final double longitudeRad;
    private final double latitudeRad;

    private EclipticCoordinates(double longitudeRad, double latitudeRad) {
        this.longitudeRad = longitudeRad;
        this.latitudeRad = latitudeRad;
    }

    public static EclipticCoordinates ofRadians(double longitudeRad, double latitudeRad) {
        return new EclipticCoordinates(longitudeRad, latitudeRad);
    }

    public double getLongitudeDeg() {
        return Math.toDegrees(longitudeRad);
    }

    public double getLongitudeRad() {
        return longitudeRad;
    }

    public double getLatitudeDeg() {
        return Math.toDegrees(latitudeRad);
    }

    public double getLatitudeRad() {
        return latitudeRad;
    }
}
