package org.jroadsign.canada.quebec.montreal;

import org.jroadsign.common.Coordinate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 * @description Responsible for reading a file containing parking signs (of Montréal, Qc, Canada) and converting
 *         the data into post and sign objects.
 */
public class RoadParkingSign {

    private static final Logger LOGGER = Logger.getLogger(RoadParkingSign.class.getName());

    private static final String GEOJSON_EXTENSION = ".geojson";
    private static final String JSON_EXTENSION = ".json";
    private static final String CSV_EXTENSION = ".csv";

    private static final String FEATURE_KEY = "features";
    private static final String PROPERTIES_KEY = "properties";

    private static final String SIGN_POSITION_KEY = "POSITION_POP";
    private static final String SIGN_ID_KEY = "PANNEAU_ID_PAN";
    private static final String SIGN_ID_RPA_KEY = "PANNEAU_ID_RPA";
    private static final String SIGN_DESC_RPA_KEY = "DESCRIPTION_RPA";
    private static final String SIGN_CODE_RPA_KEY = "CODE_RPA";
    private static final String SIGN_ARROW_CODE_KEY = "FLECHE_PAN";
    private static final String SIGN_TOPONYMIC_KEY = "TOPONYME_PAN";
    private static final String SIGN_CATEGORY_DESC_KEY = "DESCRIPTION_CAT";
    private static final String SIGN_REP_DESC_KEY = "DESCRIPTION_REP";

    private static final String POST_ID_KEY = "POTEAU_ID_POT";
    private static final String POST_VERSION_KEY = "POTEAU_VERSION_POT";
    private static final String POST_DATE_CONCEPTION_KEY = "DATE_CONCEPTION_POT";
    private static final String POST_NOT_ON_STREET_KEY = "PAS_SUR_RUE";
    private static final String POST_RTP_DESC_KEY = "DESCRIPTION_RTP";
    private static final String POST_LATITUDE_KEY = "Latitude";
    private static final String POST_LONGITUDE_KEY = "Longitude";
    private static final String DISTRICT_NAME_KEY = "NOM_ARROND";

    TreeMap<Long, RoadPost> roadPosts = new TreeMap<>();

    public RoadParkingSign() {
    }

    public RoadParkingSign(TreeMap<Long, RoadPost> roadPosts) {
        this.roadPosts = roadPosts;
    }

    /**
     * Represents a Road Parking Sign.
     *
     * @param file the file to be read and processed
     * @throws RoadParkingSignException if an unknown file type is encountered, or if an IOException occurs while
     *                                  reading the file
     * @note Authorize files are :
     */
    public RoadParkingSign(File file) throws RoadParkingSignException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String fileName = file.getName();

            if (fileName.endsWith(GEOJSON_EXTENSION) || fileName.endsWith(JSON_EXTENSION)) {
                parseJsonFile(bufferedReader);
            } else if (fileName.endsWith(CSV_EXTENSION)) {
                parseCsvFile(bufferedReader);
            } else {
                throw new RoadParkingSignException("Unknown file type");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                    e, () -> "IOException occurred while reading the file: " + file.getPath());
        }
    }

    private void parseCsvFile(BufferedReader bufferedReader) {
        // TODO: Implement CSV parsing logic
    }

    private void parseJsonFile(BufferedReader bufferedReader) throws IOException {
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line);
        }

        JSONObject jsonObject = new JSONObject(content.toString());
        JSONArray features = jsonObject.getJSONArray(FEATURE_KEY);

        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = features.getJSONObject(i);
            JSONObject properties = feature.getJSONObject(PROPERTIES_KEY);
            extractGeoJsonProperties(properties);
        }
    }

    private void extractGeoJsonProperties(JSONObject properties) {
        int signPosition = properties.getInt(SIGN_POSITION_KEY);
        long signId = properties.getLong(SIGN_ID_KEY);
        long signIdRpa = properties.getLong(SIGN_ID_RPA_KEY);
        String signDescRpa = properties.optString(SIGN_DESC_RPA_KEY, null);
        String signCodeRpa = properties.optString(SIGN_CODE_RPA_KEY, null);
        int signArrowCode = properties.getInt(SIGN_ARROW_CODE_KEY);
        String signToponymic = properties.optString(SIGN_TOPONYMIC_KEY, null);
        String signCategoryDescription = properties.optString(SIGN_CATEGORY_DESC_KEY, null);
        String signRepDescription = properties.optString(SIGN_REP_DESC_KEY, null);
        RpaSign signRpa = new RpaSign(signIdRpa, signCodeRpa, signDescRpa);

        RoadSign roadSign = new RoadSign(
                signPosition, signId, signRpa, signArrowCode,
                signToponymic, signCategoryDescription, signRepDescription
        );

        long postId = properties.getLong(POST_ID_KEY);

        if (!roadPosts.containsKey(postId)) {
            int postVersion = properties.getInt(POST_VERSION_KEY);
            String postStrDate = properties.getString(POST_DATE_CONCEPTION_KEY);
            LocalDate postConceptionDate = "NaT".equalsIgnoreCase(postStrDate) ? null : LocalDate.parse(postStrDate);
            Boolean postIsOnStreet = properties.has(POST_NOT_ON_STREET_KEY) /*&& properties.optDouble(POST_NOT_ON_STREET_KEY, 0) == 0*/;
            String postRtpDescription = properties.optString(POST_RTP_DESC_KEY, null);
            Coordinate postCoordinate = new Coordinate(properties.getDouble(POST_LATITUDE_KEY), properties.getDouble(POST_LONGITUDE_KEY));
            String postDistrictName = properties.optString(DISTRICT_NAME_KEY, null);

            List<RoadSign> listRoadSigns = new ArrayList<>();
            listRoadSigns.add(roadSign);

            roadPosts.put(postId, new RoadPost(postVersion, postConceptionDate, postIsOnStreet,
                    postRtpDescription, postDistrictName, postCoordinate, listRoadSigns)
            );
        } else {
            RoadPost roadPost = roadPosts.get(postId);
            if (roadPost.getRoadSigns().contains(roadSign)) {
                roadPost.addRoadSign(roadSign);
            }
        }
    }

    public TreeMap<Long, RoadPost> getRoadPosts() {
        return roadPosts;
    }

    @Override
    public String toString() {
        return "RoadParkingSign{" +
                "roadPosts=" + roadPosts +
                '}';
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        JSONArray roadPostsArray = new JSONArray();
        for (Map.Entry<Long, RoadPost> entry : roadPosts.entrySet()) {
            JSONObject roadPostJson = new JSONObject();
            roadPostJson.put("id", entry.getKey().toString());
            roadPostJson.put("roadPost", entry.getValue().toJson());
            roadPostsArray.put(roadPostJson);
        }
        json.put("roadPosts", roadPostsArray);

        return json;
    }
}
