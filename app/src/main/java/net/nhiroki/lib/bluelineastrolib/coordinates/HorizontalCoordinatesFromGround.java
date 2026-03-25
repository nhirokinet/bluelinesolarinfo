package net.nhiroki.lib.bluelineastrolib.coordinates;

import net.nhiroki.lib.bluelineastrolib.astronomicalobjects.AstronomicalObject;
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

    /**
     * Returns horizontal coordinate converted from {@code horizontalCoordinatesFromTheCenterOfTheEarth} by correcting the parallax by the radius of the earth.
     * @param horizontalCoordinatesFromTheCenterOfTheEarth Coordinates from the center of the earth
     * @param astronomicalObject Target astronomical object. Note that position of the astronomical object is not computed by this function. Some functions are used for converting and computing the elevation.
     * @param time Target time. Used for converting and computing the elevation.
     * @param locationOnTheEarth Location to be observed from. Used for converting and computing the elevation.
     * @return {@code HorizontalCoordinatesFromGround} object describing the horizontal coordinates from the ground
     */
    public static HorizontalCoordinatesFromGround fromCoordinatesFromTheCenterOfTheEarth(HorizontalCoordinatesFromTheCenterOfTheEarth horizontalCoordinatesFromTheCenterOfTheEarth,
                                                                                         AstronomicalObject astronomicalObject,
                                                                                         Instant time, LocationOnTheEarth locationOnTheEarth) throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        double actualElevationRad = horizontalCoordinatesFromTheCenterOfTheEarth.getElevationRad();
        if (Math.abs(actualElevationRad) < Math.PI * 0.4999999) {
            actualElevationRad = Math.atan(Math.tan(actualElevationRad) - Math.tan(astronomicalObject.calculateEquatorialHorizontalParallaxRad(time)) / Math.cos(actualElevationRad));
        }

        return new HorizontalCoordinatesFromGround(astronomicalObject, time, locationOnTheEarth, horizontalCoordinatesFromTheCenterOfTheEarth.getAzimuthRad(), actualElevationRad);
    }

    /**
     * Calculate horizontal coordinates of the {@code astronomicalObject} seen from the ground of {@code locationOnTheEarth} at {@code time}.
     *
     * @param astronomicalObject Target astronomical object
     * @param time Target time
     * @param locationOnTheEarth Location to be observed from
     * @return {@code HorizontalCoordinatesFromGround} object describing the horizontal coordinates from the ground
     */
    public static HorizontalCoordinatesFromGround calculatePositionOfAstronomicalObject(AstronomicalObject astronomicalObject, Instant time,
                                                                                        LocationOnTheEarth locationOnTheEarth)
            throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {

        HorizontalCoordinatesFromTheCenterOfTheEarth fromTheCenterOfTheEarth = HorizontalCoordinatesFromTheCenterOfTheEarth.ofAstronomicalObject(astronomicalObject, time, locationOnTheEarth);
        return fromCoordinatesFromTheCenterOfTheEarth(fromTheCenterOfTheEarth, astronomicalObject, time, locationOnTheEarth);
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
        return Math.toDegrees(azimuthRad);
    }

    public double getActualElevationRad() {
        return actualElevationRad;
    }

    /**
     * Returns apparent elevation in radians.<br>
     * <b>Note about the case of below the horizon:</b> This function returns some value even if below the horizon, but in such cases, you should be careful about the meaning of the returned value, as there is no "apparent" elevation and it includes the atmospheric refraction which does not exist actually.<br>
     * @return Apparent elevation in radians
     */
    public double calculateApparentElevationRad() {
        return this.actualElevationRad + Earth.calculateAtmosphericRefractionRadFromActualElevationRad(this.actualElevationRad);
    }

    /**
     * Returns apparent elevation in degrees.<br>
     * <b>Note about the case of below the horizon:</b> This function returns some value even if below the horizon, but in such cases, you should be careful about the meaning of the returned value, as there is no "apparent" elevation and it includes the atmospheric refraction which does not exist actually.<br>
     * @return Apparent elevation in degrees
     */
    public double calculateApparentElevationDeg() {
        return Math.toDegrees(calculateApparentElevationRad());
    }

    /**
     * Returns whether the top of the astronomical object is above the horizon. If this function returns {@code false}, the astronomical object should be invisible.<br>
     * <br>
     * <b>Note about threshold:</b><br>
     * Earth.calculateAtmosphericRefractionRadFromActualElevationRad() at horizon and Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_ARCSEC differs a bit.<br>
     * Here, this function uses the constant value used for rise/set.<br>
     * This would make the result consistent as total, but you may see the {@code calculateApparentElevationDeg} returns negative value and still this functions return {@code true} if near the horizon.
     * @return {@code true} if the top of the astronomical object is above the horizon, {@code false} otherwise
     */
    public boolean isTopAboveHorizon() throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        double topActualRad = this.actualElevationRad + astronomicalObject.calculateApparentRadiusRad(time);
        double threshold = -Math.toRadians(Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_ARCSEC / 3600.0) - Earth.calculateAltitudeCorrectionOfHorizonRad(locationOnTheEarth.getElevationMeters());
        return topActualRad > threshold;
    }
}
