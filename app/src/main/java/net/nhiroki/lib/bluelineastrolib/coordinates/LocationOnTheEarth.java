package net.nhiroki.lib.bluelineastrolib.coordinates;


/**
 * LocationOnTheEarth describes the coordinate for astronomical calculation.
 */
public class LocationOnTheEarth {
    private double longitudeDeg;
    private double latitudeDeg;
    private double elevationMeters;


    /**
     * Create LocationOnTheEarth instance with longitude and latitude in degrees and elevation in meters.
     *
     * @param longitudeDeg Astronomical longitude in degrees from -180.0 to 180.0 (positive indicates east)
     * @param latitudeDeg Astronomical latitude in degrees from -90.0 to 90.0 (positive indicates north)
     * @param elevationMeters Elevation from horizon in meters
     * @return LocationOnTheEarth instance with the specified coordinates
     */
    public static LocationOnTheEarth ofDegreesMeters(double longitudeDeg, double latitudeDeg, double elevationMeters) {
        return new LocationOnTheEarth(longitudeDeg, latitudeDeg, elevationMeters);
    }

    /**
     * Constructor of LocationOnTheEarth.
     *
     * @param longitudeDeg Astronomical longitude in degrees from -180.0 to 180.0 (positive indicates east)
     * @param latitudeDeg Astronomical latitude in degrees from -90.0 to 90.0 (positive indicates north)
     * @param elevationMeters Elevation from horizon in meters
     */
    private LocationOnTheEarth(double longitudeDeg, double latitudeDeg, double elevationMeters) {
        this.longitudeDeg = longitudeDeg;
        this.latitudeDeg = latitudeDeg;
        this.elevationMeters = elevationMeters;
    }

    public String toString() {
        return String.format("LocationOnTheEarth{longitudeDeg=%.6f, latitudeDeg=%.6f, elevationMeters=%.2f}", longitudeDeg, latitudeDeg, elevationMeters);
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

    public boolean equals(Object other) {
        if (other == null || !(other instanceof LocationOnTheEarth)) {
            return false;
        }
        LocationOnTheEarth otherLocation = (LocationOnTheEarth) other;
        return this.longitudeDeg == otherLocation.longitudeDeg &&
                this.latitudeDeg == otherLocation.latitudeDeg &&
                this.elevationMeters == otherLocation.elevationMeters;
    }
}
