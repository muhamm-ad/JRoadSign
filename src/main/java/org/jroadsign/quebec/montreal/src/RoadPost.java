// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src;

import org.jroadsign.common.Coordinate;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a RoadPos object with various details such as ID, version, conception date,
 * coordinates, signs etc.
 */
public class RoadPost implements Comparable<RoadPost> {
    private final long id; // ID of the post
    private final int postVersion; // Version of the post
    private final LocalDate conceptionDate; // Conception date of the post
    private final Boolean isOnStreet; // Indicates if the post is on the street
    private final String rtpDescription; // RTP description
    private final Coordinate coordinate; // Coordinate (WGS84) of the post
    private final String districtName; // District of the sign
    private final List<RoadSign> roadSigns;


    public RoadPost(long id, int postVersion, List<RoadSign> roadSigns, LocalDate conceptionDate,
                    Boolean isOnStreet, String rtpDescription, Coordinate coordinate, String districtName) {
        this.id = id;
        this.postVersion = postVersion;
        this.conceptionDate = conceptionDate;
        this.isOnStreet = isOnStreet;
        this.rtpDescription = rtpDescription;
        this.coordinate = coordinate;
        this.districtName = districtName;

        roadSigns.sort(Comparator.comparingInt(RoadSign::getPosition));
        this.roadSigns = roadSigns;
    }

    public long getId() {
        return id;
    }

    public int getPostVersion() {
        return postVersion;
    }

    public LocalDate getConceptionDate() {
        return conceptionDate;
    }

    public Boolean isOnStreet() {
        return isOnStreet;
    }

    public String getRtpDescription() {
        return rtpDescription;
    }

    public String getDistrictName() {
        return districtName;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public List<RoadSign> getSigns() {
        return roadSigns;
    }

    public int getNumRealSigns() {
        int count = 0;
        for (RoadSign roadSign : roadSigns) {
            count += roadSign.isReal() ? 1 : 0;
        }
        return count;
    }

    public boolean hasSign(String codeRpa) {
        for (RoadSign roadSign : roadSigns) {
            if (roadSign.getRpaSign().getStringCode().equalsIgnoreCase(codeRpa))
                return true;
        }
        return false;
    }

    public boolean hasSign(long id) {
        for (RoadSign roadSign : roadSigns) {
            if (roadSign.getId() == id)
                return true;
        }
        return false;
    }

    @Override
    public int compareTo(RoadPost otherRoadPost) {
        return Long.compare(this.id, otherRoadPost.id);
    }


    /**
     * Adds a new sign to the post.
     *
     * @param roadSign the sign to be added
     */
    public void addSign(RoadSign roadSign) {
        roadSigns.add(roadSign);
    }

    /**
     * Removes a sign from the post.
     *
     * @param roadSign the sign to be removed
     */
    public void removeSign(RoadSign roadSign) {
        roadSigns.remove(roadSign);
    }

    @Override
    public String toString() {
        return "RoadPos{" +
                "id=" + id +
                ", postVersion=" + postVersion +
                ", conceptionDate=" + conceptionDate +
                ", isOnStreet=" + isOnStreet +
                ", districtName='" + districtName + '\'' +
                ", coordinate=" + coordinate +
                ", signs=" + roadSigns +
                '}';
    }

}
