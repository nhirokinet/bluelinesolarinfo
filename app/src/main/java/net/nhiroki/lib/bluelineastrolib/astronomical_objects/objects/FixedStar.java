package net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;

import java.time.Instant;


/**
 * An astronomical object fixed on the equotorial coordinate system.
 */
public class FixedStar implements AstronomicalObject {
    private final double rightAscensionRad;
    private final double declinationRad;


    public FixedStar(double rightAscensionRad, double declinationRad) {
        this.rightAscensionRad = rightAscensionRad;
        this.declinationRad = declinationRad;
    }

    @Override
    public double calculateRightAscensionRad(Instant t) {
        return this.rightAscensionRad;
    }

    @Override
    public double calculateDeclinationRad(Instant t) {
        return this.declinationRad;
    }

    @Override
    public double calculateEquatorialHorizontalParallaxRad(Instant t) {
        return 0.0;
    }

    @Override
    public double calculateApparentRadiusRad(Instant t) {
        return 0.0;
    }

    @Override
    public double estimatedIncrementOfRightAscensionRadPerDay(Instant t) { return 0.0; }
}
