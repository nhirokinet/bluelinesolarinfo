package net.nhiroki.lib.bluelineastrolib.coordinates;

import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

public class CelestialCoordinatesWithHourAngle {
    private final double hourAngleRad;
    private final double declinationRad;


    public static CelestialCoordinatesWithHourAngle ofRadians(double hourAngle, double declinationRad) {
        return new CelestialCoordinatesWithHourAngle(hourAngle, declinationRad);
    }

    public static CelestialCoordinatesWithHourAngle fromCelestialCoordinatesWithRightAscension(CelestialCoordinatesWithRightAscension celestialCoordinatesWithRightAscension,
                                                                                               TimePointOnTheEarth timePointOnTheEarth,
                                                                                               LocationOnTheEarth locationOnTheEarth) throws UnsupportedDateRangeException {
        double hourAngleRad = timePointOnTheEarth.calculateSiderealTimeRad(locationOnTheEarth.getLongitudeRad()) - celestialCoordinatesWithRightAscension.getRightAscensionRad();
        return new CelestialCoordinatesWithHourAngle(hourAngleRad, celestialCoordinatesWithRightAscension.getDeclinationRad());
    }

    private CelestialCoordinatesWithHourAngle(double hourAngleRad, double declinationRad) {
        this.hourAngleRad = hourAngleRad;
        this.declinationRad = declinationRad;
    }

    public double getHourAngleRad() {
        return hourAngleRad;
    }

    public double getDeclinationRad() {
        return declinationRad;
    }
}
