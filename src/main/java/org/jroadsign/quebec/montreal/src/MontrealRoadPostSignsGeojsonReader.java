package org.jroadsign.quebec.montreal.src;

import org.jroadsign.common.Coordinate;
import org.jroadsign.quebec.montreal.src.rpasign.RpaSign;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The MontrealRoadPostSignsGeojsonReader class is responsible for reading a GeoJSON file containing parking signs
 * (of Montreal, Qc, Canada) and converting the data into a SortedMap of sign objects.
 */
public class MontrealRoadPostSignsGeojsonReader {

    private static final Logger LOGGER = Logger.getLogger(MontrealRoadPostSignsGeojsonReader.class.getName());

    TreeMap<Long, RoadPost> posts = new TreeMap<>();
    TreeMap<Long, RoadSign> signs = new TreeMap<>();


    /**
     * Reads a GeoJSON file containing road signs and returns them as a SortedMap.
     * The key of the map is the sign ID, and the value is a Sign object containing
     * all the information about the RoadSign.
     *
     * @param geoJsonFile The path to the GeoJSON file
     */
    public MontrealRoadPostSignsGeojsonReader(String geoJsonFile) {
        String jsonData;

        try (BufferedReader br = new BufferedReader(new FileReader(geoJsonFile))) {
            LOGGER.info(() -> "Starting to read the GeoJSON file: " + geoJsonFile);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            jsonData = sb.toString();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray features = jsonObject.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");

                getGeoJsonProperties(properties);
            }

            LOGGER.info(() -> "Finished reading the GeoJSON file: " + geoJsonFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                    () -> "IOException occurred while reading the GeoJSON file: " + geoJsonFile + ", " + e);
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE,
                    () -> "JSONException occurred while parsing the GeoJSON file: " + geoJsonFile + ", " + e);
        }
    }

    /**
     * Reads a RoadPost and RoadSign from a JSONObject and adds theme to a SortedMaps.
     * If the RoadPost already exists in the map (same ID), it adds the RoadSign to the existing RoadPost.
     *
     * @param properties The JSONObject containing the RoadPost's properties
     */
    private void getGeoJsonProperties(JSONObject properties) {
        long postId = properties.getLong("POTEAU_ID_POT");
        int signPosition = properties.getInt("POSITION_POP");
        long signId = properties.getLong("PANNEAU_ID_PAN");

        long signIdRpa = properties.getLong("PANNEAU_ID_RPA");
        String signDescRpa = properties.optString("DESCRIPTION_RPA", null);
        String signCodeRpa = properties.optString("CODE_RPA", null);

        RpaSign signRpa = new RpaSign(signIdRpa, signDescRpa, signCodeRpa);

        int signArrowCode = properties.getInt("FLECHE_PAN");
        String signToponymic = properties.optString("TOPONYME_PAN", null);
        String signCategoryDescription = properties.optString("DESCRIPTION_CAT", null);
        int postVersion = properties.getInt("POTEAU_VERSION_POT");

        String sDate = properties.getString("DATE_CONCEPTION_POT");
        LocalDate date = sDate.equalsIgnoreCase("NaT") ? null : LocalDate.parse(sDate);

        Boolean postNotOnStreet = properties.has("PAS_SUR_RUE") ?
                properties.optDouble("PAS_SUR_RUE", 0) != 0 : null;

        String signRepDescription = properties.optString("DESCRIPTION_REP", null);

        RoadSign roadSign = new RoadSign(signPosition, signId, signRpa, signArrowCode,
                signToponymic, signCategoryDescription, signRepDescription);
        signs.put(signId, roadSign);

        if (!posts.containsKey(postId)) {
            String postRtpDescription = properties.optString("DESCRIPTION_RTP", null);
            double postLat = properties.getDouble("Latitude");
            double postLon = properties.getDouble("Longitude");
            String postDistrictName = properties.optString("NOM_ARROND", null);

            List<RoadSign> listRoadSigns = new ArrayList<>();
            listRoadSigns.add(roadSign);
            posts.put(postId, new RoadPost(postId, postVersion, listRoadSigns,
                    date, postNotOnStreet, postRtpDescription, new Coordinate(postLat, postLon), postDistrictName));
        } else {
            RoadPost roadPost = posts.get(postId);
            if (!roadPost.hasSign(signId)) {
                roadPost.addSign(roadSign);
            }
        }
    }


    public TreeMap<Long, RoadPost> getPosts() {
        return posts;
    }

    public TreeMap<Long, RoadSign> getSigns() {
        return signs;
    }

}
