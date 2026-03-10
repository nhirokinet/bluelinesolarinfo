package net.nhiroki.lib.bluelineastrolib.tool;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;

import java.time.Instant;

public class SunTool {
    public static Instant calculateNextTimeOfEclipticLongitudeDeg(Instant from, double eclipticLongitudeDeg) throws AstronomicalPhenomenonComputationException {
        Sun sun = new Sun();

        Instant now = from;
        boolean isFirstLoop = true;

        double diffAbsPrev = 3600.0;

        while (true) {
            double currentEclipticLongitudeDeg = sun.calculateEclipticLongitudeDeg(now);
            double diff = eclipticLongitudeDeg - currentEclipticLongitudeDeg;
            if (diff < -180.0) {
                diff += 360.0;
            }

            if (isFirstLoop) {
                if (diff < 0.0) {
                    diff += 360.0;
                }
            }
            isFirstLoop = false;

            if (Math.abs(diff) > diffAbsPrev * 0.9) {
                // Regarding calculation method, this should never happen
                throw new AstronomicalPhenomenonComputationException("Unexpected computation error");
            }

            diffAbsPrev = Math.abs(diff);

            long estimatedMilliSecondsAfter = (long)(diff / 360.0 * 365.25 * 86400000.0);
            now = now.plusMillis(estimatedMilliSecondsAfter);

            if (Math.abs(estimatedMilliSecondsAfter) < 1000l) {
                return now;
            }
        }
    }
}
