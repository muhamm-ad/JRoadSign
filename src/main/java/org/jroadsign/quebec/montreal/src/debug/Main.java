package org.jroadsign.quebec.montreal.src.debug;

import org.jroadsign.quebec.montreal.src.MontrealRoadPostSignsGeojsonReader;
import org.jroadsign.quebec.montreal.src.RoadSign;
import org.jroadsign.quebec.montreal.src.rpasign.RpaSign;
import org.jroadsign.quebec.montreal.src.rpasign.description.RoadSignDescCleaner;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDescParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.logging.Logger;


public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String FILTERED_FILE = "src/main/resources/quebec/montreal/input/Filtered_TypeS_RoadSign.geojson";
    private static final File OUTPUT_DIR = new File("src/main/resources/quebec/montreal/output");

    private static SortedMap<Long, RoadSign> prepareRoadSignData() {
        MontrealRoadPostSignsGeojsonReader montrealRoadPostSignsGeojsonReader =
                new MontrealRoadPostSignsGeojsonReader(FILTERED_FILE);
        SortedMap<Long, RoadSign> roadSigns = montrealRoadPostSignsGeojsonReader.getSigns();
        LOGGER.info("Number of road signs: " + roadSigns.size());
        return roadSigns;
    }

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

    public static void debugRpaSignDescription(SortedMap<Long, RoadSign> roadSigns) {
        // Create a map of writers and their corresponding getters from RpaSignDescParser
        Map<BufferedWriter, Function<RpaSignDescParser, String>> writersWithGetters = new HashMap<>();
        try (
                BufferedWriter writer =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc.txt")));
                BufferedWriter writer_durationInMinutes =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_durationMinutes.txt")));
                BufferedWriter writer_dayHours =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_dailyTimeRange.txt")));
                BufferedWriter writer_weekdays =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_weeklyDayRange.txt")));
                BufferedWriter writer_months =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_annualMonthRange.txt")));
                BufferedWriter writer_additionalMetaData =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_additionalInfo.txt")));
        ) {
            for (RoadSign s : roadSigns.values()) {
                RpaSign rpaSign = s.getRpaSign();
                String sDescription = rpaSign.getStringDescription();
                RpaSignDescParser rpaSignDes =
                        new RpaSignDescParser(RoadSignDescCleaner.cleanDescription(sDescription));

                //if (rpaSignDes.getAdditionalInfo() != null
                // && !rpaSignDes.getAdditionalInfo().isEmpty()) {// DEBUG: filter
                writer.write(sDescription + "\n\t==> ");
                writer.write(rpaSignDes + "\n");
                //}

                if (rpaSignDes.getDurationMinutes() != null && !rpaSignDes.getDurationMinutes().isEmpty()) {
                    writer_durationInMinutes.write(rpaSignDes.getDurationMinutes() + "\n");
                }
                if (rpaSignDes.getDailyTimeRange() != null && !rpaSignDes.getDailyTimeRange().isEmpty()) {
                    writer_dayHours.write(rpaSignDes.getDailyTimeRange() + "\n");
                }
                if (rpaSignDes.getWeeklyDayRange() != null && !rpaSignDes.getWeeklyDayRange().isEmpty()) {
                    writer_weekdays.write(rpaSignDes.getWeeklyDayRange() + "\n");
                }
                if (rpaSignDes.getAnnualMonthRange() != null && !rpaSignDes.getAnnualMonthRange().isEmpty()) {
                    writer_months.write(rpaSignDes.getAnnualMonthRange() + "\n");
                }
                if (rpaSignDes.getAdditionalInfo() != null && !rpaSignDes.getAdditionalInfo().isEmpty()) {
                    writer_additionalMetaData.write("(" + s.getRpaSign().getStringCode() + ")\t'" + sDescription + "'\t==>\t");
                    writer_additionalMetaData.write(rpaSignDes.getAdditionalInfo() + "\n");
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }
        // Define script path and arguments
        String scriptPath = "src/main/java/org/jroadsign/quebec/montreal/src/debug/sort_and_uniq.sh";
        String targetFiles = "src/main/resources/quebec/montreal/output/RpaSignDesc_*.txt";

        // Execute the script with arguments
        executeScript(scriptPath, targetFiles);
    }

    private static String formatLineRpaSing(String lineOfRpaSign) {
        return lineOfRpaSign
                .replace("id", "\n\tid")
                .replace("description", "\n\tdescription")
                .replace("stringDescription", "\n\t\tstringDescription")

                .replace("rpaSignDescRules", "\n\t\trpaSignDescRules")
                .replace("[RpaSignDescRule", "[\n\t\t\tRpaSignDescRule")
                .replace("}, RpaSignDescRule", "\n\t\t\t},\n\t\t\tRpaSignDescRule")

                .replace("durationMinutesList", "\n\t\t\t\tdurationMinutesList")
                .replace("dailyTimeRangeList", "\n\t\t\t\tdailyTimeRangeList")
                .replace("DailyTimeRange", "\n\t\t\t\t\t\tDailyTimeRange")
                .replace("weeklyDays", "\n\t\t\t\tweeklyDays")
                .replace("annualMonthRangeList", "\n\t\t\t\tannualMonthRangeList")
                .replace("AnnualMonthRange", "\n\t\t\t\t\t\tAnnualMonthRange")
                .replace("ruleAdditionalMetaData", "\n\t\t\t\truleAdditionalMetaData")

                .replace("}], additionalMetaData", "\n\t\t\t},\n\t\t]\n\t\tadditionalMetaData")
                .replace("}, code", "\n\t},\n\tcode")

                .replace("'}", "'\n}")
                .replace("],", "\n\t\t\t\t\t],")
                .replace("{", " {")
                .replace("]\n\t\tadditionalMetaData", "],\n\t\tadditionalMetaData")
                .replace("RpaSign{", "],\nRpaSign{")

                .replace("'null'", "null");
    }

    private static void debugRoadSign(SortedMap<Long, RoadSign> roadSigns) {
        try (
                BufferedWriter writerRoadSigns = new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RoadSigns.txt")));
                BufferedWriter writerRpaSigns = new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSigns.txt")));
        ) {
            for (RoadSign s : roadSigns.values()) {

//                RpaSignDesc rpaSignDesc = s.getRpaSign().getDescription();
//                boolean boolWrite = false;
//                for (RpaSignDescRule rules : rpaSignDesc.getRpaSignDescRules()) {
//                    //int numDailyTimeRanges = rules.getDailyTimeRangeList().size();
//                    //if (numDailyTimeRanges > 1 && (rules.getWeeklyDays() != null && !rules.getWeeklyDays().isEmpty())) {
//                        boolWrite = true;
//                    //}
//                }
//                if (boolWrite) {
                if (s.getRpaSign().getStringCode().equalsIgnoreCase("SLR-ST-111")) {
                    writerRoadSigns.write(s + "\n");
                    RpaSign rpaSign = s.getRpaSign();
                    writerRpaSigns.write(formatLineRpaSing(rpaSign.toString()) + "\n");
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        // This main method is for debugging purposes.
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdir();
        }

        SortedMap<Long, RoadSign> roadSigns = prepareRoadSignData();

        debugRpaSignDescription(roadSigns);
        debugRoadSign(roadSigns);
    }

}
