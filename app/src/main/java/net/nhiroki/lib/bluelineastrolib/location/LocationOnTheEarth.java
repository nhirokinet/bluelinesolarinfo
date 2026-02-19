package net.nhiroki.lib.bluelineastrolib.location;


/**
 * LocationOnTheEarth describes the coordinate for astronomical calculation.
 */
public class LocationOnTheEarth {
    private double longitudeDeg;
    private double latitudeDeg;
    private double elevationMeters;

    /**
     * Constructor of LocationOnTheEarth.
     *
     * @param longitudeDeg Astronomical longitude in degrees from -180.0 to 180.0 (positive indicates east)
     * @param latitudeDeg Astronomical latitude in degrees from -90.0 to 90.0 (positive indicates north)
     * @param elevationMeters Elevation from horizon in meters
     */
    public LocationOnTheEarth(double longitudeDeg, double latitudeDeg, double elevationMeters) {
        this.longitudeDeg = longitudeDeg;
        this.latitudeDeg = latitudeDeg;
        this.elevationMeters = elevationMeters;
    }

    public double getLongitudeDeg() {
        return this.longitudeDeg;
    }

    public double getLongitudeRad() { return Math.toRadians(this.longitudeDeg); }

    public double getLatitudeDeg() {
        return this.latitudeDeg;
    }

    public double getLatitudeRad() {
        return Math.toRadians(this.latitudeDeg);
    }

    public double getElevationMeters() {
        return elevationMeters;
    }
}
