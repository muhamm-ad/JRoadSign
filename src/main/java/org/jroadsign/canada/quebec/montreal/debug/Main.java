package org.jroadsign.canada.quebec.montreal.debug;

import org.jroadsign.canada.quebec.montreal.RoadParkingSign;
import org.jroadsign.canada.quebec.montreal.RoadParkingSignException;
import org.jroadsign.canada.quebec.montreal.RoadSign;
import org.jroadsign.canada.quebec.montreal.RpaSign;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.RpaSignDescRule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign It contains methods to perform various debugging tasks related to road signs and parking
 *         rules. The class uses the RoadParkingSign and RoadSign classes to retrieve and process road signs and parking
 *         rules information. The class also provides methods to write the debugging results to output files and execute
 *         shell scripts.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String FILTERED_FILE = "src/main/resources/canada/quebec/montreal/input/Filtered_TypeS_RoadSign.geojson";
    private static final File OUTPUT_DIR = new File("src/main/resources/canada/quebec/montreal/output");
    private static final String SORT_UNIQ_SCRIPT_FILE = "src/main/java/org/jroadsign/canada/quebec/montreal/debug/sort_and_uniq.sh";

    static RoadParkingSign roadParkingSign = new RoadParkingSign();
    static TreeSet<RoadSign> roadSigns = new TreeSet<>(Comparator.comparing(RoadSign::getId));

    private static void executeScript(String scriptPath, String var1) {
        ProcessBuilder processBuilder = new ProcessBuilder(scriptPath, var1);
        processBuilder.redirectErrorStream(true);  // Optionally redirect error stream to standard output

        try {
            Process process = processBuilder.start();
            process.waitFor();

            // Optionally print the script output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Error executing script: " + e.getMessage());
        }
    }

    public static void debugRpaSignRules(SortedSet<RoadSign> roadSigns) {
        try (
                BufferedWriter writer_strDescription =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "strDescription.txt")));
                BufferedWriter writer_strDescriptionCleaned =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "strDescriptionCleaned.txt")));

                BufferedWriter writer_durationMinutes =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_durationMinutes.txt")));
                BufferedWriter writer_dailyTimeRanges =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_dailyTimeRanges.txt")));
                BufferedWriter writer_weeklyDays =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_weeklyDays.txt")));
                BufferedWriter writer_annualMonthRanges =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_annualMonthRanges.txt")));
                BufferedWriter writer_additionalInfos_1 =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_additionalInfos_1.txt")));
                BufferedWriter writer_additionalInfos_2 =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_additionalInfos_2.txt")));
        ) {
            for (RoadSign s : roadSigns) {
                RpaSign rpaSign = s.getRpaSign();
                String rpaCode = s.getRpaSign().getCode().getStr();

                String strDescription = rpaSign.getDescription().getStrDescription();
                writer_strDescription.write(rpaCode + "\t==>\t");
                writer_strDescription.write(strDescription + "\n");

                String strDescriptionCleaned = rpaSign.getDescription().getStrDescriptionCleaned();
                writer_strDescriptionCleaned.write(rpaCode + "\t==>\t");
                writer_strDescriptionCleaned.write(strDescriptionCleaned + "\n");

                StringBuilder additionalInfos = new StringBuilder();
                StringBuilder durationMinutes = new StringBuilder();
                StringBuilder dailyTimeRanges = new StringBuilder();
                StringBuilder days = new StringBuilder();
                StringBuilder annualMonthRanges = new StringBuilder();

                for (RpaSignDescRule rule : rpaSign.getDescription().getRpaSignDescRules()) {
                    durationMinutes.append(rule.getListDurationMinutes().isEmpty() ? "" : rule.getListDurationMinutes() + ";");
                    dailyTimeRanges.append(rule.getListDailyTimeRange().isEmpty() ? "" : rule.getListDailyTimeRange() + ";");
                    days.append(rule.getListDay().isEmpty() ? "" : rule.getListDay() + ";");
                    annualMonthRanges.append(rule.getListAnnualMonthRange().isEmpty() ? "" : rule.getListAnnualMonthRange() + ";");
                    additionalInfos.append(rule.getAdditionalMetaData() == null ? "" : rule.getAdditionalMetaData() + ";");
                }

                if (!durationMinutes.isEmpty() && durationMinutes.toString().split(";").length > 1) {
                    writer_durationMinutes.write(rpaCode + "\t==>\t");
                    writer_durationMinutes.write(durationMinutes + "\n");
                }
                if (!dailyTimeRanges.isEmpty() && dailyTimeRanges.toString().split(";").length > 1) {
                    writer_dailyTimeRanges.write(rpaCode + "\t==>\t");
                    writer_dailyTimeRanges.write(dailyTimeRanges + "\n");
                }
                if (!days.isEmpty() && days.toString().split(";").length > 1) {
                    writer_weeklyDays.write(rpaCode + "\t==>\t");
                    writer_weeklyDays.write(days + "\n");
                }
                if (!annualMonthRanges.isEmpty() && annualMonthRanges.toString().split(";").length > 1) {
                    writer_annualMonthRanges.write(rpaCode + "\t==>\t");
                    writer_annualMonthRanges.write(annualMonthRanges + "\n");
                }
                if (!additionalInfos.isEmpty()) {
                    // writer_additionalInfos_1.write(rpaCode + "\t==>\t");
                    writer_additionalInfos_1.write(additionalInfos + "\n");
                    writer_additionalInfos_2.write(rpaCode + "\t'" + strDescription + "'\t==>\t'" + additionalInfos + "'\n");
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }

        executeScript(SORT_UNIQ_SCRIPT_FILE, "src/main/resources/canada/quebec/montreal/output/RpaSignRule_*.txt");
        executeScript(SORT_UNIQ_SCRIPT_FILE, "src/main/resources/canada/quebec/montreal/output/strDescription*.txt");
    }

    private static void debugRpaSign(TreeSet<RoadSign> roadSigns) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter the code to search for sign (e.g., SV-QA) or type 'exit' to quit: ");
            String searchCode = scanner.nextLine().trim();

            if (searchCode.equalsIgnoreCase("exit")) break;

            List<RoadSign> matchingRoadSigns = roadSigns.stream()
                    .filter(s -> s.getRpaSign().getCode().getStr().equalsIgnoreCase(searchCode))
                    .distinct()
                    .toList();

            if (matchingRoadSigns.isEmpty()) {
                System.err.println("No road signs found with the code: " + searchCode);
            } else {
                System.out.print("{\"" + searchCode + "\" : [");
                for (RoadSign s : matchingRoadSigns) {
                    System.out.print(s.getRpaSign().toJson() + ",\n");
                }
                System.out.print("]}");
            }
        }
    }


    private static void debugRoadPosts(RoadParkingSign roadParkingSign) {
        File outJsonFile = new File(OUTPUT_DIR, "RoadPosts.json");
        try (BufferedWriter writerRpaSigns = new BufferedWriter(new FileWriter(outJsonFile))) {

            JSONObject outJson = new JSONObject();

            JSONObject manifest = new JSONObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String currentDateTime = LocalDateTime.now().format(formatter);
            manifest.put("generatedDate", currentDateTime);
            manifest.put("dateFormat", "--MM-DD");
            manifest.put("timezone", ZoneId.systemDefault().toString());
            manifest.put("generator", "JRoadSign"); // TODO : Use project name parameter
            manifest.put("schemaVersion", "1.0"); // TODO : Use version parameter
            manifest.put("description", "This file contains road posts description rules");

            JSONObject roadPosts = roadParkingSign.toJson();

            outJson.put("manifest", manifest);
            outJson.put("roadPosts", roadPosts);
            writerRpaSigns.write(outJson.toString(2));

        } catch (IOException e) {
            LOGGER.severe("Error writing to an output file: " + e.getMessage());
        }
    }

    private static void debugRoadSigns(RoadParkingSign roadParkingSign) {
        File outJsonFile = new File(OUTPUT_DIR, "RoadSigns.json");
        try (BufferedWriter writerRpaSigns = new BufferedWriter(new FileWriter(outJsonFile))) {

            roadSigns.clear();
            roadSigns.addAll(
                    roadParkingSign.getRoadPosts().values()
                            .stream()
                            .flatMap(post -> post.getRoadSigns().stream())
                            .collect(Collectors.toSet())
            );

            JSONObject outJson = new JSONObject();
            JSONArray roadSignsArray = new JSONArray();
            for (RoadSign roadSign : roadSigns) {
                roadSignsArray.put(new JSONObject(roadSign.toJson()));
            }
            outJson.put("roadSigns", roadSignsArray);
            writerRpaSigns.write(outJson.toString(2));

        } catch (IOException e) {
            LOGGER.severe("Error writing to an output file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdir();
        }

        try {
            roadParkingSign = new RoadParkingSign(new File(FILTERED_FILE));
        } catch (RoadParkingSignException e) {
            LOGGER.severe("Error getting roadPosts: " + e.getMessage());
        }

        debugRoadPosts(roadParkingSign);
        debugRoadSigns(roadParkingSign);

        debugRpaSignRules(roadSigns);
        // debugRpaSign(roadSigns);
    }

}
