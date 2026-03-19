package net.nhiroki.lib.bluelineastrolib.coordinates;

public class CelestialCoordinatesWithRightAscension {
    private final double rightAscensionRad;
    private final double declinationRad;


    public static CelestialCoordinatesWithRightAscension ofRadians(double rightAscensionRad, double declinationRad) {
        return new CelestialCoordinatesWithRightAscension(rightAscensionRad, declinationRad);
    }

    public static CelestialCoordinatesWithRightAscension fromEclipticCoordinates(EclipticCoordinates eclipticCoordinates, double eclipticTiltRad) {
        double longitude = eclipticCoordinates.getLongitudeRad();
        double latitude = eclipticCoordinates.getLatitudeRad();

        double U = Math.cos(latitude) * Math.cos(longitude);
        double V = -Math.sin(latitude) * Math.sin(eclipticTiltRad) + Math.cos(latitude) * Math.sin(longitude) * Math.cos(eclipticTiltRad);
        double W = Math.sin(latitude) * Math.cos(eclipticTiltRad) + Math.cos(latitude) * Math.sin(longitude) * Math.sin(eclipticTiltRad);

        double rightAscentionRad;

        if (Math.abs(U) < 1e-20) {
            if (U > 0.0) {
                if (V > 0.0) {
                    rightAscentionRad = Math.PI / 2.0;
                } else {
                    rightAscentionRad = - Math.PI / 2.0;
                }
            } else {
                if (V > 0.0) {
                    rightAscentionRad = - Math.PI / 2.0;
                } else {
                    rightAscentionRad = Math.PI / 2.0;
                }
            }
        } else {
            rightAscentionRad = Math.atan(V / U);
            if (U < 0.0) {
                rightAscentionRad += Math.PI;
            }
            rightAscentionRad -= 2.0 * Math.PI * Math.floor(rightAscentionRad / (2.0 * Math.PI));
        }

        double declinationRad = Math.atan(W / Math.sqrt(U * U + V * V));

        return CelestialCoordinatesWithRightAscension.ofRadians(rightAscentionRad, declinationRad);
    }

    public static CelestialCoordinatesWithRightAscension fromEclipticWithLatitudeZero(double eclipticLongitudeRad, double eclipticTiltRad) {
        eclipticLongitudeRad -= Math.floor(eclipticLongitudeRad / (2.0 * Math.PI)) * 2.0 * Math.PI;
        double rightAscension = Math.atan(Math.tan(eclipticLongitudeRad) * Math.cos(eclipticTiltRad));
        if (eclipticLongitudeRad >= 0.5 * Math.PI && eclipticLongitudeRad < 1.5 * Math.PI) {
            rightAscension += Math.PI;
        }
        if (rightAscension < 0.0) {
            rightAscension += 2.0 * Math.PI;
        }
        double declination = Math.asin(Math.sin(eclipticLongitudeRad) * Math.sin(eclipticTiltRad));
        return CelestialCoordinatesWithRightAscension.ofRadians(rightAscension, declination);
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
