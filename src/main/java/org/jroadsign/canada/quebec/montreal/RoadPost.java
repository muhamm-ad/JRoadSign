package org.jroadsign.canada.quebec.montreal;

import org.jroadsign.common.Coordinate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 * @description Represents a road post with various attributes such as location, and road signs.
 */
public class RoadPost implements Comparable<RoadPost> {
    private final int version; // version of the post
    private final LocalDate conceptionDate; // Conception date of the post
    private final Boolean isOnStreet; // Indicates if the post is on the street
    private final String rtpDescription; // RTP description
    private final Coordinate coordinate; // Coordinate (WGS84) of the post
    private final String districtName; // District of the sign
    private final List<RoadSign> roadSigns;


    public RoadPost(int version, LocalDate conceptionDate, Boolean isOnStreet, String rtpDescription, String districtName,
                    Coordinate coordinate, List<RoadSign> roadSigns) {
        this.version = version;
        this.conceptionDate = conceptionDate;
        this.isOnStreet = isOnStreet;
        this.rtpDescription = rtpDescription;
        this.districtName = districtName;
        this.coordinate = coordinate;

        roadSigns.sort(Comparator.comparingInt(RoadSign::getPosition));
        this.roadSigns = roadSigns;
    }

    public int getVersion() {
        return version;
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

    public List<RoadSign> getRoadSigns() {
        return roadSigns;
    }

    public void addRoadSign(RoadSign roadSign) {
        roadSigns.add(roadSign);
    }

    public void removeRoadSign(RoadSign roadSign) {
        roadSigns.remove(roadSign);
    }

    @Override
    public int compareTo(RoadPost o) {
        return Comparator.comparingInt(RoadPost::getVersion)
                .thenComparing(RoadPost::getConceptionDate)
                .thenComparing(RoadPost::isOnStreet)
                .thenComparing(RoadPost::getRtpDescription)
                .thenComparing(RoadPost::getCoordinate)
                .thenComparing(RoadPost::getDistrictName)
                .thenComparing((rp) -> rp.getRoadSigns().size())
                .compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoadPost roadPost = (RoadPost) o;
        return version == roadPost.version &&
                Objects.equals(conceptionDate, roadPost.conceptionDate) &&
                Objects.equals(isOnStreet, roadPost.isOnStreet) &&
                Objects.equals(rtpDescription, roadPost.rtpDescription) &&
                Objects.equals(coordinate, roadPost.coordinate) &&
                Objects.equals(districtName, roadPost.districtName) &&
                Objects.equals(roadSigns, roadPost.roadSigns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, conceptionDate, isOnStreet, rtpDescription, coordinate, districtName, roadSigns);
    }

    @Override
    public String toString() {
        return "RoadPost{" +
                "version=" + version +
                ", conceptionDate=" + conceptionDate +
                ", isOnStreet=" + isOnStreet +
                ", rtpDescription='" + rtpDescription + '\'' +
                ", coordinate=" + coordinate +
                ", districtName='" + districtName + '\'' +
                ", roadSigns=" + roadSigns +
                '}';
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("version", this.version);
        json.put("conceptionDate", this.conceptionDate != null ? this.conceptionDate.toString() : JSONObject.NULL);
        json.put("isOnStreet", this.isOnStreet != null ? this.isOnStreet : JSONObject.NULL);
        json.put("rtpDescription", this.rtpDescription != null ? this.rtpDescription : JSONObject.NULL);
        json.put("coordinate", this.coordinate != null ? this.coordinate.toJson() : JSONObject.NULL);
        json.put("districtName", this.districtName != null ? this.districtName : JSONObject.NULL);

        JSONArray signsArray = new JSONArray();
        for (RoadSign sign : roadSigns) {
            signsArray.put(sign.toJson());
        }
        json.put("roadSigns", signsArray);

        return json;
    }

}
