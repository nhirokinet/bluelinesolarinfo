package net.nhiroki.lib.bluelineastrolib.logic;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.earth.Earth;
import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;
import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;


public class AstronomicalObjectCalculator {
    public enum EventDirectionType { RISE, CULMINATION, SET };
    public enum ReferencePoint { TOP, CENTER, BOTTOM };
    public enum ViewPoint { CENTER_OF_THE_EARTH, GROUND };
    public enum ElevationType { APPARENT, ACTUAL };


    public static double calculateAzimuthRad(AstronomicalObject astronomicalObject, Instant time,
                                             LocationOnTheEarth locationOnTheEarth) throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        double hourAngle = new TimePointOnTheEarth(time).calculateSiderealTimeRad(locationOnTheEarth.getLongitudeRad()) - astronomicalObject.calculateRightAscensionRad(time);
        double declination = astronomicalObject.calculateDeclinationRad(time);

        return CoordinateConversion.calculateAzimuthRadFromHourAngle(hourAngle, declination, locationOnTheEarth.getLatitudeRad());
    }

    public static double calculateElevationRad(AstronomicalObject astronomicalObject, Instant time,
                                               LocationOnTheEarth locationOnTheEarth, ViewPoint viewPoint, ElevationType elevationType) throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        double hourAngle = new TimePointOnTheEarth(time).calculateSiderealTimeRad(locationOnTheEarth.getLongitudeRad()) - astronomicalObject.calculateRightAscensionRad(time);
        double declination = astronomicalObject.calculateDeclinationRad(time);

        double ret = CoordinateConversion.calculateElevationRadFromHourAngle(hourAngle, declination, locationOnTheEarth.getLatitudeRad());
        if (viewPoint == ViewPoint.GROUND) {
            if (Math.abs(ret) < Math.PI * 0.4999999) {
                ret = Math.atan(Math.tan(ret) - Math.tan(astronomicalObject.calculateEquatorialHorizontalParallaxRad(time)) / Math.cos(ret));
            }
        }
        if (elevationType == ElevationType.APPARENT) {
            ret += Earth.calculateAtmosphericRefractionRadFromActualElevationRad(ret);
        }
        return ret;
    }

    public static double calculateThresholdElevationRadForRiseSet(AstronomicalObject astronomicalObject, Instant time,
                                                                  LocationOnTheEarth locationOnTheEarth, boolean horizonByElevation,
                                                                  ReferencePoint referencePoint) throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {

        int posReference = 0;
        if (referencePoint == ReferencePoint.TOP) {
            posReference = 1;
        } else if (referencePoint == ReferencePoint.CENTER) {
            posReference = 0;
        } else if (referencePoint == ReferencePoint.BOTTOM) {
            posReference = -1;
        }

        double heightMeter = horizonByElevation ? locationOnTheEarth.getElevationMeters() : 0.0;
        return calculateActualCenterHeightRad(time, heightMeter, astronomicalObject, posReference, true,  -Math.toRadians(Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_DEC_SEC / 3600.0));
    }

    /**
     * Return time of rise of @estronomialObject from within 24 hours.<br>
     * <br>
     * If not happens within 24 hours, returns null.<br>
     * If it happens multiple times, it is not guaranteed which is returned.<br>
     *
     * This function assumes that:<br>
     * <ul>
     *   <li>@astronomicalObject does not behave so dramatically in the equatorial coordinate system. Sun goes around once in a year, and moon in about a month, they are OK.</li>
     *   <li>Event is expected to happen about once in 24 hours.</li>
     * </ul>
     *
     * @param astronomicalObject Target astronomical object
     * @param start              Start point of calculation
     * @param locationOnTheEarth Target location
     * @param horizonByElevation If true, horizon with regard of height is standard of rise/set. If false, horizon is handled as 0 degree height.
     * @param referencePoint     Which rim or center to refer
     * @return Rise, or null if not within 24 hours
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    public static Instant calculateRiseWithin24h(AstronomicalObject astronomicalObject, Instant start,
                                                 LocationOnTheEarth locationOnTheEarth, boolean horizonByElevation,
                                                 ReferencePoint referencePoint) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        return calculateEventWithin24h(astronomicalObject, EventDirectionType.RISE, start, locationOnTheEarth, horizonByElevation, referencePoint,
                true, -Math.toRadians(Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_DEC_SEC / 3600.0)
        );
    }

    /**
     * Return time of set of @estronomialObject from within 24 hours.<br>
     * <br>
     * If not happens within 24 hours, returns null.<br>
     * If it happens multiple times, it is not guaranteed which is returned.<br>
     *
     * This function assumes that:<br>
     * <ul>
     *   <li>@astronomicalObject does not behave so dramatically in the equatorial coordinate system. Sun goes around once in a year, and moon in about a month, they are OK.</li>
     *   <li>Event is expected to happen about once in 24 hours.</li>
     * </ul>
     *
     * @param astronomicalObject Target astronomical object
     * @param start              Start point of calculation
     * @param locationOnTheEarth Target location
     * @param horizonByElevation If true, horizon with regard of height is standard of rise/set. If false, horizon is handled as 0 degree height.
     * @param referencePoint     Which rim or center to refer
     * @return Set, or null if not within 24 hours
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    public static Instant calculateSetWithin24h(AstronomicalObject astronomicalObject, Instant start,
                                                LocationOnTheEarth locationOnTheEarth, boolean horizonByElevation,
                                                ReferencePoint referencePoint) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        return calculateEventWithin24h(astronomicalObject, EventDirectionType.SET, start, locationOnTheEarth, horizonByElevation, referencePoint,
                true, -Math.toRadians(Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_DEC_SEC / 3600.0)
        );
    }

    /**
     * Return time of culmination of @estronomialObject from within 24 hours.<br>
     * <br>
     * If not happens within 24 hours, returns null.<br>
     * If it happens multiple times, it is not guaranteed which is returned.<br>
     *
     * This function assumes that:<br>
     * <ul>
     *   <li>@astronomicalObject does not behave so dramatically in the equatorial coordinate system. Sun goes around once in a year, and moon in about a month, they are OK.</li>
     *   <li>Event is expected to happen about once in 24 hours.</li>
     * </ul>
     *
     * @param astronomicalObject Target astronomical object
     * @param start              Start point of calculation
     * @param locationOnTheEarth Target location
     * @return Culmination, or null if not within 24 hours
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    public static Instant calculateCulminationWithin24h(AstronomicalObject astronomicalObject, Instant start,
                                                        LocationOnTheEarth locationOnTheEarth) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        return calculateEventWithin24h(astronomicalObject, EventDirectionType.CULMINATION, start, locationOnTheEarth, false, ReferencePoint.CENTER,
                true, 0.0
        );
    }

    /**
     * Return time of the event @from within 24 hours.<br>
     * <br>
     * If not happens within 24 hours, returns null.<br>
     * If it happens multiple times, it is not guaranteed which is returned.<br>
     *
     * This function assumes that:<br>
     * <ul>
     *   <li>@astronomicalObject does not behave so dramatically in the equatorial coordinate system. Sun goes around once in a year, and moon in about a month, they are OK.</li>
     *   <li>Event is expected to happen about once in 24 hours.</li>
     * </ul>
     *
     * @param astronomicalObject Target astronomical object
     * @param eventDirectionType Rise, set, or culmination
     * @param start Start point of calculation
     * @param locationOnTheEarth Target location
     * @param horizonByElevation If set true, horizon is a bit below the horizontal 0 degrees, with considering horizontal of @locationOnTheEarth .
     * @param referencePoint Which point (top/center/bottom) of the @astronomicalObject should be the reference
     * @param considerEquatorialHorizontalParallax Whether equatorial horizontal parallax should be considered into the calculation. Set true if unsure.
     * @param heightStandardRad The standard height to be considered as rise/set, including refraction of the air.
     * @return The time of the event, or null if not within 24 hours
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    private static Instant calculateEventWithin24h(final AstronomicalObject astronomicalObject,
                                                   final EventDirectionType eventDirectionType,
                                                   final Instant start,
                                                   final LocationOnTheEarth locationOnTheEarth,
                                                   final boolean horizonByElevation, final ReferencePoint referencePoint,
                                                   final boolean considerEquatorialHorizontalParallax,
                                                   final double heightStandardRad) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        final Instant end = start.plusSeconds(86400);
        final double heightMeter = horizonByElevation ? locationOnTheEarth.getElevationMeters() : 0.0;

        int posReference = 0;
        if (referencePoint == ReferencePoint.TOP) {
            posReference = 1;
        } else if (referencePoint == ReferencePoint.CENTER) {
            posReference = 0;
        } else if (referencePoint == ReferencePoint.BOTTOM) {
            posReference = -1;
        }
        int signOfHourAngle = 0;
        if (eventDirectionType == EventDirectionType.RISE) {
            signOfHourAngle = -1;
        } else if (eventDirectionType == EventDirectionType.CULMINATION) {
            signOfHourAngle = 0;
        } else if (eventDirectionType == EventDirectionType.SET) {
            signOfHourAngle = 1;
        }

        Instant estimate = start.plusSeconds(43200);
        boolean ok = false;
        int loopCount = 0;
        while (! ok) {
            Instant estimateAtStartOfThisLoop = estimate;

            if (++loopCount > 30) {
                // Falling back to slow path
                break;
            }

            double targetHourAngle;
            if (eventDirectionType == EventDirectionType.CULMINATION) {
                targetHourAngle = 0.0;
            } else {
                targetHourAngle = CoordinateConversion.calculateHourAngleCrossingHeightRad(
                        calculateActualCenterHeightRad(estimate, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad),
                        astronomicalObject.calculateDeclinationRad(estimate),
                        locationOnTheEarth.getLatitudeRad()
                ) * signOfHourAngle;
            }

            if (Double.isNaN(targetHourAngle)) {
                // There are cases:
                //    Case 1: The slight change of declination on this day does not affect the existence of the event.
                //      This means this function must return null
                //    Case 2: The slight change of declination on this day affects the existence of the event.
                //      Case 2.1: The period that the line with the same declination as the target star cross the horizon continues at least 24 hrs.
                //        This case start or end has expected value.
                //      Case 2.2: The period that the line with the same declination as the target star cross the horizon continues less than 24 hrs.
                //        It happens in polar area.
                //        In this case, the line goes from below to above, or above from below, the horizon.
                //        I think crossing and going back does not happen, but not sure.

                // Covering case 2.2
                double startJudgeHeight = calculateHeightRad(start, locationOnTheEarth, astronomicalObject) - calculateActualCenterHeightRad(start, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad);
                double endJudgeHeight = calculateHeightRad(end, locationOnTheEarth, astronomicalObject) - calculateActualCenterHeightRad(end, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad);
                if (startJudgeHeight < 0.0 && endJudgeHeight > 0.0 && eventDirectionType == EventDirectionType.RISE) {
                    Instant hi = end;
                    Instant lo = start;
                    while (Duration.between(lo, hi).toMillis() > 400) {
                        Instant mid = lo.plus(Duration.between(lo, hi).dividedBy(2));
                        double currentJudgeHeight = calculateHeightRad(mid, locationOnTheEarth, astronomicalObject) - calculateActualCenterHeightRad(mid, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad);
                        if (currentJudgeHeight > 0.0) {
                            hi = mid;
                        } else {
                            lo = mid;
                        }
                    }
                    return lo.plus(Duration.between(lo, hi).dividedBy(2));
                }
                if (startJudgeHeight > 0.0 && endJudgeHeight < 0.0 && eventDirectionType == EventDirectionType.SET) {
                    Instant hi = end;
                    Instant lo = start;
                    while (Duration.between(lo, hi).toMillis() > 400) {
                        Instant mid = lo.plus(Duration.between(lo, hi).dividedBy(2));
                        double currentJudgeHeight = calculateHeightRad(mid, locationOnTheEarth, astronomicalObject) - calculateActualCenterHeightRad(mid, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad);
                        if (currentJudgeHeight > 0.0) {
                            lo = mid;
                        } else {
                            hi = mid;
                        }
                    }
                    return lo.plus(Duration.between(lo, hi).dividedBy(2));
                }

                // Handle case 2.1
                // We can assume that @sign != 0 because if @sign = 0 we can assume @targetHourAngle never gets NaN, it is just 0.0
                double targetHourAngleAtStart = CoordinateConversion.calculateHourAngleCrossingHeightRad(
                        calculateActualCenterHeightRad(start, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad),
                        astronomicalObject.calculateDeclinationRad(start),
                        locationOnTheEarth.getLatitudeRad()
                ) * signOfHourAngle;
                if (! Double.isNaN(targetHourAngleAtStart)) {
                    estimate = start;
                    continue;
                }
                double targetHourAngleAtEnd = CoordinateConversion.calculateHourAngleCrossingHeightRad(
                        calculateActualCenterHeightRad(end, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad),
                        astronomicalObject.calculateDeclinationRad(end),
                        locationOnTheEarth.getLatitudeRad()
                ) * signOfHourAngle;
                if (! Double.isNaN(targetHourAngleAtEnd)) {
                    estimate = end;
                    continue;
                }

                // Line of the declinations at each of start, mid, and end does not cross the expected horizon.
                // If the declination has a peak on other time between start and end, which is peak in minus area in valley at plus area,
                // and if the object rises at that time, there may be an event.
                //
                // But declination of sun does not have peak in minus area or valley in plus area.
                // And for other planets, even if there is peak or valley and the astronomical object shows within the change of declination within 12 hours,
                // it would show a little bit within the change of declination, and go back again soon (rise and set soon or set and rise soon).
                //
                // This function assumes that @astronomicalObject does not move so dramatically in the equatorial coordinate system.
                // Therefore returning as this does not happen, rather than going to slow path.
                // Caller must use other functions like calculateAllEvents if this would be a problem.
                return null;
            }

            final TimePointOnTheEarth estimateTimePoint = new TimePointOnTheEarth(estimate);
            final double hourAngle = estimateTimePoint.calculateSiderealTimeRad(locationOnTheEarth.getLongitudeRad()) - astronomicalObject.calculateRightAscensionRad(estimate);

            double diffFromNow = targetHourAngle - hourAngle;
            diffFromNow -= 2.0 * Math.PI * Math.floor(diffFromNow / (2.0 * Math.PI));
            final double hourAnglePerDay = estimateTimePoint.estimatedIncrementOfSiderealTimeRadPerDay() - astronomicalObject.estimatedIncrementOfRightAscensionRadPerDay(estimate);

            final double diffFromNowSeconds = diffFromNow / hourAnglePerDay * 86400.0;

            Instant estimateNext = estimate.plusMillis((long) (diffFromNowSeconds * 1000.0));
            Instant estimatePrev = estimateNext.minusMillis((long) (2.0 * Math.PI / hourAnglePerDay * 86400000.0));

            if (hourAnglePerDay < 0.0) {
                estimatePrev = estimate.plusMillis((long) (diffFromNowSeconds * 1000.0));
                estimateNext = estimateNext.plusMillis((long) (2.0 * Math.PI / (-hourAnglePerDay) * 86400000.0));
            }

            if (estimateNext.isBefore(end) && estimatePrev.isAfter(start)) {
                if (Duration.between(estimatePrev, estimateAtStartOfThisLoop).compareTo(Duration.between(estimateAtStartOfThisLoop, estimateNext)) == -1) {
                    estimate = estimatePrev;
                } else {
                    estimate = estimateNext;
                }
            } else if (estimateNext.isBefore(end)) {
                estimate = estimateNext;
            } else if (estimatePrev.isAfter(start)) {
                estimate = estimatePrev;
            } else {
                // fall back to slow path
                break;
            }

            if (Math.abs(Duration.between(estimate, estimateAtStartOfThisLoop).toMillis()) < 200l) {
                return estimate;
            }
        }

        // Slow path
        Instant[] result = calculateAllEvents(astronomicalObject, eventDirectionType,
                start, end, Duration.ofMinutes(1), Duration.ofMillis(200),
                locationOnTheEarth, horizonByElevation, referencePoint, considerEquatorialHorizontalParallax, heightStandardRad);

        if (result.length == 0) {
            return null;
        } else {
            return result[0];
        }
    }

    /**
     * Return time of the all events from @start to @end in the correct order.<br>
     * This is slow function that iterates from @start to @end with the specified @interval.<br>
     * Thus, if there is a case like that the object rise and then set within @interval, the event may be missed.<br>
     *
     * @param astronomicalObject Target astronomical object
     * @param eventDirectionType Rise, set, or culmination
     * @param start Start point of calculation
     * @param end End point of calculation
     * @param interval Interval of calculation. If the object shows/hides only less than this interval, the event may be ignored.
     * @param precision Expected precision of calculation. If the expected error is less than precision, calculation is finished.
     * @param locationOnTheEarth Target location
     * @param horizonByElevation If set true, horizon is a bit below the horizontal 0 degrees, with considering horizontal of @locationOnTheEarth .
     * @param referencePoint Which point (top/center/bottom) of the @astronomicalObject should be the reference
     * @param considerEquatorialHorizontalParallax Whether equatorial horizontal parallax should be considered into the calculation. Set true if unsure.
     * @param heightStandardRad The standard height to be considered as rise/set, including refraction of the air.
     * @return Array of times of event. Empty if the event does not happen.
     * @throws AstronomicalPhenomenonComputationException
     * @throws UnsupportedDateRangeException
     */
    public static Instant[] calculateAllEvents(final AstronomicalObject astronomicalObject,
                                               final EventDirectionType eventDirectionType,
                                               final Instant start, final Instant end, final Duration interval, final Duration precision,
                                               final LocationOnTheEarth locationOnTheEarth,
                                               final boolean horizonByElevation, final ReferencePoint referencePoint,
                                               final boolean considerEquatorialHorizontalParallax,
                                               final double heightStandardRad) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        final double heightMeter = horizonByElevation ? locationOnTheEarth.getElevationMeters() : 0.0;

        ArrayList<Instant> ret = new ArrayList<>();

        final long twicePrevisionInNanos = precision.toNanos() * 2l;

        if (eventDirectionType == EventDirectionType.CULMINATION) {
            double prevHourAngle = 0.0;
            boolean isFirstOfTheLoop = true;
            Instant prevTime = null;
            for (Instant now = start.minus(interval); now.isBefore(end); now = now.plus(interval)) {
                final TimePointOnTheEarth nowTimePoint = new TimePointOnTheEarth(now);
                double hourAngle = nowTimePoint.calculateSiderealTimeRad(locationOnTheEarth.getLongitudeRad()) - astronomicalObject.calculateRightAscensionRad(now);
                hourAngle -= Math.floor(hourAngle / 2.0 / Math.PI) * 2.0 * Math.PI;

                if (! isFirstOfTheLoop) {
                    if ((hourAngle < 0.5 * Math.PI && prevHourAngle > 1.5 * Math.PI) ||
                            (hourAngle > 1.5 * Math.PI && prevHourAngle < 0.5 * Math.PI)) {

                        Instant hi = now;
                        Instant lo = prevTime;
                        double loHourAngle = prevHourAngle;

                        while (Math.abs(Duration.between(lo, hi).toNanos()) < twicePrevisionInNanos) {
                            final Instant mid = lo.plus(Duration.between(lo, hi).dividedBy(2));
                            double midHourAngle = nowTimePoint.calculateSiderealTimeRad(locationOnTheEarth.getLongitudeRad()) - astronomicalObject.calculateRightAscensionRad(now);
                            midHourAngle -= Math.floor(hourAngle / 2.0 / Math.PI) * 2.0 * Math.PI;

                            if ((midHourAngle <= Math.PI && loHourAngle > Math.PI) ||
                                    (midHourAngle > Math.PI && loHourAngle <= Math.PI)) {
                                hi = mid;
                            } else {
                                lo = mid;
                                loHourAngle = hourAngle;
                            }
                        }

                        ret.add(lo.plus(Duration.between(lo, hi).dividedBy(2)));
                    }
                }

                prevHourAngle = hourAngle;
                prevTime = now;
                isFirstOfTheLoop = false;
            }

        } else {
            int posReference = 0;
            if (referencePoint == ReferencePoint.TOP) {
                posReference = 1;
            } else if (referencePoint == ReferencePoint.CENTER) {
                posReference = 0;
            } else if (referencePoint == ReferencePoint.BOTTOM) {
                posReference = -1;
            }

            double signOfDirection = 0.0;
            if (eventDirectionType == EventDirectionType.RISE) {
                signOfDirection = 1.0;
            } else if (eventDirectionType == EventDirectionType.SET) {
                signOfDirection = -1.0;
            }

            Instant prevTime = null;
            double prevJudgeHeight = 0.0;
            boolean isFirstOfTheLoop = true;
            for (Instant now = start.minus(interval); now.isBefore(end); now = now.plus(interval)) {
                double nowJudgeHeight = (calculateHeightRad(now, locationOnTheEarth, astronomicalObject) - calculateActualCenterHeightRad(now, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad)) * signOfDirection;

                if (!isFirstOfTheLoop) {
                    if (prevJudgeHeight < 0.0 && nowJudgeHeight > 0.0) {
                        Instant hi = now;
                        Instant lo = prevTime;

                        while (Math.abs(Duration.between(lo, hi).toNanos()) < twicePrevisionInNanos) {
                            final Instant mid = lo.plus(Duration.between(lo, hi).dividedBy(2));
                            final double midJudgeHeight = (calculateHeightRad(start, locationOnTheEarth, astronomicalObject) - calculateActualCenterHeightRad(start, heightMeter, astronomicalObject, posReference, considerEquatorialHorizontalParallax, heightStandardRad)) * signOfDirection;

                            if (midJudgeHeight < 0.0) {
                                lo = mid;
                            } else {
                                hi = mid;
                            }
                        }
                        ret.add(lo.plus(Duration.between(lo, hi).dividedBy(2)));
                    }
                }

                prevTime = now;
                prevJudgeHeight = nowJudgeHeight;
                isFirstOfTheLoop = false;
            }
        }

        Instant[] retArray = new Instant[ret.size()];
        for (int i = 0; i < ret.size(); ++i) {
            retArray[i] = ret.get(i);
        }
        return retArray;
    }

    private static double calculateActualCenterHeightRad(Instant t, double heightMeters, AstronomicalObject astronomicalObject,
                                                         int pos, boolean considerEquatorialHorizontalParallax,
                                                         double heightStandardRad) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        double ret = heightStandardRad;

        ret -= Earth.calculateAltitudeCorrectionOfHorizonRad(heightMeters);
        if (pos != 0) {
            ret -= astronomicalObject.calculateApparentRadiusRad(t) * (double) pos;
        }
        if (considerEquatorialHorizontalParallax) {
            ret += astronomicalObject.calculateEquatorialHorizontalParallaxRad(t);
        }

        return ret;
    }

    private static double calculateHeightRad(Instant now, LocationOnTheEarth loc, AstronomicalObject astronomicalObject) throws UnsupportedDateRangeException, AstronomicalPhenomenonComputationException {
        double hourAngle = new TimePointOnTheEarth(now).calculateSiderealTimeRad(loc.getLongitudeRad()) - astronomicalObject.calculateRightAscensionRad(now);
        double declination = astronomicalObject.calculateDeclinationRad(now);
        return CoordinateConversion.calculateElevationRadFromHourAngle(hourAngle, declination, loc.getLatitudeRad());
    }
}
