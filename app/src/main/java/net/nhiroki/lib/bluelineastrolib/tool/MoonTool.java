package net.nhiroki.lib.bluelineastrolib.tool;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Moon;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;

import java.time.Instant;


public class MoonTool {
    public static double calculateMoonPhaseDeg(Instant t) {
        double phase = new Moon().calculateEclipticLongitudeDeg(t) - new Sun().calculateEclipticLongitudeDeg(t);
        phase -= Math.floor(phase / 360.0) * 360.0;
        return phase;
    }

    public static Instant calculatePreviousTimeOfMoonPhaseByDeg(Instant t, double moonPhaseDeg) throws AstronomicalPhenomenonComputationException {
        double prevCorrectionMilliSeconds = 100.0 * 86400000.0;

        while (true) {
            double currentPhaseDiff = moonPhaseDiffDeg(t, moonPhaseDeg);

            double estimatedMilliSecondsBefore = currentPhaseDiff / 360.0 * 29.53 * 86400000.0;
            if (Math.abs(estimatedMilliSecondsBefore) > prevCorrectionMilliSeconds * 0.9) {
                // Regarding calculation method, this should never happen
                throw new AstronomicalPhenomenonComputationException("Unexpected computation result of moon phase correction");
            }
            prevCorrectionMilliSeconds = Math.abs(estimatedMilliSecondsBefore);
            t = t.minusMillis((long)(estimatedMilliSecondsBefore));

            if (Math.abs(estimatedMilliSecondsBefore) < 5000.0) {
                return t;
            }

            while (moonPhaseDiffDeg(t, moonPhaseDeg) > 180.0) {
                t = t.plusMillis((long)(estimatedMilliSecondsBefore / 3.0));
            }
        }
    }

    public static Instant calculateNextTimeOfMoonPhaseByDeg(Instant t, double moonPhaseDeg) throws AstronomicalPhenomenonComputationException {
        double prevCorrectionMilliSeconds = 100.0 * 86400000.0;

        while (true) {
            double currentPhaseDiff = 360.0 - moonPhaseDiffDeg(t, moonPhaseDeg);

            double estimatedMilliSecondsAfter = currentPhaseDiff / 360.0 * 29.53 * 86400000.0;
            if (Math.abs(estimatedMilliSecondsAfter) > prevCorrectionMilliSeconds * 0.9) {
                // Regarding calculation method, this should never happen
                throw new AstronomicalPhenomenonComputationException("Unexpected computation error");
            }
            prevCorrectionMilliSeconds = Math.abs(estimatedMilliSecondsAfter);
            t = t.plusMillis((long)(estimatedMilliSecondsAfter));

            if (estimatedMilliSecondsAfter < 5000.0) {
                return t;
            }

            while (moonPhaseDiffDeg(t, moonPhaseDeg) < 180.0) {
                t = t.minusMillis((long)(estimatedMilliSecondsAfter / 5.0));
            }
        }
    }

    private static double moonPhaseDiffDeg(Instant t, double moonPhaseDeg) {
        double currentPhaseDiff = MoonTool.calculateMoonPhaseDeg(t) - moonPhaseDeg;
        currentPhaseDiff -= Math.floor(currentPhaseDiff / 360.0) * 360.0;

        return currentPhaseDiff;
    }
}
