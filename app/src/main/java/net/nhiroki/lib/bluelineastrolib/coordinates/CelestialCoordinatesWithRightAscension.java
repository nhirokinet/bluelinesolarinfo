package net.nhiroki.lib.bluelineastrolib.coordinates;

public class CelestialCoordinatesWithRightAscension {
    private final double rightAscensionRad;
    private final double declinationRad;


    public static CelestialCoordinatesWithRightAscension ofRadians(double rightAscensionRad, double declinationRad) {
        return new CelestialCoordinatesWithRightAscension(rightAscensionRad, declinationRad);
    }

    private CelestialCoordinatesWithRightAscension(double rightAscensionRad, double declinationRad) {
        this.rightAscensionRad = rightAscensionRad;
        this.declinationRad = declinationRad;
    }

    public double getRightAscensionRad() {
        return rightAscensionRad;
    }

    public double getDeclinationRad() {
        return declinationRad;
    }
}
