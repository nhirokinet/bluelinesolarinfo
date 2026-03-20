package net.nhiroki.lib.bluelineastrolib.coordinates;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.earth.Earth;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;

public class HorizontalCoordinatesFromGround {
    private final AstronomicalObject astronomicalObject;
    private final Instant time;
    private final LocationOnTheEarth locationOnTheEarth;
    private final double azimuthRad;
    private final double actualElevationRad;

    private HorizontalCoordinatesFromGround(AstronomicalObject astronomicalObject, Instant time, LocationOnTheEarth locationOnTheEarth, double azimuthRad, double actualElevationRad) {
        this.astronomicalObject = astronomicalObject;
        this.time = time;
        this.locationOnTheEarth = locationOnTheEarth;
        this.azimuthRad = azimuthRad;
        this.actualElevationRad = actualElevationRad;
    }

    public static HorizontalCoordinatesFromGround fromCoordinatesFromTheCenterOfTheEarth(HorizontalCoordinatesFromTheCenterOfTheEarth horizontalCoordinatesFromTheCenterOfTheEarth,
                                                                                         AstronomicalObject astronomicalObject,
                                                                                         Instant time, LocationOnTheEarth locationOnTheEarth) throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        double actualElevationRad = horizontalCoordinatesFromTheCenterOfTheEarth.getElevationRad();
        if (Math.abs(actualElevationRad) < Math.PI * 0.4999999) {
            actualElevationRad = Math.atan(Math.tan(actualElevationRad) - Math.tan(astronomicalObject.calculateEquatorialHorizontalParallaxRad(time)) / Math.cos(actualElevationRad));
        }

        return new HorizontalCoordinatesFromGround(astronomicalObject, time, locationOnTheEarth, horizontalCoordinatesFromTheCenterOfTheEarth.getAzimuthRad(), actualElevationRad);
    }

    public static HorizontalCoordinatesFromGround calculatePositionOfAstronomicalObject(AstronomicalObject astronomicalObject, Instant time,
                                                                                        LocationOnTheEarth locationOnTheEarth)
            throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {

        HorizontalCoordinatesFromTheCenterOfTheEarth fromTheCenterOfTheEarth = HorizontalCoordinatesFromTheCenterOfTheEarth.ofAstronomicalObject(astronomicalObject, time, locationOnTheEarth);
        return fromCoordinatesFromTheCenterOfTheEarth(fromTheCenterOfTheEarth, astronomicalObject, time, locationOnTheEarth);
    }

    public double getAzimuthRad() {
        return azimuthRad;
    }

    public double getAzimuthDeg() {
        return Math.toDegrees(azimuthRad);
    }

    public double getActualElevationRad() {
        return actualElevationRad;
    }

    public double calculateApparentElevationRad() {
        return this.actualElevationRad + Earth.calculateAtmosphericRefractionRadFromActualElevationRad(this.actualElevationRad);
    }

    public double calculateApparentElevationDeg() {
        return Math.toDegrees(calculateApparentElevationRad());
    }

    public boolean isTopAboveHorizon() throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        // Earth.calculateAtmosphericRefractionRadFromActualElevationRad() at horizon and Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_ARCSEC differs a bit.
        // Here, for threshold, this library uses the constant value used for rise/set.
        double topActualRad = this.actualElevationRad + astronomicalObject.calculateApparentRadiusRad(time);
        double threshold = -Math.toRadians(Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_ARCSEC / 3600.0) - Earth.calculateAltitudeCorrectionOfHorizonRad(locationOnTheEarth.getElevationMeters());
        return topActualRad > threshold;
    }
}
