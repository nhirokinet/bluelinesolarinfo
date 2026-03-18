package net.nhiroki.lib.bluelineastrolib.coordinates;

public class CelestialCoordinatesWithHourAngle {
    private final double hourAngle;
    private final double declinationRad;


    public static CelestialCoordinatesWithHourAngle ofRadians(double hourAngle, double declinationRad) {
        return new CelestialCoordinatesWithHourAngle(hourAngle, declinationRad);
    }

    private CelestialCoordinatesWithHourAngle(double hourAngle, double declinationRad) {
        this.hourAngle = hourAngle;
        this.declinationRad = declinationRad;
    }

    public double getHourAngle() {
        return hourAngle;
    }

    public double getDeclinationRad() {
        return declinationRad;
    }
}
