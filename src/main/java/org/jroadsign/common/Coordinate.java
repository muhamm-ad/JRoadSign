package org.jroadsign.common;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.Objects;

/**
 * This class encapsulates a Point2D.Double and provide access
 * via <tt>lat</tt> and <tt>lon</tt>.
 */
public class Coordinate implements Serializable, Comparable<Coordinate> {
    private transient Point2D.Double data;

    public Coordinate(final double lat, final double lon) {
        data = new Point2D.Double(lon, lat);
    }

    public double getLat() {
        return data.y;
    }

    public void setLat(final double lat) {
        data.y = lat;
    }

    public double getLon() {
        return data.x;
    }

    public void setLon(final double lon) {
        data.x = lon;
    }

    public double distance(final Coordinate oCoor) {
        double deltaX = data.x - oCoor.data.x;
        double deltaY = data.y - oCoor.data.y;
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    public double distanceSq(final Coordinate oCoor) {
        double distance = distance(oCoor);
        return Math.pow(distance, 2);
    }

    public double calculateHaversineDistance(final Coordinate oCoor) {
        double earthRadius = 6378; // Earth's radius in kilometers
        double latitude1 = Math.toRadians(data.x);
        double latitude2 = Math.toRadians(oCoor.data.x);
        double deltaLatitude = Math.toRadians(oCoor.data.x - data.x);
        double deltaLongitude = Math.toRadians(oCoor.data.y - data.y);

        double a = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2) +
                Math.cos(latitude1) * Math.cos(latitude2) *
                        Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    public double earthDistance(Coordinate coor) {
        double lon1 = Math.PI * data.y / 180.0;
        double lon2 = Math.PI * coor.data.y / 180.0;
        double lat1 = Math.PI * data.x / 180.0;
        double lat2 = Math.PI * coor.data.x / 180.0;

        double s1 = Math.sin((lat2 - lat1) / 2);
        double s2 = Math.sin((lon2 - lon1) / 2);

        return 2 * 6373 * Math.asin(Math.sqrt(s1 * s1 + Math.cos(lat1) * Math.cos(lat2) * s2 * s2));
    }

    @Serial
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(data.x);
        out.writeObject(data.y);
    }

    @Serial
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        data = new Point2D.Double();
        data.x = (Double) in.readObject();
        data.y = (Double) in.readObject();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate that)) return false;
        return Double.compare(that.getLat(), getLat()) == 0 &&
                Double.compare(that.getLon(), getLon()) == 0;
    }

    @Override
    public int compareTo(final Coordinate other) {
        int latComparison = Double.compare(getLat(), other.getLat());
        if (latComparison != 0) {
            return latComparison;
        } else {
            return Double.compare(getLon(), other.getLon());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "Coordinate[" + data.y + ", " + data.x + "]";
    }

}
