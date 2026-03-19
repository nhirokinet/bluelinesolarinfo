package net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.coordinates.CelestialCoordinatesWithRightAscension;

import java.time.Instant;


/**
 * An astronomical object fixed on the equatorial coordinate system.
 */
public class FixedStar implements AstronomicalObject {
    private final CelestialCoordinatesWithRightAscension celestialCoordinates;


    public FixedStar(CelestialCoordinatesWithRightAscension celestialCoordinates) {
        this.celestialCoordinates = celestialCoordinates;
    }

    @Override
    public CelestialCoordinatesWithRightAscension calculateCelestialCoordinates(Instant t) {
        return this.celestialCoordinates;
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
