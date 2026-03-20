package net.nhiroki.lib.bluelineastrolib.logic;

import static java.lang.Double.NaN;


public class CoordinatesCalculation {
    public static double calculateHourAngleRadCrossingElevationRad(double elevationRad, double declinationRad, double latitudeRad) {
        double denominator = Math.cos(declinationRad) * Math.cos(latitudeRad);
        double numerator = Math.sin(elevationRad) - Math.sin(declinationRad) * Math.sin(latitudeRad);

        if (Math.abs(denominator) < Math.abs(numerator)) {
            return NaN;
        }

        return Math.acos(numerator / denominator);
    }
}
